package dev.devportfolio.publicpage.application;

public interface PublicPageService {

    /**
     * @throws dev.devportfolio.shared.domain.NotFoundException se o username não existe
     *         ou se o portfólio está em DRAFT — mesma resposta para os dois casos (RF09),
     *         para não revelar a existência de portfólios não publicados.
     */
    PublicPortfolioView getByUsername(String username);
}
