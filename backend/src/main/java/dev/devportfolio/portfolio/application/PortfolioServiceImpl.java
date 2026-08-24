package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Portfolio;
import dev.devportfolio.portfolio.domain.PortfolioRepository;
import dev.devportfolio.portfolio.domain.PortfolioStatus;
import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.portfolio.domain.ProfileRepository;
import dev.devportfolio.shared.domain.NotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ProfileRepository profileRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository, ProfileRepository profileRepository) {
        this.portfolioRepository = portfolioRepository;
        this.profileRepository = profileRepository;
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
                .orElseThrow(() -> new NotFoundException("Portfólio não encontrado."));
    }

    @Override
    public Portfolio getByOwner(UUID ownerUserId) {
        return portfolioRepository.findByOwnerUserId(ownerUserId)
                .orElseThrow(() -> new NotFoundException("Portfólio não encontrado."));
    }

    @Override
    @Transactional
    public Portfolio updateStatus(UUID ownerUserId, PortfolioStatus status) {
        Portfolio portfolio = portfolioRepository.findByOwnerUserId(ownerUserId)
                .orElseThrow(() -> new NotFoundException("Portfólio não encontrado."));
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
