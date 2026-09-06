package dev.devportfolio.project.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class ProjectSlugAlreadyInUseException extends ConflictException {

    public ProjectSlugAlreadyInUseException(String message) {
        super(message);
    }
}
