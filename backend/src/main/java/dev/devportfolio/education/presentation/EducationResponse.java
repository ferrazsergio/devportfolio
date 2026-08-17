package dev.devportfolio.education.presentation;

import dev.devportfolio.education.domain.Education;
import java.time.LocalDate;
import java.util.UUID;

public record EducationResponse(UUID id, String institution, String course, String degree, LocalDate startDate,
        LocalDate endDate, String description) {

    public static EducationResponse from(Education education) {
        return new EducationResponse(education.getId(), education.getInstitution(), education.getCourse(),
                education.getDegree(), education.getStartDate(), education.getEndDate(), education.getDescription());
    }
}
