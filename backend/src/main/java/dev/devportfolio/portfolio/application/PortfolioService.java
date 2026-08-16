package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Portfolio;
import java.util.UUID;

/**
 * Porta pública do módulo portfolio, usada por outros módulos (ex.: identity)
 * sem que precisem conhecer PortfolioRepository ou a camada de infraestrutura.
 */
public interface PortfolioService {

    Portfolio createDraft(UUID ownerUserId);
}
