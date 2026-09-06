package dev.devportfolio.skill.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class SkillAlreadyExistsException extends ConflictException {

    public SkillAlreadyExistsException(String message) {
        super(message);
    }
}
