package dev.devportfolio.github.application;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GithubRepositoryServiceImpl implements GithubRepositoryService {

    private final GithubConnectionRepository connectionRepository;
    private final PortfolioService portfolioService;
    private final GithubApiClient apiClient;
    private final TokenEncryptor tokenEncryptor;
    private final ProjectService projectService;

    public GithubRepositoryServiceImpl(GithubConnectionRepository connectionRepository,
            PortfolioService portfolioService, GithubApiClient apiClient, TokenEncryptor tokenEncryptor,
            ProjectService projectService) {
        this.connectionRepository = connectionRepository;
        this.portfolioService = portfolioService;
        this.apiClient = apiClient;
        this.tokenEncryptor = tokenEncryptor;
        this.projectService = projectService;
    }

    @Override
    public List<GithubRepoSummary> listRepositories(UUID ownerUserId) {
        String accessToken = requireAccessToken(ownerUserId);
        return apiClient.listRepositories(accessToken).stream().filter(repo -> !repo.fork())
                .map(repo -> new GithubRepoSummary(repo.name(), repo.fullName(), repo.description(), repo.htmlUrl(),
                        repo.language()))
                .toList();
    }

    @Override
    public GithubImportResult importRepositories(UUID ownerUserId, List<String> fullNames) {
        String accessToken = requireAccessToken(ownerUserId);
        List<GithubRepoDto> available = apiClient.listRepositories(accessToken);

        List<Project> imported = new ArrayList<>();
        List<GithubImportResult.Skipped> skipped = new ArrayList<>();

        for (String fullName : fullNames) {
            GithubRepoDto repo = available.stream().filter(candidate -> candidate.fullName().equals(fullName))
                    .findFirst().orElse(null);
            if (repo == null) {
                skipped.add(new GithubImportResult.Skipped(fullName, "Repositório não encontrado no GitHub."));
                continue;
            }
            try {
                Project project = projectService.create(ownerUserId, repo.name(), slugify(repo.name()),
                        truncate(repo.description(), 500), null, null, repo.htmlUrl(), null, null,
                        ProjectStatus.IN_PROGRESS, false, Set.of());
                imported.add(project);
            } catch (ProjectSlugAlreadyInUseException ex) {
                skipped.add(new GithubImportResult.Skipped(fullName, "Já existe um projeto com esse nome."));
            }
        }
        return new GithubImportResult(imported, skipped);
    }

    private String requireAccessToken(UUID ownerUserId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        GithubConnection connection = connectionRepository.findByPortfolioId(portfolioId)
                .orElseThrow(GithubNotConnectedException::new);
        return tokenEncryptor.decrypt(connection.getEncryptedAccessToken());
    }

    private static String slugify(String name) {
        String slug = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9-]+", "-").replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
        if (slug.length() < 3) {
            slug = (slug + "-repo").substring(0, Math.min(slug.length() + 5, 10));
        }
        return slug.length() > 100 ? slug.substring(0, 100) : slug;
    }

    private static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
