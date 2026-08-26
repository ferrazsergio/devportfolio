package dev.devportfolio.github.presentation;

import dev.devportfolio.github.application.GithubConnectionStatus;

public record GithubStatusResponse(boolean connected, String githubUsername) {

    public static GithubStatusResponse from(GithubConnectionStatus status) {
        return new GithubStatusResponse(status.connected(), status.githubUsername());
    }
}
