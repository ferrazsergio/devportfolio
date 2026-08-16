package dev.devportfolio.shared.presentation;

import java.util.List;

public record ErrorResponse(String traceId, String message, List<FieldError> errors) {

    public record FieldError(String field, String reason) {
    }
}
