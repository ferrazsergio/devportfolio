package dev.devportfolio.portfolio.presentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequest(
        @NotBlank(message = "Nome completo é obrigatório.") String fullName,

        @NotBlank(message = "Nome de usuário é obrigatório.")
        @Pattern(regexp = "^[a-z0-9-]{3,50}$",
                message = "Nome de usuário deve ter entre 3 e 50 caracteres, apenas letras minúsculas, números e hífen.")
        String username,

        String photoUrl,
        String headline,
        String bio,
        String location,

        @Email(message = "Email profissional deve ser válido.") String professionalEmail,

        String phone,
        String githubUrl,
        String linkedinUrl,
        String websiteUrl) {
}
