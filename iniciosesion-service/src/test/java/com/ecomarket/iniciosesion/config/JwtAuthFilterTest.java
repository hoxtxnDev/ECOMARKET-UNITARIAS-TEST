package com.ecomarket.iniciosesion.config;

import com.ecomarket.iniciosesion.service.JwtUtil;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter")
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtAuthFilter filter;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final FilterChain filterChain = mock(FilterChain.class);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("doFilterInternal")
    class DoFilterInternal {

        @Test
        @DisplayName("setea authentication cuando el token es válido")
        void tokenValidoSeteaAuthentication() throws Exception {
            request.addHeader("Authorization", "Bearer token-valido");

            when(jwtUtil.esTokenValido("token-valido")).thenReturn(true);
            when(jwtUtil.obtenerCorreo("token-valido")).thenReturn("user@eco.cl");
            when(jwtUtil.obtenerRoles("token-valido")).thenReturn(List.of("ROLE_USER", "ROLE_ADMIN"));

            filter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.getPrincipal()).isEqualTo("user@eco.cl");
            assertThat(auth.getAuthorities())
                    .hasSize(2)
                    .extracting(Object::toString)
                    .containsExactly("ROLE_USER", "ROLE_ADMIN");
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("no setea authentication cuando no hay header Authorization")
        void sinHeaderNoSeteaAuthentication() throws Exception {
            filter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
            verifyNoInteractions(jwtUtil);
        }

        @Test
        @DisplayName("no setea authentication cuando el header no es Bearer")
        void headerSinBearerNoSeteaAuthentication() throws Exception {
            request.addHeader("Authorization", "Basic abc123");

            filter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verifyNoInteractions(jwtUtil);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("no setea authentication cuando el header está vacío")
        void headerVacioNoSeteaAuthentication() throws Exception {
            request.addHeader("Authorization", "");

            filter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verifyNoInteractions(jwtUtil);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("no setea authentication cuando el token es inválido")
        void tokenInvalidoNoSeteaAuthentication() throws Exception {
            request.addHeader("Authorization", "Bearer token-invalido");

            when(jwtUtil.esTokenValido("token-invalido")).thenReturn(false);

            filter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("no setea authentication cuando el token es malformado")
        void tokenMalformadoNoSeteaAuthentication() throws Exception {
            request.addHeader("Authorization", "Bearer ");

            filter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull();
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("siempre llama a filterChain.doFilter")
        void siempreLlamaFilterChain() throws Exception {
            request.addHeader("Authorization", "Bearer token-valido");

            when(jwtUtil.esTokenValido("token-valido")).thenReturn(true);
            when(jwtUtil.obtenerCorreo("token-valido")).thenReturn("user@eco.cl");
            when(jwtUtil.obtenerRoles("token-valido")).thenReturn(List.of("ROLE_USER"));

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("setea authentication con authorities vacías cuando roles es null")
        void rolesNullSeteaAuthenticationConAutoridadesVacias() throws Exception {
            request.addHeader("Authorization", "Bearer token-sin-roles");

            when(jwtUtil.esTokenValido("token-sin-roles")).thenReturn(true);
            when(jwtUtil.obtenerCorreo("token-sin-roles")).thenReturn("user@eco.cl");
            when(jwtUtil.obtenerRoles("token-sin-roles")).thenReturn(null);

            filter.doFilterInternal(request, response, filterChain);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.getPrincipal()).isEqualTo("user@eco.cl");
            assertThat(auth.getAuthorities()).isEmpty();
            verify(filterChain).doFilter(request, response);
        }
    }
}
