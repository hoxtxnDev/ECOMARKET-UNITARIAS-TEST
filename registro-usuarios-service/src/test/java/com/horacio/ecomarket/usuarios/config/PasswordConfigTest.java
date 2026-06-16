package com.horacio.ecomarket.usuarios.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PasswordConfig")
class PasswordConfigTest {

    private final PasswordConfig config = new PasswordConfig();

    @Test
    @DisplayName("passwordEncoder() retorna instancia de BCryptPasswordEncoder")
    void retornaBCrypt() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    @DisplayName("passwordEncoder() codifica y verifica correctamente")
    void codificaYVerifica() {
        PasswordEncoder encoder = config.passwordEncoder();
        String raw = "miPassword123";
        String encoded = encoder.encode(raw);

        assertThat(encoder.matches(raw, encoded)).isTrue();
        assertThat(encoder.matches("otraPass", encoded)).isFalse();
    }
}