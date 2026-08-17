package dev.devportfolio.skill.presentation;

import dev.devportfolio.skill.domain.Skill;
import dev.devportfolio.skill.domain.SkillCategory;
import java.util.UUID;

public record SkillResponse(UUID id, String name, SkillCategory category) {

    public static SkillResponse from(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getName(), skill.getCategory());
    }
}
