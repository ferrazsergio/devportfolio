package dev.devportfolio.shared.domain;

/**
 * Falha ao chamar uma API externa (timeout, indisponibilidade, circuit breaker
 * aberto). Nunca deve vazar a causa original ao cliente — só uma mensagem
 * genérica (RNF01).
 */
public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
