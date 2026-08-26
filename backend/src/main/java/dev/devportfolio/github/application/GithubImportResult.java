package dev.devportfolio.github.application;

import dev.devportfolio.project.domain.Project;
import java.util.List;

public record GithubImportResult(List<Project> imported, List<Skipped> skipped) {

    public record Skipped(String fullName, String reason) {
    }
}
