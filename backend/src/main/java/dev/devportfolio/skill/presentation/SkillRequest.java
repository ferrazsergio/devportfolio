package dev.devportfolio.skill.presentation;

import dev.devportfolio.skill.domain.SkillCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SkillRequest(
        @NotBlank(message = "{validation.skill.name.required}") String name,
        @NotNull(message = "{validation.skill.category.required}") SkillCategory category) {
}
