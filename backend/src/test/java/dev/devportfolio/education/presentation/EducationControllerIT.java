package dev.devportfolio.education.presentation;

import static dev.devportfolio.testsupport.TestAuthSupport.registerAndLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
class EducationControllerIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsListsAndUpdatesEducation() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "edu-owner@example.com");

        MvcResult created = mockMvc.perform(post("/api/v1/educations")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"institution\":\"USP\",\"course\":\"Ciência da Computação\",\"degree\":\"Bacharelado\",\"startDate\":\"2015-01-01\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/v1/educations").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].institution").value("USP"));

        mockMvc.perform(put("/api/v1/educations/" + id)
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"institution\":\"USP\",\"course\":\"Engenharia\",\"degree\":\"Bacharelado\",\"startDate\":\"2015-01-01\",\"endDate\":\"2019-12-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course").value("Engenharia"));
    }

    @Test
    void rejectsInvalidPayload() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "edu-invalid@example.com");

        mockMvc.perform(post("/api/v1/educations")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"institution\":\"\",\"course\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ownerCannotUpdateAnotherOwnersEducation() throws Exception {
        MockHttpSession ownerSession = registerAndLogin(mockMvc, "edu-owner2@example.com");
        MvcResult created = mockMvc.perform(post("/api/v1/educations")
                        .session(ownerSession)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"institution\":\"USP\",\"course\":\"CC\",\"startDate\":\"2015-01-01\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        MockHttpSession intruderSession = registerAndLogin(mockMvc, "edu-intruder@example.com");

        mockMvc.perform(put("/api/v1/educations/" + id)
                        .session(intruderSession)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"institution\":\"Hackeado\",\"course\":\"CC\",\"startDate\":\"2015-01-01\"}"))
                .andExpect(status().isNotFound());
    }
}
