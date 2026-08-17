package dev.devportfolio.education.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.devportfolio.education.domain.Education;
import dev.devportfolio.education.domain.EducationRepository;
import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.shared.domain.NotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private EducationServiceImpl educationService;

    @Test
    void createsEducationUnderCallersPortfolio() {
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        Education saved = new Education(PORTFOLIO_ID, "USP", "Ciência da Computação", "Bacharelado",
                LocalDate.of(2015, 1, 1), LocalDate.of(2019, 12, 1), null);
        when(educationRepository.save(any(Education.class))).thenReturn(saved);

        Education result = educationService.create(OWNER_ID, "USP", "Ciência da Computação", "Bacharelado",
                LocalDate.of(2015, 1, 1), LocalDate.of(2019, 12, 1), null);

        assertThat(result.getInstitution()).isEqualTo("USP");
    }

    @Test
    void updatingUnknownEducationThrowsNotFound() {
        UUID educationId = UUID.randomUUID();
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(educationRepository.findByIdAndPortfolioId(educationId, PORTFOLIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> educationService.update(OWNER_ID, educationId, "USP", "Curso", null,
                LocalDate.now(), null, null)).isInstanceOf(NotFoundException.class);
    }
}
