package com.neurosync.kinemo.crisisalert.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neurosync.kinemo.child.application.ChildProfiles;
import com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access.*;
import com.neurosync.kinemo.crisisalert.domain.repository.CrisisAlertRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("local") @Transactional
class ProfessionalBraceletIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ChildProfiles children;
    @Autowired CrisisAlertRepository alerts;
    @Autowired SpringDataProfessionalAssignmentRepository assignments;
    private static final String ROOT = "/api/v1/crisis-alerts";
    private String child() { return children.list(7001L).getFirst().getId().toString(); }
    private String create() throws Exception {
        var response = mvc.perform(post(ROOT + "/simulated-bracelet-events").with(httpBasic("7001", "demo123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"" + child() + "\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(response).path("alert").path("id").asText();
    }
    private boolean isPending(String id, String actor) throws Exception {
        var body = mvc.perform(get(ROOT + "/professional-notifications").with(httpBasic(actor, "demo123")))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        for (var row : mapper.readTree(body)) if (id.equals(row.path("id").asText())) return true;
        return false;
    }
    @Test void storesStopAndConnectionHistoryWithoutDuplicatingRetriedEvents() throws Exception {
        for (String type : new String[]{"MONITORING_STARTED", "SIMULATION_STOPPED", "DISCONNECTED", "SIGNAL_MISSED", "RECONNECTED"}) {
            String id = UUID.randomUUID().toString();
            String json = "{\"childId\":\"" + child() + "\",\"eventId\":\"" + id + "\",\"type\":\"" + type + "\"}";
            for (int repeat = 0; repeat < 2; repeat++) mvc.perform(post(ROOT + "/bracelet-history")
                    .with(httpBasic("7001", "demo123")).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.actorId").value("7001"))
                    .andExpect(jsonPath("$.occurredAt").isNotEmpty());
        }
        mvc.perform(get(ROOT + "/bracelet-history").param("childId", child()).with(httpBasic("7003", "demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(5));
    }
    @Test void protectsHistoryFromAnonymousUnassignedAndProfessionalWrites() throws Exception {
        mvc.perform(get(ROOT + "/bracelet-history").param("childId", child())).andExpect(status().isUnauthorized());
        mvc.perform(get(ROOT + "/bracelet-history").param("childId", child())
                .with(httpBasic("psychologist-guide-001", "demo123"))).andExpect(status().isForbidden());
        mvc.perform(post(ROOT + "/bracelet-history").with(httpBasic("7003", "demo123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"" + child() + "\",\"eventId\":\"" + UUID.randomUUID() + "\",\"type\":\"SIMULATION_STOPPED\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post(ROOT + "/bracelet-history").with(httpBasic("7001", "demo123"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"other-child\",\"eventId\":\"" + UUID.randomUUID() + "\",\"type\":\"DISCONNECTED\"}"))
                .andExpect(status().isForbidden());
    }
    @Test void viewingDoesNotAcknowledgeAndConfirmingDoesNotCloseCrisis() throws Exception {
        var id = create();
        assertTrue(isPending(id, "7003"));
        mvc.perform(get(ROOT + "/professional-notifications/" + id).with(httpBasic("7003", "demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.childId").value(child()));
        assertTrue(isPending(id, "7003"));
        var first = mvc.perform(post(ROOT + "/professional-notifications/" + id + "/review")
                .with(httpBasic("7003", "demo123"))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var second = mvc.perform(post(ROOT + "/professional-notifications/" + id + "/review")
                .with(httpBasic("7003", "demo123"))).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertEquals(mapper.readTree(first).path("reviewedAt"), mapper.readTree(second).path("reviewedAt"));
        assertFalse(isPending(id, "7003"));
        assertEquals("ACTIVE", alerts.findById(UUID.fromString(id)).orElseThrow().status().name());
        mvc.perform(post(ROOT + "/professional-notifications/" + id + "/review")
                .with(httpBasic("7001", "demo123"))).andExpect(status().isNotFound());
    }
    @Test void currentAssignmentIsRequiredForEveryReadAndAcknowledgement() throws Exception {
        var id = create();
        assertTrue(isPending(id, "7003"));
        assignments.deleteById("local-angular-" + child());
        assignments.flush();
        assertFalse(isPending(id, "7003"));
        mvc.perform(get(ROOT + "/professional-notifications/" + id).with(httpBasic("7003", "demo123")))
                .andExpect(status().isNotFound());
        mvc.perform(post(ROOT + "/professional-notifications/" + id + "/review").with(httpBasic("7003", "demo123")))
                .andExpect(status().isNotFound());
    }
    @Test void closedAlertsRemainPendingAndEachProfessionalReviewsIndependently() throws Exception {
        assignments.saveAndFlush(new ProfessionalAssignmentJpaEntity("second-reviewer", child(), "psychologist-guide-001", true));
        var id = create();
        mvc.perform(patch(ROOT + "/" + id + "/attend").with(httpBasic("7001", "demo123"))).andExpect(status().isOk());
        mvc.perform(patch(ROOT + "/" + id + "/close").with(httpBasic("7001", "demo123"))).andExpect(status().isOk());
        assertTrue(isPending(id, "7003"));
        assertTrue(isPending(id, "psychologist-guide-001"));
        mvc.perform(post(ROOT + "/professional-notifications/" + id + "/review").with(httpBasic("7003", "demo123")))
                .andExpect(status().isOk());
        assertFalse(isPending(id, "7003"));
        assertTrue(isPending(id, "psychologist-guide-001"));
    }
}
