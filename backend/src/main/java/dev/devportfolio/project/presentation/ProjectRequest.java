package dev.devportfolio.project.presentation;

import dev.devportfolio.project.domain.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ProjectRequest(
        @NotBlank(message = "Nome é obrigatório.") String name,

        @NotBlank(message = "Slug é obrigatório.")
        @Pattern(regexp = "^[a-z0-9-]{3,100}$",
                message = "Slug deve ter entre 3 e 100 caracteres, apenas letras minúsculas, números e hífen.")
        String slug,

        String shortDescription,
        String fullDescription,
        String imageUrl,
        String githubUrl,
        String demoUrl,
        LocalDate date,

        @NotNull(message = "Status é obrigatório.") ProjectStatus status,

        boolean featured,
        Set<UUID> technologyIds) {

    public ProjectRequest {
        if (technologyIds == null) {
            technologyIds = Set.of();
        }
    }
}
