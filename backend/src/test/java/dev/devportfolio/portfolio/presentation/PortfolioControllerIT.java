package dev.devportfolio.portfolio.presentation;

import static dev.devportfolio.testsupport.TestAuthSupport.registerAndLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PortfolioControllerIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void newPortfolioStartsAsDraft() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "portfolio-owner@example.com");

        mockMvc.perform(get("/api/v1/portfolio").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void ownerCanPublishAndUnpublish() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "portfolio-toggle@example.com");

        mockMvc.perform(patch("/api/v1/portfolio/status")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PUBLISHED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        mockMvc.perform(patch("/api/v1/portfolio/status")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DRAFT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/v1/portfolio")).andExpect(status().isUnauthorized());
    }
}
