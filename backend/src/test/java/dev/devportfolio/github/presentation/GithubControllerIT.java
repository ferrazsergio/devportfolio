package dev.devportfolio.github.presentation;

import static dev.devportfolio.testsupport.TestAuthSupport.registerAndLogin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Fluxo completo "Conectar com GitHub" (ver ADR-008) contra um WireMock local
 * simulando a API do GitHub — nunca contra github.com real, para não depender
 * de credenciais/rede externa nos testes.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class GithubControllerIT {

    private static final WireMockServer WIRE_MOCK = new WireMockServer(WireMockConfiguration.options().dynamicPort());

    static {
        WIRE_MOCK.start();
    }

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void githubProperties(DynamicPropertyRegistry registry) {
        registry.add("app.github.oauth-base-url", WIRE_MOCK::baseUrl);
        registry.add("app.github.api-base-url", WIRE_MOCK::baseUrl);
        registry.add("app.github.client-id", () -> "test-client-id");
        registry.add("app.github.client-secret", () -> "test-client-secret");
        registry.add("app.github.token-encryption-key",
                () -> Base64.getEncoder().encodeToString(new byte[32]));
    }

    @AfterAll
    static void stopWireMock() {
        WIRE_MOCK.stop();
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetWireMock() {
        WIRE_MOCK.resetAll();
    }

    private String extractState(String redirectedUrl) {
        String query = redirectedUrl.substring(redirectedUrl.indexOf('?') + 1);
        for (String param : query.split("&")) {
            if (param.startsWith("state=")) {
                return URLDecoder.decode(param.substring("state=".length()), StandardCharsets.UTF_8);
            }
        }
        throw new IllegalStateException("state não encontrado na URL: " + redirectedUrl);
    }

    private void connectSuccessfully(MockHttpSession session) throws Exception {
        WIRE_MOCK.stubFor(WireMock.post("/login/oauth/access_token")
                .willReturn(WireMock.okJson(
                        "{\"access_token\":\"tok-123\",\"token_type\":\"bearer\",\"scope\":\"public_repo\"}")));
        WIRE_MOCK.stubFor(WireMock.get("/user").withHeader("Authorization", WireMock.equalTo("Bearer tok-123"))
                .willReturn(WireMock.okJson("{\"login\":\"ana-souza\"}")));

        MvcResult connectResult = mockMvc.perform(get("/api/v1/github/connect").session(session))
                .andExpect(status().is3xxRedirection()).andReturn();
        String state = extractState(connectResult.getResponse().getRedirectedUrl());

        mockMvc.perform(get("/api/v1/github/callback").session(session).param("code", "code-abc")
                        .param("state", state))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .endsWith("/admin/projects?github=connected"));
    }

    @Test
    void connectRedirectsToGithubAuthorizeUrlWithClientIdAndState() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "gh-connect@example.com");

        MvcResult result = mockMvc.perform(get("/api/v1/github/connect").session(session))
                .andExpect(status().is3xxRedirection()).andReturn();

        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assertThat(redirectedUrl).startsWith(WIRE_MOCK.baseUrl() + "/login/oauth/authorize");
        assertThat(redirectedUrl).contains("client_id=test-client-id").contains("scope=public_repo");
    }

    @Test
    void callbackWithMismatchedStateRedirectsToError() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "gh-badstate@example.com");
        mockMvc.perform(get("/api/v1/github/connect").session(session)).andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/api/v1/github/callback").session(session).param("code", "code-abc")
                        .param("state", "state-que-nao-bate"))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .endsWith("/admin/projects?github=error"));
    }

    @Test
    void callbackWithValidCodeConnectsAndStatusReflectsIt() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "gh-connected@example.com");

        connectSuccessfully(session);

        mockMvc.perform(get("/api/v1/github/status").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.connected").value(true))
                .andExpect(jsonPath("$.githubUsername").value("ana-souza"));
    }

    @Test
    void repositoriesEndpointRequiresAnExistingConnection() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "gh-notconnected@example.com");

        mockMvc.perform(get("/api/v1/github/repositories").session(session)).andExpect(status().isConflict());
    }

    @Test
    void repositoriesEndpointListsNonForkRepositories() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "gh-repos@example.com");
        connectSuccessfully(session);
        WIRE_MOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo("/user/repos")).willReturn(WireMock.okJson("""
                [
                  {"id":1,"name":"devportfolio","full_name":"ana-souza/devportfolio","description":"Meu portfólio",
                   "html_url":"https://github.com/ana-souza/devportfolio","language":"Java","fork":false,"archived":false},
                  {"id":2,"name":"um-fork","full_name":"ana-souza/um-fork","description":"fork",
                   "html_url":"https://github.com/ana-souza/um-fork","language":"Go","fork":true,"archived":false}
                ]
                """)));

        mockMvc.perform(get("/api/v1/github/repositories").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].fullName").value("ana-souza/devportfolio"));
    }

    @Test
    void repositoriesEndpointMapsGithubOutageToBadGateway() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "gh-outage@example.com");
        connectSuccessfully(session);
        WIRE_MOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo("/user/repos"))
                .willReturn(WireMock.serverError()));

        mockMvc.perform(get("/api/v1/github/repositories").session(session)).andExpect(status().isBadGateway());
    }

    @Test
    void importCreatesProjectFromSelectedRepository() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "gh-import@example.com");
        connectSuccessfully(session);
        WIRE_MOCK.stubFor(WireMock.get(WireMock.urlPathEqualTo("/user/repos")).willReturn(WireMock.okJson("""
                [{"id":1,"name":"devportfolio","full_name":"ana-souza/devportfolio","description":"Meu portfólio",
                  "html_url":"https://github.com/ana-souza/devportfolio","language":"Java","fork":false,"archived":false}]
                """)));

        mockMvc.perform(post("/api/v1/github/import").session(session).with(csrf())
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"fullNames\":[\"ana-souza/devportfolio\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imported[0].name").value("devportfolio"))
                .andExpect(jsonPath("$.imported[0].slug").value("devportfolio"))
                .andExpect(jsonPath("$.skipped").isEmpty());

        mockMvc.perform(get("/api/v1/projects").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("devportfolio"))
                .andExpect(jsonPath("$[0].githubUrl").value("https://github.com/ana-souza/devportfolio"));
    }

    @Test
    void disconnectRemovesConnectionAndStatusReflectsIt() throws Exception {
        MockHttpSession session = registerAndLogin(mockMvc, "gh-disconnect@example.com");
        connectSuccessfully(session);

        mockMvc.perform(delete("/api/v1/github/connection").session(session).with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/github/status").session(session)).andExpect(status().isOk())
                .andExpect(jsonPath("$.connected").value(false));
    }
}
