package dev.devportfolio.github.presentation;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record GithubImportRequest(@NotEmpty(message = "Selecione ao menos um repositório.") List<String> fullNames) {
}
