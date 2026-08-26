package dev.devportfolio.github.presentation;

import dev.devportfolio.github.application.GithubImportResult;
import dev.devportfolio.project.domain.Project;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GithubImportResponse(List<ImportedProject> imported, List<GithubImportResult.Skipped> skipped) {

    public record ImportedProject(UUID id, String name, String slug, String githubUrl, LocalDate date) {

        static ImportedProject from(Project project) {
            return new ImportedProject(project.getId(), project.getName(), project.getSlug(),
                    project.getGithubUrl(), project.getDate());
        }
    }

    public static GithubImportResponse from(GithubImportResult result) {
        return new GithubImportResponse(result.imported().stream().map(ImportedProject::from).toList(),
                result.skipped());
    }
}
