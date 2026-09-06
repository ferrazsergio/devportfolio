package dev.devportfolio.translation.infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

record DeepLTranslateRequest(List<String> text, @JsonProperty("target_lang") String targetLang) {
}
