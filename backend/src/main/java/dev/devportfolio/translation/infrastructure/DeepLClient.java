package dev.devportfolio.translation.infrastructure;

import dev.devportfolio.shared.domain.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Chamada isolada à API do DeepL (mesmo padrão de isolamento do GithubApiClient —
 * timeout + circuit breaker, nenhum outro módulo fala com a API externa direto).
 * A tradução automática é um extra opcional: qualquer falha aqui volta pro chamador
 * (ContentTranslationServiceImpl) como "sem tradução disponível", nunca quebra o save.
 */
@Component
public class DeepLClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final RestClient restClient;
    private final String apiKey;
    private final String baseUrl;

    public DeepLClient(RestClient.Builder builder, @Value("${app.translation.deepl.api-key:}") String apiKey,
            @Value("${app.translation.deepl.base-url:https://api-free.deepl.com}") String baseUrl) {
        // Mesmo motivo do GithubApiClient: força HTTP/1.1 pra não bater em RST_STREAM/EOF
        // contra o WireMock nos testes de integração.
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(TIMEOUT).build());
        requestFactory.setReadTimeout(TIMEOUT);
        this.restClient = builder.requestFactory(requestFactory).build();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @CircuitBreaker(name = "deepl-translation", fallbackMethod = "translateFallback")
    public String translate(String text) {
        DeepLTranslateResponse response = restClient.post().uri(baseUrl + "/v2/translate")
                .header("Authorization", "DeepL-Auth-Key " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DeepLTranslateRequest(List.of(text), "EN"))
                .retrieve()
                .body(DeepLTranslateResponse.class);
        if (response == null || response.translations().isEmpty()) {
            throw new ExternalServiceException("DeepL retornou resposta vazia.");
        }
        return response.translations().get(0).text();
    }

    @SuppressWarnings("unused")
    private String translateFallback(String text, Throwable cause) {
        return null;
    }
}
