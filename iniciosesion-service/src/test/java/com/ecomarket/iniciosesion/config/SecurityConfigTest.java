package com.ecomarket.iniciosesion.config;

import com.ecomarket.iniciosesion.service.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig")
class SecurityConfigTest {

    private final SecurityConfig config = new SecurityConfig();

    @Mock
    private JwtUtil jwtUtil;

    @Nested
    @DisplayName("jwtAuthFilter")
    class JwtAuthFilterBean {

        @Test
        @DisplayName("crea JwtAuthFilter con JwtUtil")
        void creaJwtAuthFilter() {
            JwtAuthFilter filter = config.jwtAuthFilter(jwtUtil);
            assertThat(filter).isNotNull();
        }
    }

    @Nested
    @DisplayName("corsConfigurationSource")
    class CorsConfigurationSourceTest {

        @Test
        @DisplayName("retorna config con métodos permitidos por defecto más DELETE y PUT")
        void permiteMetodosPorDefectoYExtra() {
            var source = config.corsConfigurationSource();
            var corsConfig = source.getCorsConfiguration(new MockHttpServletRequest());

            assertThat(corsConfig.getAllowedMethods())
                    .contains("GET", "POST", "HEAD", "DELETE", "PUT");
        }
    }
}
