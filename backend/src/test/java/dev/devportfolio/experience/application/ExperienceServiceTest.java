package dev.devportfolio.experience.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.devportfolio.experience.domain.Experience;
import dev.devportfolio.experience.domain.ExperienceRepository;
import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.skill.application.SkillService;
import dev.devportfolio.skill.domain.Skill;
import dev.devportfolio.skill.domain.SkillCategory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private ExperienceServiceImpl experienceService;

    @Test
    void rejectsTechnologyIdNotOwnedByCallersPortfolio() {
        UUID foreignSkillId = UUID.randomUUID();
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(skillService.findByPortfolioIdAndIdIn(PORTFOLIO_ID, Set.of(foreignSkillId))).thenReturn(List.of());

        assertThatThrownBy(() -> experienceService.create(OWNER_ID, "Acme", "Dev", null, LocalDate.of(2020, 1, 1),
                null, true, null, Set.of(foreignSkillId))).isInstanceOf(NotFoundException.class);
    }

    @Test
    void createsExperienceWhenTechnologiesAreOwned() {
        UUID skillId = UUID.randomUUID();
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(skillService.findByPortfolioIdAndIdIn(PORTFOLIO_ID, Set.of(skillId)))
                .thenReturn(List.of(new Skill(PORTFOLIO_ID, "Java", SkillCategory.BACKEND)));
        when(experienceRepository.findByPortfolioIdOrderByOrderAsc(PORTFOLIO_ID)).thenReturn(List.of());
        Experience saved = new Experience(PORTFOLIO_ID, "Acme", "Dev", null, LocalDate.of(2020, 1, 1), null, true,
                null, 0, Set.of(skillId));
        when(experienceRepository.save(any(Experience.class))).thenReturn(saved);

        Experience result = experienceService.create(OWNER_ID, "Acme", "Dev", null, LocalDate.of(2020, 1, 1), null,
                true, null, Set.of(skillId));

        assertThat(result.getCompany()).isEqualTo("Acme");
    }
}
