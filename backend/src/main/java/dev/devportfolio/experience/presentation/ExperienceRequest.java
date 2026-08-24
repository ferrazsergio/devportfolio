package dev.devportfolio.experience.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ExperienceRequest(
        @NotBlank(message = "Empresa é obrigatória.") String company,
        @NotBlank(message = "Cargo é obrigatório.") String role,
        String description,
        @NotNull(message = "Data de início é obrigatória.") LocalDate startDate,
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
