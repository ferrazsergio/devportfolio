package dev.devportfolio.identity.presentation;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{validation.auth.login.email.required}") String email,
        @NotBlank(message = "{validation.auth.login.password.required}") String password) {
}
