package dev.devportfolio.github.application;

import dev.devportfolio.github.domain.GithubConnection;
import dev.devportfolio.github.domain.GithubConnectionRepository;
import dev.devportfolio.github.infrastructure.GithubApiClient;
import dev.devportfolio.github.infrastructure.GithubTokenResponse;
import dev.devportfolio.github.infrastructure.GithubUserDto;
import dev.devportfolio.github.infrastructure.TokenEncryptor;
import dev.devportfolio.portfolio.application.PortfolioService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GithubOAuthServiceImpl implements GithubOAuthService {

    // Somente repositórios públicos — suficiente para importar como Project (RF público).
    private static final String SCOPE = "public_repo";

    private final GithubConnectionRepository connectionRepository;
    private final PortfolioService portfolioService;
    private final GithubApiClient apiClient;
    private final TokenEncryptor tokenEncryptor;
    private final String clientId;
    private final String oauthBaseUrl;

    public GithubOAuthServiceImpl(GithubConnectionRepository connectionRepository, PortfolioService portfolioService,
            GithubApiClient apiClient, TokenEncryptor tokenEncryptor,
            @Value("${app.github.client-id}") String clientId,
            @Value("${app.github.oauth-base-url:https://github.com}") String oauthBaseUrl) {
        this.connectionRepository = connectionRepository;
        this.portfolioService = portfolioService;
        this.apiClient = apiClient;
        this.tokenEncryptor = tokenEncryptor;
        this.clientId = clientId;
        this.oauthBaseUrl = oauthBaseUrl;
    }

    @Override
    public String buildAuthorizeUrl(String state, String redirectUri) {
        String encodedRedirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return oauthBaseUrl + "/login/oauth/authorize?client_id=" + clientId + "&scope=" + SCOPE + "&redirect_uri="
                + encodedRedirect + "&state=" + state;
    }

    @Override
    @Transactional
    public void connect(UUID ownerUserId, String code, String redirectUri) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        GithubTokenResponse tokenResponse = apiClient.exchangeCodeForToken(code, redirectUri);
        GithubUserDto user = apiClient.getAuthenticatedUser(tokenResponse.accessToken());
        String encryptedToken = tokenEncryptor.encrypt(tokenResponse.accessToken());

        connectionRepository.findByPortfolioId(portfolioId)
                .ifPresentOrElse(existing -> existing.updateToken(user.login(), encryptedToken),
                        () -> connectionRepository
                                .save(new GithubConnection(portfolioId, user.login(), encryptedToken)));
    }

    @Override
    @Transactional
    public void disconnect(UUID ownerUserId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        connectionRepository.deleteByPortfolioId(portfolioId);
    }

    @Override
    public GithubConnectionStatus status(UUID ownerUserId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return connectionRepository.findByPortfolioId(portfolioId)
                .map(connection -> new GithubConnectionStatus(true, connection.getGithubUsername()))
                .orElseGet(GithubConnectionStatus::disconnected);
    }
}
