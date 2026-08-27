package dev.devportfolio.github.infrastructure;

import dev.devportfolio.github.domain.GithubTokenInvalidException;
import dev.devportfolio.shared.domain.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Chamadas à API do GitHub isoladas por timeout + circuit breaker (ver ADR-008).
 * Nenhum outro módulo chama github.com diretamente.
 */
@Component
public class GithubApiClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;
    private final String oauthBaseUrl;
    private final String apiBaseUrl;

    public GithubApiClient(RestClient.Builder builder, @Value("${app.github.client-id}") String clientId,
            @Value("${app.github.client-secret}") String clientSecret,
            @Value("${app.github.oauth-base-url:https://github.com}") String oauthBaseUrl,
            @Value("${app.github.api-base-url:https://api.github.com}") String apiBaseUrl) {
        // HTTP/1.1 explícito: o HttpClient do JDK tenta HTTP/2 por padrão, o que causa
        // "RST_STREAM"/EOF ao falar com servidores de teste (WireMock/Jetty) que não
        // negociam h2c corretamente. GitHub também atende bem em HTTP/1.1.
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(TIMEOUT).build());
        requestFactory.setReadTimeout(TIMEOUT);
        this.restClient = builder.requestFactory(requestFactory).build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.oauthBaseUrl = oauthBaseUrl;
        this.apiBaseUrl = apiBaseUrl;
    }

    @CircuitBreaker(name = "github-oauth", fallbackMethod = "exchangeCodeFallback")
    public GithubTokenResponse exchangeCodeForToken(String code, String redirectUri) {
        GithubTokenResponse response = restClient.post().uri(oauthBaseUrl + "/login/oauth/access_token")
                .header("Accept", "application/json")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(Map.of("client_id", clientId, "client_secret", clientSecret, "code", code, "redirect_uri",
                        redirectUri))
                .retrieve().body(GithubTokenResponse.class);
        if (response == null || response.accessToken() == null) {
            String reason = response != null ? response.errorDescription() : "resposta vazia";
            throw new ExternalServiceException("Não foi possível concluir a autorização com o GitHub: " + reason);
        }
        return response;
    }

    @SuppressWarnings("unused")
    private GithubTokenResponse exchangeCodeFallback(String code, String redirectUri, Throwable cause) {
        throw new ExternalServiceException("GitHub indisponível no momento. Tente novamente em instantes.", cause);
    }

    @CircuitBreaker(name = "github-oauth", fallbackMethod = "getAuthenticatedUserFallback")
    public GithubUserDto getAuthenticatedUser(String accessToken) {
        return restClient.get().uri(apiBaseUrl + "/user").header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .exchange((request, response) -> {
                    requireSuccessOrThrow(response);
                    return response.bodyTo(GithubUserDto.class);
                });
    }

    @SuppressWarnings("unused")
    private GithubUserDto getAuthenticatedUserFallback(String accessToken, Throwable cause) {
        if (cause instanceof GithubTokenInvalidException invalid) {
            throw invalid;
        }
        throw new ExternalServiceException("GitHub indisponível no momento. Tente novamente em instantes.", cause);
    }

    @CircuitBreaker(name = "github-api", fallbackMethod = "listRepositoriesFallback")
    public List<GithubRepoDto> listRepositories(String accessToken) {
        GithubRepoDto[] repos = restClient.get().uri(apiBaseUrl + "/user/repos?per_page=100&sort=updated")
                .header("Authorization", "Bearer " + accessToken).header("Accept", "application/vnd.github+json")
                .exchange((request, response) -> {
                    requireSuccessOrThrow(response);
                    return response.bodyTo(GithubRepoDto[].class);
                });
        return repos == null ? List.of() : Arrays.asList(repos);
    }

    /**
     * `.exchange()` não lança automaticamente em respostas de erro como `.retrieve()`
     * faz — precisa ser verificado explicitamente, senão um corpo de erro vazio (ex.:
     * 500 sem corpo) é interpretado como sucesso com dado nulo/vazio.
     */
    private static void requireSuccessOrThrow(RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response)
            throws IOException {
        int status = response.getStatusCode().value();
        if (status == 401) {
            throw new GithubTokenInvalidException();
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ExternalServiceException("GitHub respondeu com status " + status + ".");
        }
    }

    @SuppressWarnings("unused")
    private List<GithubRepoDto> listRepositoriesFallback(String accessToken, Throwable cause) {
        if (cause instanceof GithubTokenInvalidException invalid) {
            throw invalid;
        }
        throw new ExternalServiceException(
                "Não foi possível listar os repositórios do GitHub agora. Tente novamente em instantes.", cause);
    }
}
