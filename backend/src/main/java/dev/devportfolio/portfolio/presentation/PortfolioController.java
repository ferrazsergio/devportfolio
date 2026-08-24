package dev.devportfolio.portfolio.presentation;

import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.portfolio.domain.Portfolio;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public PortfolioResponse get(@AuthenticationPrincipal AuthenticatedUser principal) {
        Portfolio portfolio = portfolioService.getByOwner(principal.getUser().getId());
        return PortfolioResponse.from(portfolio);
    }

    @PatchMapping("/status")
    public PortfolioResponse updateStatus(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody UpdatePortfolioStatusRequest request) {
        Portfolio portfolio = portfolioService.updateStatus(principal.getUser().getId(), request.status());
        return PortfolioResponse.from(portfolio);
    }
}
