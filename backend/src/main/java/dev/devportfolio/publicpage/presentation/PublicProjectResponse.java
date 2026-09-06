package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.project.domain.Project;
import dev.devportfolio.project.domain.ProjectStatus;
import dev.devportfolio.skill.domain.Skill;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record PublicProjectResponse(String name, String slug, String shortDescription, String fullDescription,
        String imageUrl, String githubUrl, String demoUrl, LocalDate date, ProjectStatus status, boolean featured,
        int order, List<PublicSkillResponse> technologies) {

    public static PublicProjectResponse from(Project project, Map<UUID, Skill> skillsById, Locale locale) {
        List<PublicSkillResponse> technologies = project.getTechnologyIds().stream()
                .map(skillsById::get)
                .filter(Objects::nonNull)
                .map(PublicSkillResponse::from)
                .toList();
        boolean english = "en".equals(locale.getLanguage());
        String shortDescription = english && project.getShortDescriptionEn() != null ? project.getShortDescriptionEn()
                : project.getShortDescription();
        String fullDescription = english && project.getFullDescriptionEn() != null ? project.getFullDescriptionEn()
                : project.getFullDescription();
        return new PublicProjectResponse(project.getName(), project.getSlug(), shortDescription,
                fullDescription, project.getImageUrl(), project.getGithubUrl(), project.getDemoUrl(),
                project.getDate(), project.getStatus(), project.isFeatured(), project.getOrder(), technologies);
    }
}
