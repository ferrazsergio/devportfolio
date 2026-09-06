package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.portfolio.domain.ProfileRepository;
import dev.devportfolio.portfolio.domain.UsernameAlreadyInUseException;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.translation.application.ContentTranslationService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final PortfolioService portfolioService;
    private final MessageSource messageSource;
    private final ContentTranslationService translationService;

    public ProfileServiceImpl(ProfileRepository profileRepository, PortfolioService portfolioService,
            MessageSource messageSource, ContentTranslationService translationService) {
        this.profileRepository = profileRepository;
        this.portfolioService = portfolioService;
        this.messageSource = messageSource;
        this.translationService = translationService;
    }

    @Override
    public Profile getByOwner(UUID ownerUserId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return profileRepository.findByPortfolioId(portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.profile.notFound", null, LocaleContextHolder.getLocale())));
    }

    @Override
    public Optional<Profile> findByUsername(String username) {
        return profileRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    @Transactional
    public Profile update(UUID ownerUserId, String fullName, String username, String headline,
            String bio, String location, String professionalEmail, String phone, String githubUrl,
            String linkedinUrl, String websiteUrl) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Profile profile = profileRepository.findByPortfolioId(portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.profile.notFound", null, LocaleContextHolder.getLocale())));

        if (profileRepository.existsByUsernameAndPortfolioIdNot(username, portfolioId)) {
            throw new UsernameAlreadyInUseException(
                    messageSource.getMessage("error.username.alreadyInUse", null, LocaleContextHolder.getLocale()));
        }

        String headlineEn = translationService.translateToEnglish(headline).orElse(null);
        String bioEn = translationService.translateToEnglish(bio).orElse(null);
        profile.update(fullName, username, headline, headlineEn, bio, bioEn, location, professionalEmail, phone,
                githubUrl, linkedinUrl, websiteUrl);
        return profile;
    }

    @Override
    @Transactional
    public Profile updatePhoto(UUID ownerUserId, String photoUrl) {
        Profile profile = getByOwner(ownerUserId);
        profile.setPhotoUrl(photoUrl);
        return profile;
    }
}
