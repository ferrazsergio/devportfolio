package dev.devportfolio.experience.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ExperienceRequest(
        @NotBlank(message = "{validation.experience.company.required}") String company,
        @NotBlank(message = "{validation.experience.role.required}") String role,
        String description,
        @NotNull(message = "{validation.experience.startDate.required}") LocalDate startDate,
        LocalDate endDate,
        boolean current,
        String location,
        Set<UUID> technologyIds) {

    public ExperienceRequest {
        if (technologyIds == null) {
            technologyIds = Set.of();
        }
    }
}
