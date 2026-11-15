package com.neurosync.kinemo.child;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neurosync.kinemo.child.application.VerifiedChildAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:children;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@AutoConfigureMockMvc(addFilters = false)
class ChildApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void ownerRegistersChildAndOnlyVerifiedCaregiverCanAcceptUntilRevoked() throws Exception {
        var root = "/api/v1/children";
        mvc.perform(get(root).header("X-Actor-Id", "1")).andExpect(status().isUnauthorized());
        var created = mvc.perform(post(root).principal(() -> "1").contentType("application/json")
                .content("""
                        {"firstName":"Mateo","lastName":"Ejemplo","birthDate":"2016-04-01"}
                        """))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.parentId").value(1)).andReturn();
        var path = root + "/" + json.readTree(created.getResponse().getContentAsString()).get("id").asText();
        mvc.perform(put(path + "/clinical-profile").principal(() -> "1").contentType("application/json")
                .content("{\"triggers\":\"Ruido\"}")).andExpect(status().isOk());
        mvc.perform(get(path).principal(() -> "2")).andExpect(status().isForbidden());

        var network = path + "/care-network";
        var invite = mvc.perform(post(network + "/invitations").principal(() -> "1")
                .contentType("application/json")
                .content("{\"email\":\"caregiver@example.test\",\"permissions\":[\"canReadChildProfile\"]}"))
                .andExpect(status().isCreated()).andReturn();
        var invitation = json.readTree(invite.getResponse().getContentAsString()).get("invitations").get(0).get("id").asText();
        var accept = network + "/invitations/" + invitation + "/accept";
        mvc.perform(post(accept).principal(() -> "2").header("X-Email", "caregiver@example.test"))
                .andExpect(status().isForbidden());
        mvc.perform(post(accept).principal(new Account("2", "caregiver@example.test")))
                .andExpect(status().isNoContent());
        mvc.perform(get(path + "/clinical-profile").principal(() -> "2"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.triggers").value("Ruido"));
        var info = mvc.perform(get(network).principal(() -> "1")).andExpect(status().isOk()).andReturn();
        var member = json.readTree(info.getResponse().getContentAsString()).get("members").get(0).get("id").asText();
        mvc.perform(delete(network + "/members/" + member).principal(() -> "1")).andExpect(status().isNoContent());
        mvc.perform(get(path).principal(() -> "2")).andExpect(status().isForbidden());
    }

    private record Account(String accountId, String verifiedEmail) implements VerifiedChildAccount {
        @Override public String getName() { return accountId; }
    }
}
