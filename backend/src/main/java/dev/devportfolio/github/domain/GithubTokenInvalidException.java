package dev.devportfolio.github.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class GithubTokenInvalidException extends ConflictException {

    public GithubTokenInvalidException() {
        super("Conexão com o GitHub expirou ou foi revogada. Reconecte sua conta.");
    }
}
