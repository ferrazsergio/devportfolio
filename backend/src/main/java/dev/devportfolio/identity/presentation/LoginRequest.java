package dev.devportfolio.identity.presentation;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email é obrigatório.") String email,
        @NotBlank(message = "Senha é obrigatória.") String password) {
}
