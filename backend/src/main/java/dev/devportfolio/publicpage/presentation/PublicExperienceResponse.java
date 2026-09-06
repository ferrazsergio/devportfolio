package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.experience.domain.Experience;
import dev.devportfolio.skill.domain.Skill;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record PublicExperienceResponse(String company, String role, String description, LocalDate startDate,
        LocalDate endDate, boolean current, String location, int order, List<PublicSkillResponse> technologies) {

    public static PublicExperienceResponse from(Experience experience, Map<UUID, Skill> skillsById, Locale locale) {
        List<PublicSkillResponse> technologies = experience.getTechnologyIds().stream()
                .map(skillsById::get)
                .filter(Objects::nonNull)
                .map(PublicSkillResponse::from)
                .toList();
        boolean english = "en".equals(locale.getLanguage());
        String description = english && experience.getDescriptionEn() != null ? experience.getDescriptionEn()
                : experience.getDescription();
        return new PublicExperienceResponse(experience.getCompany(), experience.getRole(),
                description, experience.getStartDate(), experience.getEndDate(),
                experience.isCurrent(), experience.getLocation(), experience.getOrder(), technologies);
    }
}
