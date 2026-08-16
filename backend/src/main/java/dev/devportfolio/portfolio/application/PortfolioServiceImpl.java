package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Portfolio;
import dev.devportfolio.portfolio.domain.PortfolioRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Portfolio createDraft(UUID ownerUserId) {
        return portfolioRepository.save(new Portfolio(ownerUserId));
    }
}
