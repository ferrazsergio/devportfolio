package dev.devportfolio.translation.application;

import java.util.Optional;

/**
 * Tradução automática (EN) de campos de prosa livre cadastrados pelo usuário — ver
 * ADR-006 (i18n como camada adicional) e docs de i18n. Nunca lança: ausência de
 * tradução (chave não configurada, DeepL fora do ar, texto vazio) é um resultado
 * válido (Optional.empty()), não um erro — o conteúdo original sempre continua
 * disponível independente desta feature funcionar ou não.
 */
public interface ContentTranslationService {

    Optional<String> translateToEnglish(String text);
}
