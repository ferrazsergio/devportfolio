package dev.devportfolio.publicpage.presentation;

import static dev.devportfolio.testsupport.TestAuthSupport.registerAndLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PublicPageControllerIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void draftPortfolioIsNotFoundPublicly() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "public-draft@example.com");
        mockMvc.perform(put("/api/v1/profile")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Draft User\",\"username\":\"draft-user\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/public/draft-user")).andExpect(status().isNotFound());
    }

    @Test
    void unknownUsernameIsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/public/never-registered")).andExpect(status().isNotFound());
    }

    @Test
    void publishedPortfolioIsServedPubliclyWithoutAuthentication() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "public-owner@example.com");

        mockMvc.perform(put("/api/v1/profile")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Ana Souza\",\"username\":\"ana-souza-pub\",\"headline\":\"Java dev\"}"))
                .andExpect(status().isOk());

        var skillResult = mockMvc.perform(post("/api/v1/skills")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Java\",\"category\":\"BACKEND\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String skillId = JsonPath.read(skillResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(post("/api/v1/experiences")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(("{\"company\":\"Acme\",\"role\":\"Dev\",\"startDate\":\"2020-01-01\","
                                + "\"current\":true,\"technologyIds\":[\"%s\"]}").formatted(skillId)))
                .andExpect(status().isCreated());

        // ainda DRAFT: não deve aparecer publicamente
        mockMvc.perform(get("/api/v1/public/ana-souza-pub")).andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/v1/portfolio/status")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PUBLISHED\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/public/ana-souza-pub"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.fullName").value("Ana Souza"))
                .andExpect(jsonPath("$.profile.headline").value("Java dev"))
                .andExpect(jsonPath("$.experiences[0].company").value("Acme"))
                .andExpect(jsonPath("$.experiences[0].technologies[0].name").value("Java"))
                .andExpect(jsonPath("$.skills[0].name").value("Java"));
    }

    @Test
    void publicEndpointIsCaseInsensitiveOnUsername() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "public-case@example.com");
        mockMvc.perform(put("/api/v1/profile")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Case User\",\"username\":\"case-user\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/api/v1/portfolio/status")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PUBLISHED\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/public/CASE-USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.fullName").value("Case User"));
    }
}
