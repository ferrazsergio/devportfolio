package dev.devportfolio.publicpage.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.publicpage.application.PublicPortfolioView;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

class PublicPageHtmlRendererTest {

    private static final Locale PT = new Locale("pt");

    private static MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    private final PublicPageHtmlRenderer renderer = new PublicPageHtmlRenderer(messageSource());

    private PublicPortfolioView viewWithProfile(Profile profile) {
        return new PublicPortfolioView(profile, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
    }

    @Test
    void includesTitleDescriptionCanonicalAndOpenGraphTags() {
        Profile profile = new Profile(UUID.randomUUID());
        profile.update("Ana Souza", "ana-souza", "Desenvolvedora Java", null, "Bio longa", null,
                "São Paulo", null, null, null, null, null);
        profile.setPhotoUrl("https://example.com/photo.jpg");

        String html = renderer.render(viewWithProfile(profile), "https://devportfolio.example", "ana-souza", PT);

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
        profile.update("Ana <script>alert(1)</script>", "ana-souza", "\"Full-Stack\" & <b>bold</b>", null, null,
                null, null, null, null, null, null, null);

        String html = renderer.render(viewWithProfile(profile), "https://devportfolio.example", "ana-souza", PT);

        assertThat(html).doesNotContain("<script>");
        assertThat(html).contains("&lt;script&gt;");
        assertThat(html).contains("&amp;");
        assertThat(html).contains("&lt;b&gt;bold&lt;/b&gt;");
    }

    @Test
    void fallsBackToUsernameAndDefaultDescriptionWhenProfileFieldsAreBlank() {
        Profile profile = new Profile(UUID.randomUUID());
        profile.update(null, "sem-nome", null, null, null, null, null, null, null, null, null, null);

        String html = renderer.render(viewWithProfile(profile), "https://devportfolio.example", "sem-nome", PT);

        assertThat(html).contains("<title>sem-nome · DevPortfolio</title>");
        assertThat(html).contains("Portfólio de desenvolvedor(a) no DevPortfolio.");
        assertThat(html).doesNotContain("og:image");
        assertThat(html).contains("<meta name=\"twitter:card\" content=\"summary\">");
    }

    @Test
    void notFoundHtmlHasNoindexAndFriendlyMessage() {
        String html = renderer.renderNotFound(PT);

        assertThat(html).contains("noindex");
        assertThat(html).contains("não encontrado");
    }
}
