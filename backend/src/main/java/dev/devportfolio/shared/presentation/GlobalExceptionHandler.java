package dev.devportfolio.shared.presentation;

import dev.devportfolio.shared.domain.ConflictException;
import dev.devportfolio.shared.domain.DomainValidationException;
import dev.devportfolio.shared.domain.ExternalServiceException;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.shared.infrastructure.TraceIdFilter;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Traduz exceções para o payload de erro padronizado (traceId, message, errors[]),
 * garantindo que nenhuma stack trace ou mensagem interna vaze ao cliente (RNF01).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return new ErrorResponse(traceId(), messageSource.getMessage("error.validation.failed", null, LocaleContextHolder.getLocale()), errors);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(ConflictException ex) {
        return new ErrorResponse(traceId(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(DomainValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDomainValidation(DomainValidationException ex) {
        return new ErrorResponse(traceId(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException ex) {
        return new ErrorResponse(traceId(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleExternalService(ExternalServiceException ex) {
        String traceId = traceId();
        log.warn("Falha ao chamar serviço externo, traceId={}", traceId, ex);
        return new ErrorResponse(traceId, ex.getMessage(), List.of());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        return new ErrorResponse(traceId(), messageSource.getMessage("error.file.tooLarge", null, LocaleContextHolder.getLocale()), List.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthentication(AuthenticationException ex) {
        return new ErrorResponse(traceId(), messageSource.getMessage("error.auth.invalidCredentials", null, LocaleContextHolder.getLocale()), List.of());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpected(Exception ex) {
        String traceId = traceId();
        log.error("Erro inesperado, traceId={}", traceId, ex);
        return new ErrorResponse(traceId, messageSource.getMessage("error.internal.unexpected", null, LocaleContextHolder.getLocale()), List.of());
    }

    private String traceId() {
        String traceId = MDC.get(TraceIdFilter.TRACE_ID_MDC_KEY);
        return traceId != null ? traceId : UUID.randomUUID().toString();
    }
}
