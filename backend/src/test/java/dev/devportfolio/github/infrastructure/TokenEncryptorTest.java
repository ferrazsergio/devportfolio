package dev.devportfolio.github.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Base64;
import java.security.SecureRandom;
import org.junit.jupiter.api.Test;

class TokenEncryptorTest {

    private static String randomBase64Key() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    @Test
    void decryptReturnsTheOriginalPlainText() {
        TokenEncryptor encryptor = new TokenEncryptor(randomBase64Key());

        String encrypted = encryptor.encrypt("ghu_supersecrettoken");

        assertThat(encryptor.decrypt(encrypted)).isEqualTo("ghu_supersecrettoken");
    }

    @Test
    void encryptingTheSameValueTwiceProducesDifferentCipherText() {
        TokenEncryptor encryptor = new TokenEncryptor(randomBase64Key());

        String first = encryptor.encrypt("same-token");
        String second = encryptor.encrypt("same-token");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void throwsWhenEncryptionKeyIsNotConfigured() {
        TokenEncryptor encryptor = new TokenEncryptor("");

        assertThatThrownBy(() -> encryptor.encrypt("token")).isInstanceOf(IllegalStateException.class);
    }
}
