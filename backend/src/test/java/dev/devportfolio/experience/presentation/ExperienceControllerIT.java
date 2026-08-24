package dev.devportfolio.experience.presentation;

import static dev.devportfolio.testsupport.TestAuthSupport.registerAndLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
class ExperienceControllerIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsExperienceReferencingOwnedSkill() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "exp-owner@example.com");
        String skillId = createSkill(session, "Java");

        mockMvc.perform(post("/api/v1/experiences")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"company\":\"Acme\",\"role\":\"Dev\",\"startDate\":\"2020-01-01\","
                                + "\"current\":true,\"technologyIds\":[\"%s\"]}").formatted(skillId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.company").value("Acme"))
                .andExpect(jsonPath("$.technologyIds[0]").value(skillId));
    }

    @Test
    void rejectsCurrentExperienceWithEndDate() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "exp-invalid@example.com");

        mockMvc.perform(post("/api/v1/experiences")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"company\":\"Acme\",\"role\":\"Dev\",\"startDate\":\"2020-01-01\","
                                + "\"endDate\":\"2021-01-01\",\"current\":true}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsUnknownTechnologyId() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "exp-badskill@example.com");
        String randomSkillId = java.util.UUID.randomUUID().toString();

        mockMvc.perform(post("/api/v1/experiences")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"company\":\"Acme\",\"role\":\"Dev\",\"startDate\":\"2020-01-01\","
                                + "\"current\":true,\"technologyIds\":[\"%s\"]}").formatted(randomSkillId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void reordersExperiencesForCaller() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "exp-reorder@example.com");
        String firstId = createExperience(session, "Acme");
        String secondId = createExperience(session, "Beta");

        mockMvc.perform(patch("/api/v1/experiences/reorder")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderedIds\":[\"%s\",\"%s\"]}".formatted(secondId, firstId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/experiences").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].company").value("Beta"))
                .andExpect(jsonPath("$[1].company").value("Acme"));
    }

    @Test
    void ownerCannotDeleteAnotherOwnersExperience() throws Exception {
        MockHttpSession ownerSession = registerAndLogin(mockMvc, "exp-owner2@example.com");
        String experienceId = createExperience(ownerSession, "Acme");

        MockHttpSession intruderSession = registerAndLogin(mockMvc, "exp-intruder@example.com");

        mockMvc.perform(delete("/api/v1/experiences/" + experienceId).session(intruderSession).with(csrf()))
                .andExpect(status().isNotFound());
    }

    private String createSkill(MockHttpSession session, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/skills")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"%s\",\"category\":\"BACKEND\"}".formatted(name)))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }

    private String createExperience(MockHttpSession session, String company) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/experiences")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"company\":\"%s\",\"role\":\"Dev\",\"startDate\":\"2020-01-01\","
                                + "\"current\":true}").formatted(company)))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id");
    }
}
