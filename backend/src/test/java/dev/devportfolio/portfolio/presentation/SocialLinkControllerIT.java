package dev.devportfolio.portfolio.presentation;

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
class SocialLinkControllerIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsListsAndDeletesSocialLink() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "sociallink-owner@example.com");

        MvcResult created = mockMvc.perform(post("/api/v1/social-links")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"platform\":\"GitHub\",\"url\":\"https://github.com/ana\",\"order\":0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.platform").value("GitHub"))
                .andReturn();
        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/api/v1/social-links").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].platform").value("GitHub"));

        mockMvc.perform(delete("/api/v1/social-links/" + id).session(session).with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/social-links").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void ownerCannotDeleteAnotherOwnersSocialLink() throws Exception {
        MockHttpSession ownerSession = registerAndLogin(mockMvc, "sociallink-owner2@example.com");
        MvcResult created = mockMvc.perform(post("/api/v1/social-links")
                        .session(ownerSession)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"platform\":\"GitHub\",\"url\":\"https://github.com/ana\",\"order\":0}"))
                .andExpect(status().isCreated())
                .andReturn();
        String id = JsonPath.read(created.getResponse().getContentAsString(), "$.id");

        MockHttpSession intruderSession = registerAndLogin(mockMvc, "sociallink-intruder@example.com");

        mockMvc.perform(delete("/api/v1/social-links/" + id).session(intruderSession).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
