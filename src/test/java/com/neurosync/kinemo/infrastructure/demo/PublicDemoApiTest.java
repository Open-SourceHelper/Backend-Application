package com.neurosync.kinemo.infrastructure.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "kinemo.public-demo.frontend-origin=https://kinemo.example.test")
@AutoConfigureMockMvc
@ActiveProfiles("public-demo")
class PublicDemoApiTest {
    @Autowired MockMvc mvc;

    @Test
    void returnsSyntheticHistoryAndRoutinesWithoutAnAccount() throws Exception {
        mvc.perform(get("/api/v1/public-demo/health"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ok"));
        mvc.perform(get("/api/v1/public-demo/snapshot").header("Origin", "https://kinemo.example.test"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://kinemo.example.test"))
                .andExpect(jsonPath("$.source").value("spring-boot"))
                .andExpect(jsonPath("$.child.id").value("demo-mateo"))
                .andExpect(jsonPath("$.episodes.length()").value(3))
                .andExpect(jsonPath("$.routines[0].activities.length()").value(2));
        mvc.perform(get("/api/v1/public-demo/snapshot").header("Origin", "https://other.example.test"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsWritesAndKeepsLocalAndRealRoutesPrivate() throws Exception {
        mvc.perform(post("/api/v1/public-demo/snapshot").with(csrf()))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/children/1/episodes/1/review").with(csrf()))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/children").with(httpBasic("7001", "demo123")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/local-demo/session"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isForbidden());
    }
}
