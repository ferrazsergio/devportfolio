package dev.devportfolio.skill.presentation;

import dev.devportfolio.skill.domain.SkillCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SkillRequest(
        @NotBlank(message = "Nome é obrigatório.") String name,
        @NotNull(message = "Categoria é obrigatória.") SkillCategory category) {
}
