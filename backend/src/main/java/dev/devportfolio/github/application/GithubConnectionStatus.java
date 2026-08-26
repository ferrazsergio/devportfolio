package dev.devportfolio.github.application;

public record GithubConnectionStatus(boolean connected, String githubUsername) {

    public static GithubConnectionStatus disconnected() {
        return new GithubConnectionStatus(false, null);
    }
}
