package dev.devportfolio.identity.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class EmailAlreadyInUseException extends ConflictException {

    public EmailAlreadyInUseException() {
        super("Este email já está cadastrado.");
    }
}
