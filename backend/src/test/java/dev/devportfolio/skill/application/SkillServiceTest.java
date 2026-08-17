package dev.devportfolio.skill.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.skill.domain.Skill;
import dev.devportfolio.skill.domain.SkillAlreadyExistsException;
import dev.devportfolio.skill.domain.SkillCategory;
import dev.devportfolio.skill.domain.SkillRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private SkillServiceImpl skillService;

    @Test
    void createsSkillWhenNameIsNotDuplicated() {
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(skillRepository.existsByPortfolioIdAndNameIgnoreCase(PORTFOLIO_ID, "Java")).thenReturn(false);
        Skill saved = new Skill(PORTFOLIO_ID, "Java", SkillCategory.BACKEND);
        when(skillRepository.save(any(Skill.class))).thenReturn(saved);

        Skill result = skillService.create(OWNER_ID, "Java", SkillCategory.BACKEND);

        assertThat(result.getName()).isEqualTo("Java");
        assertThat(result.getCategory()).isEqualTo(SkillCategory.BACKEND);
    }

    @Test
    void rejectsDuplicateSkillName() {
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(skillRepository.existsByPortfolioIdAndNameIgnoreCase(PORTFOLIO_ID, "Java")).thenReturn(true);

        assertThatThrownBy(() -> skillService.create(OWNER_ID, "Java", SkillCategory.BACKEND))
                .isInstanceOf(SkillAlreadyExistsException.class);

        verify(skillRepository, never()).save(any());
    }

    @Test
    void deletingUnknownSkillThrowsNotFound() {
        UUID skillId = UUID.randomUUID();
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(skillRepository.findByIdAndPortfolioId(skillId, PORTFOLIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> skillService.delete(OWNER_ID, skillId)).isInstanceOf(NotFoundException.class);
    }
}
