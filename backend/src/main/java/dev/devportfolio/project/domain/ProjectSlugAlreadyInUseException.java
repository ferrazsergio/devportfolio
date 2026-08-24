package dev.devportfolio.project.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class ProjectSlugAlreadyInUseException extends ConflictException {

    public ProjectSlugAlreadyInUseException() {
        super("Você já possui um projeto com esse slug.");
    }
}
