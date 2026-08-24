package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.project.domain.Project;
import dev.devportfolio.project.domain.ProjectStatus;
import dev.devportfolio.skill.domain.Skill;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record PublicProjectResponse(String name, String slug, String shortDescription, String fullDescription,
        String imageUrl, String githubUrl, String demoUrl, LocalDate date, ProjectStatus status, boolean featured,
        int order, List<PublicSkillResponse> technologies) {

    public static PublicProjectResponse from(Project project, Map<UUID, Skill> skillsById) {
        List<PublicSkillResponse> technologies = project.getTechnologyIds().stream()
                .map(skillsById::get)
                .filter(Objects::nonNull)
                .map(PublicSkillResponse::from)
                .toList();
        return new PublicProjectResponse(project.getName(), project.getSlug(), project.getShortDescription(),
                project.getFullDescription(), project.getImageUrl(), project.getGithubUrl(), project.getDemoUrl(),
                project.getDate(), project.getStatus(), project.isFeatured(), project.getOrder(), technologies);
    }
}
