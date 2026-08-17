package dev.devportfolio.portfolio.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class UsernameAlreadyInUseException extends ConflictException {

    public UsernameAlreadyInUseException() {
        super("Este nome de usuário já está em uso.");
    }
}
