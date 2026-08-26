package dev.devportfolio.github.application;

import java.util.List;
import java.util.UUID;

public interface GithubRepositoryService {

    List<GithubRepoSummary> listRepositories(UUID ownerUserId);

    GithubImportResult importRepositories(UUID ownerUserId, List<String> fullNames);
}
