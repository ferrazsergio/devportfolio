package dev.devportfolio.project.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.project.domain.Project;
import dev.devportfolio.project.domain.ProjectRepository;
import dev.devportfolio.project.domain.ProjectSlugAlreadyInUseException;
import dev.devportfolio.project.domain.ProjectStatus;
import dev.devportfolio.skill.application.SkillService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void rejectsDuplicateSlugForSameOwner() {
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(projectRepository.existsByPortfolioIdAndSlugIgnoreCase(PORTFOLIO_ID, "meu-projeto")).thenReturn(true);

        assertThatThrownBy(() -> projectService.create(OWNER_ID, "Meu Projeto", "meu-projeto", null, null, null,
                null, null, null, ProjectStatus.IN_PROGRESS, false, Set.of()))
                .isInstanceOf(ProjectSlugAlreadyInUseException.class);

        verify(projectRepository, never()).save(any());
    }

    @Test
    void rejectsTechnologyIdNotOwnedByCallersPortfolio() {
        UUID foreignSkillId = UUID.randomUUID();
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(skillService.findByPortfolioIdAndIdIn(PORTFOLIO_ID, Set.of(foreignSkillId))).thenReturn(List.of());

        assertThatThrownBy(() -> projectService.create(OWNER_ID, "Meu Projeto", "meu-projeto", null, null, null,
                null, null, null, ProjectStatus.IN_PROGRESS, false, Set.of(foreignSkillId)))
                .isInstanceOf(dev.devportfolio.shared.domain.NotFoundException.class);
    }
}
