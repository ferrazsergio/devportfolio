package dev.devportfolio.certification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.devportfolio.certification.domain.Certification;
import dev.devportfolio.certification.domain.CertificationRepository;
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
class CertificationServiceTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private CertificationServiceImpl certificationService;

    @Test
    void createsCertificationUnderCallersPortfolio() {
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        Certification saved = new Certification(PORTFOLIO_ID, "AWS SAA", "AWS", LocalDate.of(2023, 1, 1), null, null,
                null);
        when(certificationRepository.save(any(Certification.class))).thenReturn(saved);

        Certification result = certificationService.create(OWNER_ID, "AWS SAA", "AWS", LocalDate.of(2023, 1, 1),
                null, null, null);

        assertThat(result.getName()).isEqualTo("AWS SAA");
    }

    @Test
    void deletingUnknownCertificationThrowsNotFound() {
        UUID certificationId = UUID.randomUUID();
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(certificationRepository.findByIdAndPortfolioId(certificationId, PORTFOLIO_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> certificationService.delete(OWNER_ID, certificationId))
                .isInstanceOf(NotFoundException.class);
    }
}
