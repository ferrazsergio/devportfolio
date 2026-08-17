package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Portfolio;
import java.util.UUID;

/**
 * Porta pública do módulo portfolio, usada por outros módulos (ex.: identity)
 * sem que precisem conhecer PortfolioRepository ou a camada de infraestrutura.
 */
public interface PortfolioService {

    Portfolio createDraft(UUID ownerUserId);

    /**
     * Resolve o id do portfólio do usuário autenticado. É a base da checagem de
     * posse (RNF01/anti-IDOR): todo módulo com recursos filhos de Portfolio
     * escopa suas queries por esse id, nunca por um id vindo diretamente da URL.
     */
    UUID requirePortfolioId(UUID ownerUserId);
}
