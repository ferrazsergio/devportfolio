package dev.devportfolio.skill.domain;

import dev.devportfolio.shared.domain.ConflictException;

public class SkillAlreadyExistsException extends ConflictException {

    public SkillAlreadyExistsException() {
        super("Você já possui uma habilidade com esse nome.");
    }
}
