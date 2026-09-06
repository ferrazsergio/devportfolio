package dev.devportfolio.translation.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

record DeepLTranslateResponse(List<Translation> translations) {

    record Translation(String text, @JsonProperty("detected_source_language") String detectedSourceLanguage) {
    }
}
