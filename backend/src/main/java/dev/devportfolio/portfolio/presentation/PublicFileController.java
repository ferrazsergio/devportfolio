package dev.devportfolio.portfolio.presentation;

import dev.devportfolio.portfolio.infrastructure.ProfilePhotoStorage;
import dev.devportfolio.shared.domain.NotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Serve arquivos enviados pelo usuário (hoje, só fotos de perfil) sem autenticação — ver SecurityConfig. */
@Tag(name = "Arquivos públicos")
@RestController
@RequestMapping("/api/v1/public/files")
public class PublicFileController {

    private static final Map<String, MediaType> CONTENT_TYPES = Map.of(
            "jpg", MediaType.IMAGE_JPEG,
            "png", MediaType.IMAGE_PNG,
            "webp", MediaType.valueOf("image/webp"));

    private final ProfilePhotoStorage photoStorage;

    public PublicFileController(ProfilePhotoStorage photoStorage) {
        this.photoStorage = photoStorage;
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> get(@PathVariable String filename) {
        Path path = photoStorage.resolve(filename);
        if (!Files.isRegularFile(path)) {
            throw new NotFoundException("Arquivo não encontrado.");
        }

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        MediaType contentType = CONTENT_TYPES.getOrDefault(extension, MediaType.APPLICATION_OCTET_STREAM);
        try {
            return ResponseEntity.ok()
                    .contentType(contentType)
                    .contentLength(Files.size(path))
                    .body(new FileSystemResource(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
