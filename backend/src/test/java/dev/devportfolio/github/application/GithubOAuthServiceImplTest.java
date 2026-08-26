package dev.devportfolio.github.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.devportfolio.github.domain.GithubConnection;
import dev.devportfolio.github.domain.GithubConnectionRepository;
import dev.devportfolio.github.infrastructure.GithubApiClient;
import dev.devportfolio.github.infrastructure.GithubTokenResponse;
import dev.devportfolio.github.infrastructure.GithubUserDto;
import dev.devportfolio.github.infrastructure.TokenEncryptor;
import dev.devportfolio.portfolio.application.PortfolioService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GithubOAuthServiceImplTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Mock
    private GithubConnectionRepository connectionRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private GithubApiClient apiClient;

    @Mock
    private TokenEncryptor tokenEncryptor;

    private GithubOAuthServiceImpl service;

    private GithubOAuthServiceImpl serviceWith(String clientId, String oauthBaseUrl) {
        return new GithubOAuthServiceImpl(connectionRepository, portfolioService, apiClient, tokenEncryptor, clientId,
                oauthBaseUrl);
    }

    @Test
    void buildsAuthorizeUrlWithClientIdScopeStateAndEncodedRedirect() {
        service = serviceWith("abc123", "https://github.com");

        String url = service.buildAuthorizeUrl("xyz-state", "http://localhost:4200/api/v1/github/callback");

        assertThat(url).isEqualTo("https://github.com/login/oauth/authorize?client_id=abc123&scope=public_repo"
                + "&redirect_uri=http%3A%2F%2Flocalhost%3A4200%2Fapi%2Fv1%2Fgithub%2Fcallback&state=xyz-state");
    }

    @Test
    void connectCreatesNewConnectionWhenNoneExists() {
        service = serviceWith("abc123", "https://github.com");
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(apiClient.exchangeCodeForToken("code123", "redirect")).thenReturn(
                new GithubTokenResponse("token-abc", "bearer", "public_repo", null, null));
        when(apiClient.getAuthenticatedUser("token-abc")).thenReturn(new GithubUserDto("ana-souza"));
        when(tokenEncryptor.encrypt("token-abc")).thenReturn("encrypted-token");
        when(connectionRepository.findByPortfolioId(PORTFOLIO_ID)).thenReturn(Optional.empty());

        service.connect(OWNER_ID, "code123", "redirect");

        verify(connectionRepository).save(any(GithubConnection.class));
    }

    @Test
    void connectUpdatesExistingConnectionInstead() {
        service = serviceWith("abc123", "https://github.com");
        GithubConnection existing = new GithubConnection(PORTFOLIO_ID, "old-user", "old-encrypted");
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(apiClient.exchangeCodeForToken("code123", "redirect")).thenReturn(
                new GithubTokenResponse("token-new", "bearer", "public_repo", null, null));
        when(apiClient.getAuthenticatedUser("token-new")).thenReturn(new GithubUserDto("new-user"));
        when(tokenEncryptor.encrypt("token-new")).thenReturn("new-encrypted");
        when(connectionRepository.findByPortfolioId(PORTFOLIO_ID)).thenReturn(Optional.of(existing));

        service.connect(OWNER_ID, "code123", "redirect");

        assertThat(existing.getGithubUsername()).isEqualTo("new-user");
        assertThat(existing.getEncryptedAccessToken()).isEqualTo("new-encrypted");
        verify(connectionRepository, never()).save(any());
    }

    @Test
    void disconnectDeletesConnectionByPortfolioId() {
        service = serviceWith("abc123", "https://github.com");
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);

        service.disconnect(OWNER_ID);

        verify(connectionRepository).deleteByPortfolioId(PORTFOLIO_ID);
    }

    @Test
    void statusReflectsConnectedState() {
        service = serviceWith("abc123", "https://github.com");
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(connectionRepository.findByPortfolioId(PORTFOLIO_ID))
                .thenReturn(Optional.of(new GithubConnection(PORTFOLIO_ID, "ana-souza", "enc")));

        GithubConnectionStatus status = service.status(OWNER_ID);

        assertThat(status.connected()).isTrue();
        assertThat(status.githubUsername()).isEqualTo("ana-souza");
    }

    @Test
    void statusReflectsDisconnectedStateWhenNoConnectionExists() {
        service = serviceWith("abc123", "https://github.com");
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(connectionRepository.findByPortfolioId(PORTFOLIO_ID)).thenReturn(Optional.empty());

        GithubConnectionStatus status = service.status(OWNER_ID);

        assertThat(status.connected()).isFalse();
        assertThat(status.githubUsername()).isNull();
    }
}
