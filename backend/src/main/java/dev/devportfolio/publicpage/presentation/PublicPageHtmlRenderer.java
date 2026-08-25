package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.publicpage.application.PublicPortfolioView;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

/**
 * HTML mínimo com meta tags (SEO/Open Graph) para crawlers que não executam
 * JavaScript (LinkedInBot, facebookexternalhit, Twitterbot, WhatsApp, etc.).
 * Ver ADR-007 (dynamic rendering) — o Nginx encaminha só requisições de bots
 * conhecidos para este endpoint; usuários reais continuam recebendo a SPA.
 */
@Component
public class PublicPageHtmlRenderer {

    private static final int DESCRIPTION_MAX_LENGTH = 200;

    public String render(PublicPortfolioView view, String publicBaseUrl, String username) {
        Profile profile = view.profile();
        String displayName = firstNonBlank(profile.getFullName(), username);
        String title = displayName + " · DevPortfolio";
        String description = truncate(
                firstNonBlank(profile.getHeadline(), profile.getBio(), "Portfólio de desenvolvedor(a) no DevPortfolio."));
        String pageUrl = publicBaseUrl + "/" + username;
        String photoUrl = profile.getPhotoUrl();

        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>\n<html lang=\"pt-BR\"><head>\n");
        html.append("<meta charset=\"utf-8\">\n");
        html.append("<title>").append(esc(title)).append("</title>\n");
        html.append("<meta name=\"description\" content=\"").append(esc(description)).append("\">\n");
        html.append("<link rel=\"canonical\" href=\"").append(esc(pageUrl)).append("\">\n");
        html.append("<meta property=\"og:type\" content=\"profile\">\n");
        html.append("<meta property=\"og:title\" content=\"").append(esc(displayName)).append("\">\n");
        html.append("<meta property=\"og:description\" content=\"").append(esc(description)).append("\">\n");
        html.append("<meta property=\"og:url\" content=\"").append(esc(pageUrl)).append("\">\n");
        if (photoUrl != null && !photoUrl.isBlank()) {
            html.append("<meta property=\"og:image\" content=\"").append(esc(photoUrl)).append("\">\n");
            html.append("<meta name=\"twitter:card\" content=\"summary_large_image\">\n");
        } else {
            html.append("<meta name=\"twitter:card\" content=\"summary\">\n");
        }
        html.append("<meta name=\"twitter:title\" content=\"").append(esc(displayName)).append("\">\n");
        html.append("<meta name=\"twitter:description\" content=\"").append(esc(description)).append("\">\n");
        html.append("</head><body>\n");
        html.append("<h1>").append(esc(displayName)).append("</h1>\n");
        if (profile.getHeadline() != null && !profile.getHeadline().isBlank()) {
            html.append("<p>").append(esc(profile.getHeadline())).append("</p>\n");
        }
        if (profile.getBio() != null && !profile.getBio().isBlank()) {
            html.append("<p>").append(esc(profile.getBio())).append("</p>\n");
        }
        html.append("<p><a href=\"").append(esc(pageUrl)).append("\">Ver portfólio completo</a></p>\n");
        html.append("</body></html>\n");
        return html.toString();
    }

    public String renderNotFound() {
        return "<!doctype html>\n<html lang=\"pt-BR\"><head><meta charset=\"utf-8\">"
                + "<title>Portfólio não encontrado · DevPortfolio</title>"
                + "<meta name=\"robots\" content=\"noindex\"></head>"
                + "<body><h1>Portfólio não encontrado</h1>"
                + "<p>Esse portfólio não existe ou ainda não foi publicado.</p></body></html>\n";
    }

    private static String esc(String value) {
        // Passar o encoding evita que HtmlUtils escape acentos/caracteres UTF-8 válidos
        // como entidades HTML (ex.: "ó" -> "&oacute;") — só escapa o que é realmente
        // perigoso (& < > " '), já que a resposta é servida como text/html;charset=UTF-8.
        return HtmlUtils.htmlEscape(value, "UTF-8");
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private static String truncate(String value) {
        if (value.length() <= DESCRIPTION_MAX_LENGTH) {
            return value;
        }
        return value.substring(0, DESCRIPTION_MAX_LENGTH - 1).stripTrailing() + "…";
    }
}
