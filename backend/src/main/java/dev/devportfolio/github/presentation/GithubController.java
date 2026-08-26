package dev.devportfolio.github.presentation;

import dev.devportfolio.github.application.GithubImportResult;
import dev.devportfolio.github.application.GithubOAuthService;
import dev.devportfolio.github.application.GithubRepositoryService;
import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fluxo "Conectar com GitHub" (RF Fase 8, ver ADR-008). Não é login — o
 * usuário já está autenticado via sessão própria (ADR-005); aqui só vincula
 * uma conta GitHub opcional para importar repositórios como Project.
 */
@RestController
@RequestMapping("/api/v1/github")
public class GithubController {

    private static final String OAUTH_STATE_SESSION_KEY = "github_oauth_state";

    private final GithubOAuthService oauthService;
    private final GithubRepositoryService repositoryService;
    private final String publicBaseUrl;

    public GithubController(GithubOAuthService oauthService, GithubRepositoryService repositoryService,
            @Value("${app.public-base-url}") String publicBaseUrl) {
        this.oauthService = oauthService;
        this.repositoryService = repositoryService;
        this.publicBaseUrl = publicBaseUrl;
    }

    @GetMapping("/connect")
    public void connect(HttpSession session, HttpServletResponse response) throws IOException {
        String state = UUID.randomUUID().toString();
        session.setAttribute(OAUTH_STATE_SESSION_KEY, state);
        String authorizeUrl = oauthService.buildAuthorizeUrl(state, callbackUrl());
        response.sendRedirect(authorizeUrl);
    }

    @GetMapping("/callback")
    public void callback(@AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) String code, @RequestParam(required = false) String state,
            @RequestParam(required = false) String error, HttpSession session, HttpServletResponse response)
            throws IOException {
        Object expectedState = session.getAttribute(OAUTH_STATE_SESSION_KEY);
        session.removeAttribute(OAUTH_STATE_SESSION_KEY);

        boolean valid = error == null && code != null && expectedState != null && expectedState.equals(state);
        if (valid) {
            try {
                oauthService.connect(principal.getUser().getId(), code, callbackUrl());
                response.sendRedirect(publicBaseUrl + "/admin/projects?github=connected");
                return;
            } catch (RuntimeException ex) {
                response.sendRedirect(publicBaseUrl + "/admin/projects?github=error");
                return;
            }
        }
        response.sendRedirect(publicBaseUrl + "/admin/projects?github=error");
    }

    @GetMapping("/status")
    public GithubStatusResponse status(@AuthenticationPrincipal AuthenticatedUser principal) {
        return GithubStatusResponse.from(oauthService.status(principal.getUser().getId()));
    }

    @GetMapping("/repositories")
    public List<GithubRepoResponse> repositories(@AuthenticationPrincipal AuthenticatedUser principal) {
        return repositoryService.listRepositories(principal.getUser().getId()).stream().map(GithubRepoResponse::from)
                .toList();
    }

    @PostMapping("/import")
    public GithubImportResponse importRepositories(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody GithubImportRequest request) {
        GithubImportResult result = repositoryService.importRepositories(principal.getUser().getId(),
                request.fullNames());
        return GithubImportResponse.from(result);
    }

    @DeleteMapping("/connection")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disconnect(@AuthenticationPrincipal AuthenticatedUser principal) {
        oauthService.disconnect(principal.getUser().getId());
    }

    private String callbackUrl() {
        return URI.create(publicBaseUrl).resolve("/api/v1/github/callback").toString();
    }
}
