package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.publicpage.application.PublicPageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class PublicPageController {

    private final PublicPageService publicPageService;

    public PublicPageController(PublicPageService publicPageService) {
        this.publicPageService = publicPageService;
    }

    @GetMapping("/{username}")
    public PublicPortfolioResponse getByUsername(@PathVariable String username) {
        return PublicPortfolioResponse.from(publicPageService.getByUsername(username));
    }
}
