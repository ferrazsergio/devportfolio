package dev.devportfolio.skill.presentation;

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
class SkillControllerIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndListsSkill() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "skill-owner@example.com");

        mockMvc.perform(post("/api/v1/skills")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Java\",\"category\":\"BACKEND\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Java"))
                .andExpect(jsonPath("$.category").value("BACKEND"));

        mockMvc.perform(get("/api/v1/skills").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java"));
    }

    @Test
    void rejectsDuplicateSkillNameForSameOwner() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "skill-dup@example.com");

        mockMvc.perform(post("/api/v1/skills")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Angular\",\"category\":\"FRONTEND\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/skills")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"angular\",\"category\":\"FRONTEND\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsInvalidPayload() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "skill-invalid@example.com");

        mockMvc.perform(post("/api/v1/skills")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/skills")).andExpect(status().isUnauthorized());
    }

    @Test
    void ownerCannotAccessAnotherOwnersSkill() throws Exception {
        MockHttpSession ownerSession = registerAndLogin(mockMvc, "skill-owner2@example.com");
        MvcResult created = mockMvc.perform(post("/api/v1/skills")
                        .session(ownerSession)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Kotlin\",\"category\":\"BACKEND\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String skillId = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        MockHttpSession intruderSession = registerAndLogin(mockMvc, "skill-intruder@example.com");

        mockMvc.perform(delete("/api/v1/skills/" + skillId).session(intruderSession).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
