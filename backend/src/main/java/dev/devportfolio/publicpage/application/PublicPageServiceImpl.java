package dev.devportfolio.publicpage.application;

import dev.devportfolio.certification.application.CertificationService;
import dev.devportfolio.education.application.EducationService;
import dev.devportfolio.experience.application.ExperienceService;
import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.portfolio.application.ProfileService;
import dev.devportfolio.portfolio.application.SocialLinkService;
import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.project.application.ProjectService;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.skill.application.SkillService;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PublicPageServiceImpl implements PublicPageService {

    private static final String NOT_FOUND_MESSAGE = "Portfólio não encontrado.";

    private final ProfileService profileService;
    private final PortfolioService portfolioService;
    private final SocialLinkService socialLinkService;
    private final ExperienceService experienceService;
    private final ProjectService projectService;
    private final SkillService skillService;
    private final EducationService educationService;
    private final CertificationService certificationService;

    public PublicPageServiceImpl(ProfileService profileService, PortfolioService portfolioService,
            SocialLinkService socialLinkService, ExperienceService experienceService, ProjectService projectService,
            SkillService skillService, EducationService educationService,
            CertificationService certificationService) {
        this.profileService = profileService;
        this.portfolioService = portfolioService;
        this.socialLinkService = socialLinkService;
        this.experienceService = experienceService;
        this.projectService = projectService;
        this.skillService = skillService;
        this.educationService = educationService;
        this.certificationService = certificationService;
    }

    @Override
    public PublicPortfolioView getByUsername(String username) {
        Profile profile = profileService.findByUsername(username).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

        // Mesma resposta (404) para "não existe" e "existe mas está em DRAFT" — RF09,
        // não revela a um visitante que um username específico já foi cadastrado.
        if (!portfolioService.isPublished(profile.getPortfolioId())) {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }

        UUID portfolioId = profile.getPortfolioId();
        return new PublicPortfolioView(profile, socialLinkService.listByPortfolioId(portfolioId),
                experienceService.listByPortfolioId(portfolioId), projectService.listByPortfolioId(portfolioId),
                skillService.listByPortfolioId(portfolioId), educationService.listByPortfolioId(portfolioId),
                certificationService.listByPortfolioId(portfolioId));
    }
}
