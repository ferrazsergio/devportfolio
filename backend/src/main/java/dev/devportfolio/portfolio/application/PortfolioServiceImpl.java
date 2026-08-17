package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Portfolio;
import dev.devportfolio.portfolio.domain.PortfolioRepository;
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
}
