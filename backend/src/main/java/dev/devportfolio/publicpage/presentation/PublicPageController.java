package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.publicpage.application.PublicPageService;
import dev.devportfolio.publicpage.application.PublicPortfolioView;
import dev.devportfolio.shared.domain.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public")
public class PublicPageController {

    private static final MediaType TEXT_HTML_UTF8 = MediaType.valueOf("text/html;charset=UTF-8");

    private final PublicPageService publicPageService;
    private final PublicPageHtmlRenderer htmlRenderer;
    private final String publicBaseUrl;

    public PublicPageController(PublicPageService publicPageService, PublicPageHtmlRenderer htmlRenderer,
            @Value("${app.public-base-url}") String publicBaseUrl) {
        this.publicPageService = publicPageService;
        this.htmlRenderer = htmlRenderer;
        this.publicBaseUrl = publicBaseUrl;
    }

    @GetMapping("/{username}")
    public PublicPortfolioResponse getByUsername(@PathVariable String username) {
        return PublicPortfolioResponse.from(publicPageService.getByUsername(username));
    }

    /**
     * HTML pré-renderizado para crawlers (RF13/RF14, ver ADR-007). Não é consumido
     * pela SPA — o Nginx encaminha aqui apenas requisições de bots conhecidos.
     */
    @GetMapping(value = "/{username}/meta", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getMetaHtml(@PathVariable String username) {
        try {
            PublicPortfolioView view = publicPageService.getByUsername(username);
            return ResponseEntity.ok().contentType(TEXT_HTML_UTF8)
                    .body(htmlRenderer.render(view, publicBaseUrl, username));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(TEXT_HTML_UTF8)
                    .body(htmlRenderer.renderNotFound());
        }
    }
}
