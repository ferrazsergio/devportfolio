package dev.devportfolio.portfolio.presentation;

import static dev.devportfolio.testsupport.TestAuthSupport.registerAndLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class ProfileControllerIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void profileIsAutoCreatedEmptyAtRegistration() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "profile-owner@example.com");

        mockMvc.perform(get("/api/v1/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").doesNotExist());
    }

    @Test
    void updatesProfile() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "profile-update@example.com");

        mockMvc.perform(put("/api/v1/profile")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Ana Souza\",\"username\":\"ana-souza\",\"headline\":\"Java dev\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ana-souza"))
                .andExpect(jsonPath("$.headline").value("Java dev"));
    }

    @Test
    void rejectsDuplicateUsernameAcrossPortfolios() throws Exception {
        MockHttpSession firstSession = registerAndLogin(mockMvc, "profile-first@example.com");
        mockMvc.perform(put("/api/v1/profile")
                        .session(firstSession)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Primeiro\",\"username\":\"mesmo-usuario\"}"))
                .andExpect(status().isOk());

        MockHttpSession secondSession = registerAndLogin(mockMvc, "profile-second@example.com");
        mockMvc.perform(put("/api/v1/profile")
                        .session(secondSession)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Segundo\",\"username\":\"mesmo-usuario\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsInvalidUsernameFormat() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "profile-invalid@example.com");

        mockMvc.perform(put("/api/v1/profile")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Ana\",\"username\":\"AB\"}"))
                .andExpect(status().isBadRequest());
    }
}
