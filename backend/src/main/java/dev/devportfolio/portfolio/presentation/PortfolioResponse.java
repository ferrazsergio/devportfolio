package dev.devportfolio.portfolio.presentation;

import dev.devportfolio.portfolio.domain.Portfolio;
import dev.devportfolio.portfolio.domain.PortfolioStatus;
import java.util.UUID;

public record PortfolioResponse(UUID id, PortfolioStatus status) {

    public static PortfolioResponse from(Portfolio portfolio) {
        return new PortfolioResponse(portfolio.getId(), portfolio.getStatus());
    }
}
