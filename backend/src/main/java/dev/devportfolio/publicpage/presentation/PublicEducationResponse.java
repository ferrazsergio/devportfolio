package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.education.domain.Education;
import java.time.LocalDate;
import java.util.Locale;

public record PublicEducationResponse(String institution, String course, String degree, LocalDate startDate,
        LocalDate endDate, String description) {

    public static PublicEducationResponse from(Education education, Locale locale) {
        boolean english = "en".equals(locale.getLanguage());
        String description = english && education.getDescriptionEn() != null ? education.getDescriptionEn()
                : education.getDescription();
        return new PublicEducationResponse(education.getInstitution(), education.getCourse(), education.getDegree(),
                education.getStartDate(), education.getEndDate(), description);
    }
}
