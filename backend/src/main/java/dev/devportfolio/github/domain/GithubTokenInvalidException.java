package dev.devportfolio.github.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class GithubTokenInvalidException extends ConflictException {

    public GithubTokenInvalidException(String message) {
        super(message);
    }
}
