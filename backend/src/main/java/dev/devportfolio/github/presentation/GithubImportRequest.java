package dev.devportfolio.github.presentation;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record GithubImportRequest(@NotEmpty(message = "{validation.github.import.required}") List<String> fullNames) {
}
