package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.education.domain.Education;
import java.time.LocalDate;

public record PublicEducationResponse(String institution, String course, String degree, LocalDate startDate,
        LocalDate endDate, String description) {

    public static PublicEducationResponse from(Education education) {
        return new PublicEducationResponse(education.getInstitution(), education.getCourse(), education.getDegree(),
                education.getStartDate(), education.getEndDate(), education.getDescription());
    }
}
