package dev.devportfolio.publicpage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublicPageServiceTest {

    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Mock
    private ProfileService profileService;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private SocialLinkService socialLinkService;

    @Mock
    private ExperienceService experienceService;

    @Mock
    private ProjectService projectService;

    @Mock
    private SkillService skillService;

    @Mock
    private EducationService educationService;

    @Mock
    private CertificationService certificationService;

    @InjectMocks
    private PublicPageServiceImpl publicPageService;

    @Test
    void rejectsUnknownUsername() {
        when(profileService.findByUsername("desconhecido")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> publicPageService.getByUsername("desconhecido"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void rejectsDraftPortfolioSameAsUnknown() {
        Profile profile = new Profile(PORTFOLIO_ID);
        when(profileService.findByUsername("ana")).thenReturn(Optional.of(profile));
        when(portfolioService.isPublished(PORTFOLIO_ID)).thenReturn(false);

        assertThatThrownBy(() -> publicPageService.getByUsername("ana")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void aggregatesAllSectionsForPublishedPortfolio() {
        Profile profile = new Profile(PORTFOLIO_ID);
        when(profileService.findByUsername("ana")).thenReturn(Optional.of(profile));
        when(portfolioService.isPublished(PORTFOLIO_ID)).thenReturn(true);
        when(socialLinkService.listByPortfolioId(PORTFOLIO_ID)).thenReturn(List.of());
        when(experienceService.listByPortfolioId(PORTFOLIO_ID)).thenReturn(List.of());
        when(projectService.listByPortfolioId(PORTFOLIO_ID)).thenReturn(List.of());
        when(skillService.listByPortfolioId(PORTFOLIO_ID)).thenReturn(List.of());
        when(educationService.listByPortfolioId(PORTFOLIO_ID)).thenReturn(List.of());
        when(certificationService.listByPortfolioId(PORTFOLIO_ID)).thenReturn(List.of());

        PublicPortfolioView view = publicPageService.getByUsername("ana");

        assertThat(view.profile()).isEqualTo(profile);
    }
}
