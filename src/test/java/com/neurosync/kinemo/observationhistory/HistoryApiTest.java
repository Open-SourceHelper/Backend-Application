package com.neurosync.kinemo.observationhistory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neurosync.kinemo.observationhistory.application.ClosedAlertLookup;
import com.neurosync.kinemo.observationhistory.application.HistoryAccess;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:history;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureMockMvc
class HistoryApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockitoBean HistoryAccess access;
    @MockitoBean ClosedAlertLookup alerts;

    @Test
    void familyRegistersAndAssignedPsychologistReviewsWhileOtherAccountsCannotWrite() throws Exception {
        var child = UUID.randomUUID().toString();
        var path = episodes(child);
        when(access.roleFor("family", child)).thenReturn(Optional.of(HistoryAccess.Role.FAMILY));
        when(access.roleFor("psychologist", child)).thenReturn(Optional.of(HistoryAccess.Role.PSYCHOLOGIST));
        when(access.roleFor("caregiver", child)).thenReturn(Optional.of(HistoryAccess.Role.CAREGIVER));

        mvc.perform(get(path).header("X-Actor-Id", "family"))
                .andExpect(status().isUnauthorized());

        MvcResult created = mvc.perform(post(path).principal(() -> "family")
                .contentType("application/json").content(payload(null, Instant.now().minusSeconds(20))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.caregiverId").value("family"))
                .andExpect(jsonPath("$.crisisSeverity").value("mild"))
                .andReturn();
        String item = path + "/" + json.readTree(created.getResponse().getContentAsString()).get("id").asText();

        mvc.perform(get(path).principal(() -> "caregiver"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].description").value("Cambio de actividad"));
        when(access.roleFor("caregiver", child)).thenReturn(Optional.empty());
        mvc.perform(get(item).principal(() -> "caregiver")).andExpect(status().isForbidden());

        mvc.perform(post(item + "/comments").principal(() -> "family")
                .contentType("application/json").content("{\"text\":\"Comentario\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post(item + "/comments").principal(() -> "psychologist")
                .contentType("application/json").content("{\"text\":\"Revisión profesional\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].authorId").value("psychologist"));
        mvc.perform(put(item + "/review").principal(() -> "psychologist"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reviewedBy").value("psychologist"));
        mvc.perform(get(item).principal(() -> "family"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reviewedAt").exists())
                .andExpect(jsonPath("$.comments[0].text").isNotEmpty());

        String otherChild = UUID.randomUUID().toString();
        when(access.roleFor("family", otherChild)).thenReturn(Optional.of(HistoryAccess.Role.FAMILY));
        mvc.perform(get(item.replace(child, otherChild)).principal(() -> "family"))
                .andExpect(status().isNotFound());
    }

    @Test
    void onlyClosedAlertsCanBeDocumentedAndOnlyOnce() throws Exception {
        String child = UUID.randomUUID().toString();
        String alert = UUID.randomUUID().toString();
        String path = episodes(child);
        when(access.roleFor("caregiver", child)).thenReturn(Optional.of(HistoryAccess.Role.CAREGIVER));
        String body = payload(alert, Instant.now().minusSeconds(20));

        mvc.perform(post(path).principal(() -> "caregiver").contentType("application/json").content(body))
                .andExpect(status().isConflict());
        when(alerts.isClosedForChild(alert, child)).thenReturn(true);
        mvc.perform(post(path).principal(() -> "caregiver").contentType("application/json").content(body))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.alertId").value(alert));
        mvc.perform(post(path).principal(() -> "caregiver").contentType("application/json").content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsFutureDatesAndInvalidPageSize() throws Exception {
        String child = UUID.randomUUID().toString();
        String path = episodes(child);
        when(access.roleFor("family", child)).thenReturn(Optional.of(HistoryAccess.Role.FAMILY));
        mvc.perform(post(path).principal(() -> "family").contentType("application/json")
                .content(payload(null, Instant.now().plusSeconds(60))))
                .andExpect(status().isBadRequest());
        mvc.perform(get(path).principal(() -> "family").param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void publishesHistoryInOpenApi() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/children/{childId}/episodes']").exists());
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    private String episodes(String childId) {
        return "/api/v1/children/" + childId + "/episodes";
    }

    private String payload(String alertId, Instant time) {
        return """
                {"description":"Cambio de actividad","occurredAt":"%s","crisisSeverity":"mild",
                 "durationMinutes":5,"trigger":"Ruido","helped":"Pausa"%s}
                """.formatted(time, alertId == null ? "" : ",\"alertId\":\"" + alertId + "\"");
    }
}
