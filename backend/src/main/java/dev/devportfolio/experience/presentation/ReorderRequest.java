package dev.devportfolio.experience.presentation;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record ReorderRequest(@NotEmpty(message = "Lista de ids é obrigatória.") List<UUID> orderedIds) {
}
