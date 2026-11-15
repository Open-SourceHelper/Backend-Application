package com.neurosync.kinemo.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neurosync.kinemo.child.application.ChildProfiles;
import com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access.ProfessionalAssignmentJpaEntity;
import com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access.SpringDataProfessionalAssignmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:care-contexts;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class CareContextsIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired ChildProfiles children;
    @Autowired SpringDataProfessionalAssignmentRepository assignments;

    @Test
    void documentsAClosedAlertAndRespectsCaregiverRevocationAcrossContexts() throws Exception {
        var family = user("91").roles("PARENT");
        var caregiver = user("92").roles("CAREGIVER");
        var psychologist = user("93").roles("PSYCHOLOGIST");
        var created = mvc.perform(post("/api/v1/children").with(family).contentType("application/json")
                        .content("{\"firstName\":\"Mateo\",\"lastName\":\"Ejemplo\",\"birthDate\":\"2016-04-01\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse();
        var child = json.readTree(created.getContentAsString()).path("id").asText();
        var childPath = "/api/v1/children/" + child;
        var invited = mvc.perform(post(childPath + "/care-network/invitations").with(family)
                        .contentType("application/json")
                        .content("{\"email\":\"caregiver@example.test\",\"permissions\":[\"canReadChildProfile\"]}"))
                .andExpect(status().isCreated()).andReturn().getResponse();
        long invitation = json.readTree(invited.getContentAsString()).path("invitations").get(0).path("id").asLong();
        children.accept(92L, "caregiver@example.test", Long.valueOf(child), invitation);
        assignments.save(new ProfessionalAssignmentJpaEntity("assignment-integration", child, "93", true));

        mvc.perform(get("/api/v1/routines").param("childId", child).with(caregiver))
                .andExpect(status().isOk());
        var activated = mvc.perform(post("/api/v1/crisis-alerts").with(caregiver)
                        .contentType("application/json").content("{\"childId\":\"" + child + "\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse();
        var alert = json.readTree(activated.getContentAsString()).path("alert").path("id").asText();
        mvc.perform(patch("/api/v1/crisis-alerts/{id}/attend", alert).with(family))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ATTENDED"));
        mvc.perform(patch("/api/v1/crisis-alerts/{id}/close", alert).with(family))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CLOSED"));
        var episode = mvc.perform(post(childPath + "/episodes").with(family).contentType("application/json")
                        .content("""
                                {"description":"Cambio de actividad","occurredAt":"%s","crisisSeverity":"mild",
                                 "durationMinutes":5,"alertId":"%s"}
                                """.formatted(Instant.now().minusSeconds(10), alert)))
                .andExpect(status().isCreated()).andReturn().getResponse();
        var episodeId = json.readTree(episode.getContentAsString()).path("id").asText();
        var item = childPath + "/episodes/" + episodeId;
        mvc.perform(post(item + "/comments").with(caregiver).contentType("application/json")
                        .content("{\"text\":\"Comentario de prueba\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(put(item + "/review").with(psychologist)).andExpect(status().isOk());

        var member = children.network(91L, Long.valueOf(child)).getMembers().get(0).getId();
        mvc.perform(delete(childPath + "/care-network/members/{member}", member).with(family))
                .andExpect(status().isNoContent());
        mvc.perform(get(item).with(caregiver)).andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/routines").param("childId", child).with(caregiver))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/crisis-alerts/{id}", alert).with(caregiver))
                .andExpect(status().isNotFound());
    }

    @Test
    void sharesARealDemoChildAcrossAuthenticatedRoles() throws Exception {
        String url = "/api/v1/local-demo/session";
        mvc.perform(get(url)).andExpect(status().isUnauthorized());
        mvc.perform(get(url).with(httpBasic("parent-demo-001", "demo123"))).andExpect(status().isForbidden());
        var signedIn = mvc.perform(get(url).with(httpBasic("7001", "demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("FAMILY"))
                .andReturn().getResponse();
        long childId = json.readTree(signedIn.getContentAsString()).path("childId").asLong();
        mvc.perform(get(url).with(httpBasic("7002", "demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.childId").value(childId))
                .andExpect(jsonPath("$.role").value("CAREGIVER"));
        mvc.perform(get(url).with(httpBasic("7003", "demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.childId").value(childId))
                .andExpect(jsonPath("$.role").value("PSYCHOLOGIST"));
        mvc.perform(get("/api/v1/children/{id}", childId).with(httpBasic("7002", "demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.firstName").value("Mateo"));
    }

    @Test
    void swaggerListsAllContextsWhileApplicationEndpointsRequireAuthentication() throws Exception {
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.components.securitySchemes.basicAuth.scheme").value("basic"))
                .andExpect(jsonPath("$.paths['/api/v1/children']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/crisis-alerts']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/routines']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/children/{childId}/episodes']").exists());
        mvc.perform(get("/api/v1/children")).andExpect(status().isUnauthorized());
    }
}
