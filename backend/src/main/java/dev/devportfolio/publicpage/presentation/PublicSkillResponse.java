package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.skill.domain.Skill;
import dev.devportfolio.skill.domain.SkillCategory;

public record PublicSkillResponse(String name, SkillCategory category) {

    public static PublicSkillResponse from(Skill skill) {
        return new PublicSkillResponse(skill.getName(), skill.getCategory());
    }
}
