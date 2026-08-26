package dev.devportfolio.github.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class GithubNotConnectedException extends ConflictException {

    public GithubNotConnectedException() {
        super("Conecte sua conta do GitHub antes de importar repositórios.");
    }
}
