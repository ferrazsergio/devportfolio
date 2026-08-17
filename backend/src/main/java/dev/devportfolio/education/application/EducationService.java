package dev.devportfolio.education.application;

import dev.devportfolio.education.domain.Education;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EducationService {

    List<Education> list(UUID ownerUserId);

    Education create(UUID ownerUserId, String institution, String course, String degree, LocalDate startDate,
            LocalDate endDate, String description);

    Education update(UUID ownerUserId, UUID educationId, String institution, String course, String degree,
            LocalDate startDate, LocalDate endDate, String description);

    void delete(UUID ownerUserId, UUID educationId);
}
