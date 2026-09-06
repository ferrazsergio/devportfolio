package dev.devportfolio.translation.application;

import dev.devportfolio.translation.infrastructure.DeepLClient;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ContentTranslationServiceImpl implements ContentTranslationService {

    private static final Logger log = LoggerFactory.getLogger(ContentTranslationServiceImpl.class);

    private final DeepLClient deepLClient;
    private final boolean enabled;

    public ContentTranslationServiceImpl(DeepLClient deepLClient,
            @Value("${app.translation.deepl.api-key:}") String apiKey) {
        this.deepLClient = deepLClient;
        this.enabled = apiKey != null && !apiKey.isBlank();
    }

    @Override
    public Optional<String> translateToEnglish(String text) {
        if (!enabled || text == null || text.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(deepLClient.translate(text));
        } catch (Exception e) {
            log.warn("Tradução automática falhou, seguindo sem versão em inglês", e);
            return Optional.empty();
        }
    }
}
