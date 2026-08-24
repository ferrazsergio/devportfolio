package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.publicpage.application.PublicPortfolioView;
import dev.devportfolio.skill.domain.Skill;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PublicPortfolioResponse(PublicProfileResponse profile, List<PublicExperienceResponse> experiences,
        List<PublicProjectResponse> projects, List<PublicSkillResponse> skills,
        List<PublicEducationResponse> educations, List<PublicCertificationResponse> certifications) {

    public static PublicPortfolioResponse from(PublicPortfolioView view) {
        Map<UUID, Skill> skillsById = view.skills().stream()
                .collect(Collectors.toMap(Skill::getId, Function.identity()));

        List<PublicSocialLinkResponse> socialLinks = view.socialLinks().stream()
                .map(PublicSocialLinkResponse::from)
                .toList();

        return new PublicPortfolioResponse(
                PublicProfileResponse.from(view.profile(), socialLinks),
                view.experiences().stream().map(experience -> PublicExperienceResponse.from(experience, skillsById))
                        .toList(),
                view.projects().stream().map(project -> PublicProjectResponse.from(project, skillsById)).toList(),
                view.skills().stream().map(PublicSkillResponse::from).toList(),
                view.educations().stream().map(PublicEducationResponse::from).toList(),
                view.certifications().stream().map(PublicCertificationResponse::from).toList());
    }
}
