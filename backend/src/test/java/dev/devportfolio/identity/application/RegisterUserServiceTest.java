package dev.devportfolio.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import dev.devportfolio.identity.domain.EmailAlreadyInUseException;
import dev.devportfolio.identity.domain.User;
import dev.devportfolio.identity.domain.UserRepository;
import dev.devportfolio.portfolio.application.PortfolioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserService registerUserService;

    @Test
    void registersUserWithHashedPasswordAndProvisionsDraftPortfolio() {
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(passwordEncoder.encode("supersecret")).thenReturn("hashed-password");
        User saved = new User("Ana Souza", "ana@example.com", "hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = registerUserService.register("Ana Souza", "ana@example.com", "supersecret");

        assertThat(result.getEmail()).isEqualTo("ana@example.com");
        assertThat(result.getPasswordHash()).isEqualTo("hashed-password");
        verify(portfolioService).createDraft(saved.getId());
    }

    @Test
    void rejectsRegistrationWithDuplicateEmail() {
        when(userRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> registerUserService.register("Ana Souza", "ana@example.com", "supersecret"))
                .isInstanceOf(EmailAlreadyInUseException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(portfolioService);
    }
}
