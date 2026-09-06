package dev.devportfolio.portfolio.infrastructure;

import dev.devportfolio.shared.domain.DomainValidationException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Guarda a foto de perfil em disco local (Docker volume no self-hosted), sem
 * depender de um serviço de storage dedicado — ver docs/07-backlog.md Fase 9,
 * que adia "storage em serviço dedicado" pra depois da validação do produto.
 */
@Component
public class ProfilePhotoStorage {

    private static final Map<String, String> ALLOWED_CONTENT_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    private final Path storageDir;
    private final MessageSource messageSource;

    public ProfilePhotoStorage(@Value("${app.storage.path}") String storagePath, MessageSource messageSource) {
        this.storageDir = Path.of(storagePath);
        this.messageSource = messageSource;
    }

    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DomainValidationException(
                    messageSource.getMessage("error.profilePhoto.empty", null, LocaleContextHolder.getLocale()));
        }
        String extension = ALLOWED_CONTENT_TYPES.get(file.getContentType());
        if (extension == null) {
            throw new DomainValidationException(
                    messageSource.getMessage("error.profilePhoto.invalidFormat", null, LocaleContextHolder.getLocale()));
        }

        String filename = UUID.randomUUID() + "." + extension;
        try {
            Files.createDirectories(storageDir);
            file.transferTo(storageDir.resolve(filename));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar a foto de perfil.", e);
        }
        return filename;
    }

    public void delete(String filename) {
        if (filename == null || !isSafeFilename(filename)) {
            return;
        }
        try {
            Files.deleteIfExists(storageDir.resolve(filename));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao remover a foto de perfil.", e);
        }
    }

    public Path resolve(String filename) {
        if (!isSafeFilename(filename)) {
            throw new DomainValidationException(
                    messageSource.getMessage("error.profilePhoto.invalidFilename", null, LocaleContextHolder.getLocale()));
        }
        return storageDir.resolve(filename);
    }

    private boolean isSafeFilename(String filename) {
        return !filename.contains("/") && !filename.contains("\\") && !filename.contains("..")
                && Set.of("jpg", "png", "webp").contains(extensionOf(filename));
    }

    private String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "";
    }
}
