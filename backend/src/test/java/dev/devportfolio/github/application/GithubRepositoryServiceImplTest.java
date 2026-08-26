package dev.devportfolio.github.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import dev.devportfolio.github.domain.GithubConnection;
import dev.devportfolio.github.domain.GithubConnectionRepository;
import dev.devportfolio.github.domain.GithubNotConnectedException;
import dev.devportfolio.github.infrastructure.GithubApiClient;
import dev.devportfolio.github.infrastructure.GithubRepoDto;
import dev.devportfolio.github.infrastructure.TokenEncryptor;
import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.project.application.ProjectService;
import dev.devportfolio.project.domain.Project;
import dev.devportfolio.project.domain.ProjectSlugAlreadyInUseException;
import dev.devportfolio.project.domain.ProjectStatus;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GithubRepositoryServiceImplTest {

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

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private GithubRepositoryServiceImpl service;

    private void connected() {
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(connectionRepository.findByPortfolioId(PORTFOLIO_ID))
                .thenReturn(Optional.of(new GithubConnection(PORTFOLIO_ID, "ana-souza", "encrypted")));
        when(tokenEncryptor.decrypt("encrypted")).thenReturn("plain-token");
    }

    @Test
    void listRepositoriesRequiresAnExistingConnection() {
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(connectionRepository.findByPortfolioId(PORTFOLIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listRepositories(OWNER_ID)).isInstanceOf(GithubNotConnectedException.class);
    }

    @Test
    void listRepositoriesExcludesForksAndMapsFields() {
        connected();
        when(apiClient.listRepositories("plain-token")).thenReturn(List.of(
                new GithubRepoDto(1L, "devportfolio", "ana-souza/devportfolio", "Meu portfólio",
                        "https://github.com/ana-souza/devportfolio", "Java", false, false),
                new GithubRepoDto(2L, "forked-repo", "ana-souza/forked-repo", "Um fork", "https://github.com/x", "Go",
                        true, false)));

        List<GithubRepoSummary> repos = service.listRepositories(OWNER_ID);

        assertThat(repos).hasSize(1);
        assertThat(repos.get(0).fullName()).isEqualTo("ana-souza/devportfolio");
        assertThat(repos.get(0).language()).isEqualTo("Java");
    }

    @Test
    void importCreatesProjectsForEachSelectedRepository() {
        connected();
        GithubRepoDto repo = new GithubRepoDto(1L, "devportfolio", "ana-souza/devportfolio", "Meu portfólio",
                "https://github.com/ana-souza/devportfolio", "Java", false, false);
        when(apiClient.listRepositories("plain-token")).thenReturn(List.of(repo));
        Project created = new Project(PORTFOLIO_ID, "devportfolio", "devportfolio", "Meu portfólio", null, null,
                repo.htmlUrl(), null, null, ProjectStatus.IN_PROGRESS, false, 0, Set.of());
        when(projectService.create(OWNER_ID, "devportfolio", "devportfolio", "Meu portfólio", null, null,
                repo.htmlUrl(), null, null, ProjectStatus.IN_PROGRESS, false, Set.of())).thenReturn(created);

        GithubImportResult result = service.importRepositories(OWNER_ID, List.of("ana-souza/devportfolio"));

        assertThat(result.imported()).containsExactly(created);
        assertThat(result.skipped()).isEmpty();
    }

    @Test
    void importSkipsRepositoryNotFoundAmongAvailableOnes() {
        connected();
        when(apiClient.listRepositories("plain-token")).thenReturn(List.of());

        GithubImportResult result = service.importRepositories(OWNER_ID, List.of("ana-souza/nao-existe"));

        assertThat(result.imported()).isEmpty();
        assertThat(result.skipped()).hasSize(1);
        assertThat(result.skipped().get(0).fullName()).isEqualTo("ana-souza/nao-existe");
    }

    @Test
    void importSkipsRepositoryWhenSlugAlreadyExists() {
        connected();
        GithubRepoDto repo = new GithubRepoDto(1L, "devportfolio", "ana-souza/devportfolio", "desc",
                "https://github.com/ana-souza/devportfolio", "Java", false, false);
        when(apiClient.listRepositories("plain-token")).thenReturn(List.of(repo));
        when(projectService.create(OWNER_ID, "devportfolio", "devportfolio", "desc", null, null, repo.htmlUrl(),
                null, null, ProjectStatus.IN_PROGRESS, false, Set.of()))
                .thenThrow(new ProjectSlugAlreadyInUseException());

        GithubImportResult result = service.importRepositories(OWNER_ID, List.of("ana-souza/devportfolio"));

        assertThat(result.imported()).isEmpty();
        assertThat(result.skipped()).hasSize(1);
    }
}
