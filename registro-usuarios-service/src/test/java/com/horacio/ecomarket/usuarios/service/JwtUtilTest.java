package com.horacio.ecomarket.usuarios.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtil")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEYwJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    private static final long EXPIRATION_MS = 60000;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION_MS);
    }

    @Nested
    @DisplayName("generarToken")
    class GenerarToken {

        @Test
        @DisplayName("genera un token JWT válido con los claims correctos")
        void generaTokenValido() {
            String token = jwtUtil.generarToken(1L, "test@eco.cl", List.of("ROLE_USER"));

            assertThat(token).isNotBlank();
            assertThat(jwtUtil.esTokenValido(token)).isTrue();
            assertThat(jwtUtil.obtenerUsuarioId(token)).isEqualTo(1L);
            assertThat(jwtUtil.obtenerCorreo(token)).isEqualTo("test@eco.cl");
            assertThat(jwtUtil.obtenerRoles(token)).containsExactly("ROLE_USER");
        }

        @Test
        @DisplayName("genera token con roles múltiples")
        void generaTokenConRolesMultiples() {
            String token = jwtUtil.generarToken(2L, "admin@eco.cl", List.of("ROLE_ADMIN", "ROLE_USER"));

            assertThat(jwtUtil.obtenerRoles(token)).hasSize(2);
            assertThat(jwtUtil.obtenerRoles(token)).contains("ROLE_ADMIN", "ROLE_USER");
        }
    }

    @Nested
    @DisplayName("esTokenValido")
    class EsTokenValido {

        @Test
        @DisplayName("retorna true para token válido")
        void tokenValido() {
            String token = jwtUtil.generarToken(1L, "test@eco.cl", List.of("ROLE_USER"));
            assertThat(jwtUtil.esTokenValido(token)).isTrue();
        }

        @Test
        @DisplayName("retorna false para token inválido")
        void tokenInvalido() {
            assertThat(jwtUtil.esTokenValido("token-invalido")).isFalse();
        }

        @Test
        @DisplayName("retorna false para token vacío")
        void tokenVacio() {
            assertThat(jwtUtil.esTokenValido("")).isFalse();
        }
    }

    @Nested
    @DisplayName("getExpirationMs")
    class GetExpirationMs {

        @Test
        @DisplayName("retorna el valor de expiración configurado")
        void retornaExpiracion() {
            assertThat(jwtUtil.getExpirationMs()).isEqualTo(EXPIRATION_MS);
        }
    }

    @Nested
    @DisplayName("obtenerExpiracion")
    class ObtenerExpiracion {

        @Test
        @DisplayName("retorna la fecha de expiración del token")
        void retornaFechaExpiracion() {
            String token = jwtUtil.generarToken(1L, "test@eco.cl", List.of("ROLE_USER"));
            assertThat(jwtUtil.obtenerExpiracion(token)).isNotNull();
        }
    }

    @Nested
    @DisplayName("validarYObtenerClaims")
    class ValidarYObtenerClaims {

        @Test
        @DisplayName("retorna los claims de un token válido")
        void retornaClaims() {
            String token = jwtUtil.generarToken(1L, "test@eco.cl", List.of("ROLE_USER"));
            Claims claims = jwtUtil.validarYObtenerClaims(token);

            assertThat(claims.getSubject()).isEqualTo("test@eco.cl");
            assertThat(claims.get("usuarioId", Long.class)).isEqualTo(1L);
        }
    }
}
