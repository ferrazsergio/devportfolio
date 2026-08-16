package dev.devportfolio.identity.application;

import dev.devportfolio.identity.domain.EmailAlreadyInUseException;
import dev.devportfolio.identity.domain.User;
import dev.devportfolio.identity.domain.UserRepository;
import dev.devportfolio.portfolio.application.PortfolioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserService {

    private final UserRepository userRepository;
    private final PortfolioService portfolioService;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserRepository userRepository, PortfolioService portfolioService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.portfolioService = portfolioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String name, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException();
        }
        User user = userRepository.save(new User(name, email, passwordEncoder.encode(rawPassword)));
        portfolioService.createDraft(user.getId());
        return user;
    }
}
