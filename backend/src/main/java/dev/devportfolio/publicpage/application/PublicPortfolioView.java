package dev.devportfolio.publicpage.application;

import dev.devportfolio.certification.domain.Certification;
import dev.devportfolio.education.domain.Education;
import dev.devportfolio.experience.domain.Experience;
import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.portfolio.domain.SocialLink;
import dev.devportfolio.project.domain.Project;
import dev.devportfolio.skill.domain.Skill;
import java.util.List;

/**
 * Agregado de leitura pública (RF09) — não é uma entidade persistida, apenas
 * o resultado já montado da junção dos módulos filhos de Portfolio.
 */
public record PublicPortfolioView(Profile profile, List<SocialLink> socialLinks, List<Experience> experiences,
        List<Project> projects, List<Skill> skills, List<Education> educations,
        List<Certification> certifications) {
}
