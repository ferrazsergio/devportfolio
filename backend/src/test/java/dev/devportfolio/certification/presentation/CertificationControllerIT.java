package dev.devportfolio.certification.presentation;

import static dev.devportfolio.testsupport.TestAuthSupport.registerAndLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CertificationControllerIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndListsCertification() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "cert-owner@example.com");

        mockMvc.perform(post("/api/v1/certifications")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"AWS SAA\",\"issuingOrganization\":\"AWS\",\"issueDate\":\"2023-01-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("AWS SAA"));

        mockMvc.perform(get("/api/v1/certifications").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].issuingOrganization").value("AWS"));
    }

    @Test
    void rejectsInvalidPayload() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "cert-invalid@example.com");

        mockMvc.perform(post("/api/v1/certifications")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ownerCannotDeleteAnotherOwnersCertification() throws Exception {
        MockHttpSession ownerSession = registerAndLogin(mockMvc, "cert-owner2@example.com");
        MvcResult created = mockMvc.perform(post("/api/v1/certifications")
                        .session(ownerSession)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"AWS SAA\",\"issuingOrganization\":\"AWS\",\"issueDate\":\"2023-01-01\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        MockHttpSession intruderSession = registerAndLogin(mockMvc, "cert-intruder@example.com");

        mockMvc.perform(delete("/api/v1/certifications/" + id).session(intruderSession).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
