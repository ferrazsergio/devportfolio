package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.portfolio.domain.ProfileRepository;
import dev.devportfolio.portfolio.domain.UsernameAlreadyInUseException;
import dev.devportfolio.shared.domain.NotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final PortfolioService portfolioService;

    public ProfileServiceImpl(ProfileRepository profileRepository, PortfolioService portfolioService) {
        this.profileRepository = profileRepository;
        this.portfolioService = portfolioService;
    }

    @Override
    public Profile getByOwner(UUID ownerUserId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return profileRepository.findByPortfolioId(portfolioId)
                .orElseThrow(() -> new NotFoundException("Perfil não encontrado."));
    }

    @Override
    @Transactional
    public Profile update(UUID ownerUserId, String fullName, String username, String photoUrl, String headline,
            String bio, String location, String professionalEmail, String phone, String githubUrl,
            String linkedinUrl, String websiteUrl) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Profile profile = profileRepository.findByPortfolioId(portfolioId)
                .orElseThrow(() -> new NotFoundException("Perfil não encontrado."));

        if (profileRepository.existsByUsernameAndPortfolioIdNot(username, portfolioId)) {
            throw new UsernameAlreadyInUseException();
        }

        profile.update(fullName, username, photoUrl, headline, bio, location, professionalEmail, phone, githubUrl,
                linkedinUrl, websiteUrl);
        return profile;
    }
}
