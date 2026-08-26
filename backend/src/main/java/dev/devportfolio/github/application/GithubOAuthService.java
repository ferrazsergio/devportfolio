package dev.devportfolio.github.application;

import java.util.UUID;

public interface GithubOAuthService {

    String buildAuthorizeUrl(String state, String redirectUri);

    void connect(UUID ownerUserId, String code, String redirectUri);

    void disconnect(UUID ownerUserId);

    GithubConnectionStatus status(UUID ownerUserId);
}
