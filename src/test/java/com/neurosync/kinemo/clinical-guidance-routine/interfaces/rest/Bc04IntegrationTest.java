package com.neurosync.kinemo.clinicalguidanceroutine.interfaces.rest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("local")
class Bc04IntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Test void parentCreatesAndCaregiverExecutesRoutine() throws Exception {
        String body=mvc.perform(post("/api/v1/routines")
                    .with(httpBasic("parent-demo-001","demo123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                    {"childId":"child-001","title":"Rutina de mañana","scheduledDate":"2026-11-13","activities":[{"title":"Prepararse","position":1,"durationMinutes":10}]}
                    """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.childId").value("child-001"))
                .andReturn().getResponse().getContentAsString();
        String id=mapper.readTree(body).path("id").asText();
        mvc.perform(get("/api/v1/routines/{id}",id).with(httpBasic("caregiver-alert-001","demo123")))
                .andExpect(status().isOk());
        String updated=mvc.perform(post("/api/v1/routines/{id}/activities",id)
                    .with(httpBasic("parent-demo-001","demo123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                    {"title":"Desayuno","position":2,"durationMinutes":20}
                    """))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String activityId=mapper.readTree(updated).path("activities").get(0).path("id").asText();
        mvc.perform(patch("/api/v1/routines/{id}/activities/{a}/complete",id,activityId)
                    .with(httpBasic("caregiver-alert-002","demo123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.activities[0].status").value("COMPLETED"));
        mvc.perform(get("/api/v1/routines").param("childId","child-other")
                    .with(httpBasic("caregiver-alert-001","demo123")))
                .andExpect(status().isForbidden());
    }
    @Test void professionalPublishesGuidanceAndCaregiverOnlySeesPublished() throws Exception {
        String created=mvc.perform(post("/api/v1/clinical-guidelines")
                    .with(httpBasic("psychologist-guide-001","demo123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                    {"childId":"child-001","title":"Guía personalizada","instructions":"Texto acordado durante consulta."}
                    """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();
        String id=mapper.readTree(created).path("id").asText();
        mvc.perform(get("/api/v1/clinical-guidelines/{id}",id)
                    .with(httpBasic("caregiver-alert-001","demo123"))).andExpect(status().isNotFound());
        mvc.perform(patch("/api/v1/clinical-guidelines/{id}/publish",id)
                    .with(httpBasic("psychologist-guide-001","demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PUBLISHED"));
        mvc.perform(get("/api/v1/clinical-guidelines/{id}",id)
                    .with(httpBasic("caregiver-alert-001","demo123"))).andExpect(status().isOk());
        mvc.perform(post("/api/v1/clinical-guidelines")
                    .with(httpBasic("caregiver-alert-001","demo123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                    {"childId":"child-001","title":"Prueba","instructions":"No autorizado"}
                    """))
                .andExpect(status().isForbidden());
    }
    @Test void creationRequiresActivitiesAndProfessionalOnlySeesAssignedPatients() throws Exception {
        mvc.perform(post("/api/v1/routines")
                    .with(httpBasic("parent-demo-001","demo123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                    {"childId":"child-001","title":"Sin pasos","scheduledDate":"2026-11-14","activities":[]}
                    """))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/patients")
                    .with(httpBasic("psychologist-guide-001","demo123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].childId").value("child-001"));
        mvc.perform(get("/api/v1/patients")
                    .with(httpBasic("caregiver-alert-001","demo123")))
                .andExpect(status().isForbidden());
    }
    @Test void requiresAuthentication() throws Exception {
        mvc.perform(get("/api/v1/routines").param("childId","child-001"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/practical-guides").param("childId","child-001")
                    .with(httpBasic("caregiver-alert-001","demo123")))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].published").value(true));
    }
}
