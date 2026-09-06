package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Portfolio;
import dev.devportfolio.portfolio.domain.PortfolioRepository;
import dev.devportfolio.portfolio.domain.PortfolioStatus;
import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.portfolio.domain.ProfileRepository;
import dev.devportfolio.shared.domain.NotFoundException;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ProfileRepository profileRepository;
    private final MessageSource messageSource;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository, ProfileRepository profileRepository,
            MessageSource messageSource) {
        this.portfolioRepository = portfolioRepository;
        this.profileRepository = profileRepository;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public Portfolio createDraft(UUID ownerUserId) {
        Portfolio portfolio = portfolioRepository.save(new Portfolio(ownerUserId));
        profileRepository.save(new Profile(portfolio.getId()));
        return portfolio;
    }

    @Override
    public UUID requirePortfolioId(UUID ownerUserId) {
        return portfolioRepository.findByOwnerUserId(ownerUserId)
                .map(Portfolio::getId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.portfolio.notFound", null, LocaleContextHolder.getLocale())));
    }

    @Override
    public Portfolio getByOwner(UUID ownerUserId) {
        return portfolioRepository.findByOwnerUserId(ownerUserId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.portfolio.notFound", null, LocaleContextHolder.getLocale())));
    }

    @Override
    @Transactional
    public Portfolio updateStatus(UUID ownerUserId, PortfolioStatus status) {
        Portfolio portfolio = portfolioRepository.findByOwnerUserId(ownerUserId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.portfolio.notFound", null, LocaleContextHolder.getLocale())));
        portfolio.updateStatus(status);
        return portfolio;
    }

    @Override
    public boolean isPublished(UUID portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .map(portfolio -> portfolio.getStatus() == PortfolioStatus.PUBLISHED)
                .orElse(false);
    }
}
