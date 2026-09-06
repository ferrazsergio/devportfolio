package dev.devportfolio.education.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EducationRequest(
        @NotBlank(message = "{validation.education.institution.required}") String institution,
        @NotBlank(message = "{validation.education.course.required}") String course,
        String degree,
        @NotNull(message = "{validation.education.startDate.required}") LocalDate startDate,
        LocalDate endDate,
        String description) {
}
