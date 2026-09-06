package dev.devportfolio.project.presentation;

import dev.devportfolio.project.domain.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ProjectRequest(
        @NotBlank(message = "{validation.project.name.required}") String name,

        @NotBlank(message = "{validation.project.slug.required}")
        @Pattern(regexp = "^[a-z0-9-]{3,100}$",
                message = "{validation.project.slug.pattern}")
        String slug,

        String shortDescription,
        String fullDescription,
        String imageUrl,
        String githubUrl,
        String demoUrl,
        LocalDate date,

        @NotNull(message = "{validation.project.status.required}") ProjectStatus status,

        boolean featured,
        Set<UUID> technologyIds) {

    public ProjectRequest {
        if (technologyIds == null) {
            technologyIds = Set.of();
        }
    }
}
