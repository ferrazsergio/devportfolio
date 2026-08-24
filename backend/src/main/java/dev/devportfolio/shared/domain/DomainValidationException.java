package dev.devportfolio.shared.domain;

/**
 * Violação de uma regra de negócio do domínio (ex.: invariantes de Experience),
 * distinta de MethodArgumentNotValidException (que cobre só formato/presença de
 * campo via Bean Validation). Mapeada para 400, igual à validação de campo.
 */
public class DomainValidationException extends RuntimeException {

    public DomainValidationException(String message) {
        super(message);
    }
}
