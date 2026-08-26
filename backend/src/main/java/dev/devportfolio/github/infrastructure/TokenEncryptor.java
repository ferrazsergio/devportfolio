package dev.devportfolio.github.infrastructure;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cifra/decifra o access token do GitHub em repouso (AES-GCM). Diferente de
 * senha (hash irreversível via BCrypt), aqui precisamos do valor original de
 * volta para chamar a API do GitHub — ver ADR-008.
 */
@Component
public class TokenEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int GCM_IV_LENGTH_BYTES = 12;

    private final String base64Key;
    private final SecureRandom secureRandom = new SecureRandom();

    // A integração com GitHub é opcional (Fase 8) — a chave pode não estar
    // configurada em instâncias que não usam essa feature. Decodificar de forma
    // preguiçosa evita que a aplicação inteira falhe ao subir por causa disso.
    public TokenEncryptor(@Value("${app.github.token-encryption-key}") String base64Key) {
        this.base64Key = base64Key;
    }

    private SecretKeySpec resolveKey() {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException(
                    "APP_TOKEN_ENCRYPTION_KEY não configurada — necessária para usar a integração com GitHub.");
        }
        return new SecretKeySpec(Base64.getDecoder().decode(base64Key), "AES");
    }

    public String encrypt(String plainText) {
        try {
            SecretKeySpec key = resolveKey();
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + cipherText.length);
            buffer.put(iv).put(cipherText);
            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao cifrar token.", ex);
        }
    }

    public String decrypt(String encoded) {
        try {
            SecretKeySpec key = resolveKey();
            byte[] combined = Base64.getDecoder().decode(encoded);
            ByteBuffer buffer = ByteBuffer.wrap(combined);
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            buffer.get(iv);
            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(cipherText), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao decifrar token.", ex);
        }
    }
}
