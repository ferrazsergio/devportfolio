package dev.devportfolio.portfolio.presentation;

import dev.devportfolio.portfolio.domain.PortfolioStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePortfolioStatusRequest(@NotNull(message = "{validation.portfolio.status.required}") PortfolioStatus status) {
}
