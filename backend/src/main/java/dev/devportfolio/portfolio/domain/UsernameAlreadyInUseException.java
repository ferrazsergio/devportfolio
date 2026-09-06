package dev.devportfolio.portfolio.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class UsernameAlreadyInUseException extends ConflictException {

    public UsernameAlreadyInUseException(String message) {
        super(message);
    }
}
