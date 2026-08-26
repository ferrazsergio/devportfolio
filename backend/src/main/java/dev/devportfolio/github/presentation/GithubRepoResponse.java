package dev.devportfolio.github.presentation;

import dev.devportfolio.github.application.GithubRepoSummary;

public record GithubRepoResponse(String name, String fullName, String description, String htmlUrl, String language) {

    public static GithubRepoResponse from(GithubRepoSummary summary) {
        return new GithubRepoResponse(summary.name(), summary.fullName(), summary.description(), summary.htmlUrl(),
                summary.language());
    }
}
