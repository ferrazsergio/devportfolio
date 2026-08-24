package dev.devportfolio.project.presentation;

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
class ProjectControllerIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndListsFeaturedProject() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "proj-owner@example.com");

        mockMvc.perform(post("/api/v1/projects")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"DevPortfolio\",\"slug\":\"devportfolio\",\"status\":\"IN_PROGRESS\","
                                + "\"featured\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("devportfolio"));

        mockMvc.perform(get("/api/v1/projects?featured=true").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("DevPortfolio"));
    }

    @Test
    void rejectsDuplicateSlugForSameOwner() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "proj-dup@example.com");

        mockMvc.perform(post("/api/v1/projects")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"slug\":\"meu-projeto\",\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/projects")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"B\",\"slug\":\"meu-projeto\",\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsInvalidSlugFormat() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "proj-invalid@example.com");

        mockMvc.perform(post("/api/v1/projects")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"slug\":\"Slug Inválido!\",\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ownerCannotDeleteAnotherOwnersProject() throws Exception {
        MockHttpSession ownerSession = registerAndLogin(mockMvc, "proj-owner2@example.com");
        MvcResult created = mockMvc.perform(post("/api/v1/projects")
                        .session(ownerSession)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"slug\":\"projeto-a\",\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        MockHttpSession intruderSession = registerAndLogin(mockMvc, "proj-intruder@example.com");

        mockMvc.perform(delete("/api/v1/projects/" + id).session(intruderSession).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
