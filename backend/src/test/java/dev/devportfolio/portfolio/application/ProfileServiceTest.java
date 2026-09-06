package dev.devportfolio.portfolio.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.portfolio.domain.ProfileRepository;
import dev.devportfolio.portfolio.domain.UsernameAlreadyInUseException;
import java.util.Optional;
import java.util.UUID;
import dev.devportfolio.translation.application.ContentTranslationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID PORTFOLIO_ID = UUID.randomUUID();

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ContentTranslationService translationService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    void updatesProfileWhenUsernameIsFree() {
        Profile profile = new Profile(PORTFOLIO_ID);
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(profileRepository.findByPortfolioId(PORTFOLIO_ID)).thenReturn(Optional.of(profile));
        when(profileRepository.existsByUsernameAndPortfolioIdNot("ana-souza", PORTFOLIO_ID)).thenReturn(false);

        Profile result = profileService.update(OWNER_ID, "Ana Souza", "ana-souza", null, null, null, null, null,
                null, null, null);

        assertThat(result.getUsername()).isEqualTo("ana-souza");
        assertThat(result.getFullName()).isEqualTo("Ana Souza");
    }

    @Test
    void rejectsUsernameAlreadyTakenByAnotherPortfolio() {
        Profile profile = new Profile(PORTFOLIO_ID);
        when(portfolioService.requirePortfolioId(OWNER_ID)).thenReturn(PORTFOLIO_ID);
        when(profileRepository.findByPortfolioId(PORTFOLIO_ID)).thenReturn(Optional.of(profile));
        when(profileRepository.existsByUsernameAndPortfolioIdNot("ana-souza", PORTFOLIO_ID)).thenReturn(true);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("Este nome de usuário já está em uso.");

        assertThatThrownBy(() -> profileService.update(OWNER_ID, "Ana Souza", "ana-souza", null, null, null, null,
                null, null, null, null)).isInstanceOf(UsernameAlreadyInUseException.class);
    }
}
