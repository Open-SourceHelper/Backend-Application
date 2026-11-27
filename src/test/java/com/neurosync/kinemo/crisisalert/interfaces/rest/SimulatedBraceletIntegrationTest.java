package com.neurosync.kinemo.crisisalert.interfaces.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class SimulatedBraceletIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    private static final String EVENTS = "/api/v1/crisis-alerts/simulated-bracelet-events";

    @Test void automaticEventCreatesAlertAndNotifiesOtherCaregiver() throws Exception {
        mvc.perform(post(EVENTS).with(httpBasic("caregiver-alert-001", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"child-001\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alert.status").value("ACTIVE"))
                .andExpect(jsonPath("$.alert.caregiverId").value("caregiver-alert-001"))
                .andExpect(jsonPath("$.alert.description").value("Pulsera simulada: evento de prueba detectado automáticamente."))
                .andExpect(jsonPath("$.queuedNotices").value(1));
        mvc.perform(get("/api/v1/crisis-alerts/notifications")
                        .with(httpBasic("caregiver-alert-002", "demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("QUEUED"));
    }

    @Test void manualAndAutomaticCannotCreateTwoOpenAlerts() throws Exception {
        mvc.perform(post("/api/v1/crisis-alerts").with(httpBasic("caregiver-alert-001", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"child-001\"}"))
                .andExpect(status().isCreated());
        mvc.perform(post(EVENTS).with(httpBasic("caregiver-alert-001", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"child-001\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.code").value("ALERT_ALREADY_OPEN"));
    }

    @Test void manualRemainsAvailableAfterAutomaticAlertIsClosed() throws Exception {
        String body = mvc.perform(post(EVENTS).with(httpBasic("caregiver-alert-001", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"child-001\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String id = mapper.readTree(body).path("alert").path("id").asText();
        mvc.perform(post("/api/v1/crisis-alerts").with(httpBasic("caregiver-alert-001", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"child-001\"}"))
                .andExpect(status().isConflict());
        mvc.perform(patch("/api/v1/crisis-alerts/" + id + "/attend")
                .with(httpBasic("caregiver-alert-002", "demo123"))).andExpect(status().isOk());
        mvc.perform(patch("/api/v1/crisis-alerts/" + id + "/close")
                .with(httpBasic("caregiver-alert-001", "demo123"))).andExpect(status().isOk());
        mvc.perform(post("/api/v1/crisis-alerts").with(httpBasic("caregiver-alert-001", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"childId\":\"child-001\",\"description\":\"SOS manual\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.alert.description").value("SOS manual"));
    }

    @Test void rejectsAnonymousUnauthorizedAndInvalidEvents() throws Exception {
        mvc.perform(post(EVENTS).contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"child-001\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post(EVENTS).with(httpBasic("psychologist-guide-001", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"child-001\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post(EVENTS).with(httpBasic("caregiver-alert-001", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"other-child\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post(EVENTS).with(httpBasic("caregiver-alert-001", "demo123"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"childId\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
