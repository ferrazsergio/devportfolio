package dev.devportfolio.education.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EducationRequest(
        @NotBlank(message = "Instituição é obrigatória.") String institution,
        @NotBlank(message = "Curso é obrigatório.") String course,
        String degree,
        @NotNull(message = "Data de início é obrigatória.") LocalDate startDate,
        LocalDate endDate,
        String description) {
}
