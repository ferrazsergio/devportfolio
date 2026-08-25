package dev.devportfolio.publicpage.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.publicpage.application.PublicPortfolioView;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PublicPageHtmlRendererTest {

    private final PublicPageHtmlRenderer renderer = new PublicPageHtmlRenderer();

    private PublicPortfolioView viewWithProfile(Profile profile) {
        return new PublicPortfolioView(profile, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
    }

    @Test
    void includesTitleDescriptionCanonicalAndOpenGraphTags() {
        Profile profile = new Profile(UUID.randomUUID());
        profile.update("Ana Souza", "ana-souza", "https://example.com/photo.jpg", "Desenvolvedora Java", "Bio longa",
                "São Paulo", null, null, null, null, null);

        String html = renderer.render(viewWithProfile(profile), "https://devportfolio.example", "ana-souza");

        assertThat(html).contains("<title>Ana Souza · DevPortfolio</title>");
        assertThat(html).contains("<meta name=\"description\" content=\"Desenvolvedora Java\">");
        assertThat(html).contains("<link rel=\"canonical\" href=\"https://devportfolio.example/ana-souza\">");
        assertThat(html).contains("<meta property=\"og:title\" content=\"Ana Souza\">");
        assertThat(html).contains("<meta property=\"og:url\" content=\"https://devportfolio.example/ana-souza\">");
        assertThat(html).contains("<meta property=\"og:image\" content=\"https://example.com/photo.jpg\">");
        assertThat(html).contains("summary_large_image");
    }

    @Test
    void escapesUserSuppliedContentToPreventHtmlInjection() {
        Profile profile = new Profile(UUID.randomUUID());
        profile.update("Ana <script>alert(1)</script>", "ana-souza", null, "\"Full-Stack\" & <b>bold</b>", null, null,
                null, null, null, null, null);

        String html = renderer.render(viewWithProfile(profile), "https://devportfolio.example", "ana-souza");

        assertThat(html).doesNotContain("<script>");
        assertThat(html).contains("&lt;script&gt;");
        assertThat(html).contains("&amp;");
        assertThat(html).contains("&lt;b&gt;bold&lt;/b&gt;");
    }

    @Test
    void fallsBackToUsernameAndDefaultDescriptionWhenProfileFieldsAreBlank() {
        Profile profile = new Profile(UUID.randomUUID());
        profile.update(null, "sem-nome", null, null, null, null, null, null, null, null, null);

        String html = renderer.render(viewWithProfile(profile), "https://devportfolio.example", "sem-nome");

        assertThat(html).contains("<title>sem-nome · DevPortfolio</title>");
        assertThat(html).contains("Portfólio de desenvolvedor(a) no DevPortfolio.");
        assertThat(html).doesNotContain("og:image");
        assertThat(html).contains("<meta name=\"twitter:card\" content=\"summary\">");
    }

    @Test
    void notFoundHtmlHasNoindexAndFriendlyMessage() {
        String html = renderer.renderNotFound();

        assertThat(html).contains("noindex");
        assertThat(html).contains("não encontrado");
    }
}
