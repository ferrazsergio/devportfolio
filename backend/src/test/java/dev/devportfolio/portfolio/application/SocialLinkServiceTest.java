package dev.devportfolio.portfolio.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.devportfolio.portfolio.domain.SocialLink;
import dev.devportfolio.portfolio.domain.SocialLinkRepository;
import dev.devportfolio.shared.domain.NotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SocialLinkServiceTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Mock
    private SocialLinkRepository socialLinkRepository;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private SocialLinkServiceImpl socialLinkService;

    @Test
    void createsSocialLinkUnderCallersPortfolio() {
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        SocialLink saved = new SocialLink(PORTFOLIO_ID, "GitHub", "https://github.com/ana", 0);
        when(socialLinkRepository.save(any(SocialLink.class))).thenReturn(saved);

        SocialLink result = socialLinkService.create(OWNER_ID, "GitHub", "https://github.com/ana", 0);

        assertThat(result.getPlatform()).isEqualTo("GitHub");
    }

    @Test
    void updatingUnknownSocialLinkThrowsNotFound() {
        UUID socialLinkId = UUID.randomUUID();
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(socialLinkRepository.findByIdAndPortfolioId(socialLinkId, PORTFOLIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> socialLinkService.update(OWNER_ID, socialLinkId, "GitHub", "url", 0))
                .isInstanceOf(NotFoundException.class);
    }
}
