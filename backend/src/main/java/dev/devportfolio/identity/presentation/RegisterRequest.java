package dev.devportfolio.identity.presentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "{validation.auth.register.name.required}") String name,

        @NotBlank(message = "{validation.auth.register.email.required}")
        @Email(message = "{validation.auth.register.email.valid}") String email,

        @NotBlank(message = "{validation.auth.register.password.required}")
        @Size(min = 8, message = "{validation.auth.register.password.size}") String password) {
}
