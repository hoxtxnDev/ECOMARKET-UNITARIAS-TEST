package com.horacio.ecomarket.usuarios.service;

import com.horacio.ecomarket.usuarios.dto.*;
import com.horacio.ecomarket.usuarios.exception.*;
import com.horacio.ecomarket.usuarios.model.Credencial;
import com.horacio.ecomarket.usuarios.model.SesionJWT;
import com.horacio.ecomarket.usuarios.model.TokenRecuperacion;
import com.horacio.ecomarket.usuarios.repository.CredencialRepository;
import com.horacio.ecomarket.usuarios.repository.SesionJWTRepository;
import com.horacio.ecomarket.usuarios.repository.TokenRecuperacionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private CredencialRepository credencialRepository;
    @Mock
    private TokenRecuperacionRepository tokenRecuperacionRepository;
    @Mock
    private SesionJWTRepository sesionJWTRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Captor
    private ArgumentCaptor<Credencial> credencialCaptor;

    private Credencial credencialBase;
    private final String correo = "test@eco.cl";
    private final String contrasena = "password123";
    private final String contrasenaHash = "$2a$10$hashedpassword";
    private final Long usuarioId = 1L;

    @BeforeEach
    void setUp() {
        credencialBase = Credencial.builder()
                .id(1L)
                .usuarioId(usuarioId)
                .correoAcceso(correo)
                .contrasenaHash(contrasenaHash)
                .cuentaBloqueada(false)
                .rolAcceso("ROLE_USER")
                .build();
    }

    @Nested
    @DisplayName("crearCredencial")
    class CrearCredencial {

        @Test
        @DisplayName("crea credencial exitosamente")
        void crearCredencialExitoso() {
            CrearCredencialRequest req = new CrearCredencialRequest();
            req.setUsuarioId(2L);
            req.setCorreo("nuevo@eco.cl");
            req.setContrasena("pass1234");

            when(credencialRepository.existsByCorreoAcceso("nuevo@eco.cl")).thenReturn(false);
            when(passwordEncoder.encode("pass1234")).thenReturn("encoded");
            when(credencialRepository.save(any(Credencial.class))).thenReturn(null);

            MensajeResponse response = authService.crearCredencial(req);

            assertThat(response.getMensaje()).isEqualTo("Credencial creada exitosamente.");
            verify(credencialRepository).save(credencialCaptor.capture());
            assertThat(credencialCaptor.getValue().getUsuarioId()).isEqualTo(2L);
            assertThat(credencialCaptor.getValue().getCorreoAcceso()).isEqualTo("nuevo@eco.cl");
        }

        @Test
        @DisplayName("lanza CorreoDuplicadoException cuando el correo ya existe")
        void crearCredencialCorreoDuplicado() {
            CrearCredencialRequest req = new CrearCredencialRequest();
            req.setUsuarioId(2L);
            req.setCorreo(correo);
            req.setContrasena("pass1234");

            when(credencialRepository.existsByCorreoAcceso(correo)).thenReturn(true);

            assertThatThrownBy(() -> authService.crearCredencial(req))
                    .isInstanceOf(CorreoDuplicadoException.class)
                    .hasMessageContaining(correo);
        }

        @Test
        @DisplayName("asigna ROLE_USER por defecto cuando no se especifica rol")
        void crearCredencialRolPorDefecto() {
            CrearCredencialRequest req = new CrearCredencialRequest();
            req.setUsuarioId(3L);
            req.setCorreo("nodefault@eco.cl");
            req.setContrasena("pass1234");

            when(credencialRepository.existsByCorreoAcceso("nodefault@eco.cl")).thenReturn(false);
            when(passwordEncoder.encode("pass1234")).thenReturn("encoded");
            when(credencialRepository.save(any(Credencial.class))).thenReturn(null);

            authService.crearCredencial(req);

            verify(credencialRepository).save(credencialCaptor.capture());
            assertThat(credencialCaptor.getValue().getRolAcceso()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("usa el rol proporcionado cuando se especifica")
        void crearCredencialConRolExplicito() {
            CrearCredencialRequest req = new CrearCredencialRequest();
            req.setUsuarioId(4L);
            req.setCorreo("admin@eco.cl");
            req.setContrasena("pass1234");
            req.setRol("ROLE_ADMIN");

            when(credencialRepository.existsByCorreoAcceso("admin@eco.cl")).thenReturn(false);
            when(passwordEncoder.encode("pass1234")).thenReturn("encoded");
            when(credencialRepository.save(any(Credencial.class))).thenReturn(null);

            authService.crearCredencial(req);

            verify(credencialRepository).save(credencialCaptor.capture());
            assertThat(credencialCaptor.getValue().getRolAcceso()).isEqualTo("ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("iniciarSesion")
    class IniciarSesion {

        @Test
        @DisplayName("inicia sesión exitosamente con credenciales válidas")
        void iniciarSesionExitoso() {
            IniciarSesionRequest req = new IniciarSesionRequest();
            req.setCorreo(correo);
            req.setContrasena(contrasena);

            when(credencialRepository.findByCorreoAcceso(correo)).thenReturn(Optional.of(credencialBase));
            when(passwordEncoder.matches(contrasena, contrasenaHash)).thenReturn(true);
            when(jwtUtil.generarToken(usuarioId, correo, List.of("ROLE_USER"))).thenReturn("jwt-token");
            when(jwtUtil.getExpirationMs()).thenReturn(86400000L);

            IniciarSesionResponse response = authService.iniciarSesion(req);

            assertThat(response.getToken()).isEqualTo("jwt-token");
            assertThat(response.getUsuarioId()).isEqualTo(usuarioId);
            assertThat(response.getCorreo()).isEqualTo(correo);
            assertThat(response.getRol()).isEqualTo("ROLE_USER");
            assertThat(response.getExpiracionMs()).isEqualTo(86400000L);
        }

        @Test
        @DisplayName("lanza AutenticacionException cuando el correo no existe")
        void iniciarSesionCorreoNoExiste() {
            IniciarSesionRequest req = new IniciarSesionRequest();
            req.setCorreo("noexiste@eco.cl");
            req.setContrasena(contrasena);

            when(credencialRepository.findByCorreoAcceso("noexiste@eco.cl")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.iniciarSesion(req))
                    .isInstanceOf(AutenticacionException.class)
                    .hasMessageContaining("Correo o contraseña incorrectos");
        }

        @Test
        @DisplayName("lanza CuentaBloqueadaException cuando la cuenta está bloqueada")
        void iniciarSesionCuentaBloqueada() {
            credencialBase.setCuentaBloqueada(true);

            IniciarSesionRequest req = new IniciarSesionRequest();
            req.setCorreo(correo);
            req.setContrasena(contrasena);

            when(credencialRepository.findByCorreoAcceso(correo)).thenReturn(Optional.of(credencialBase));

            assertThatThrownBy(() -> authService.iniciarSesion(req))
                    .isInstanceOf(CuentaBloqueadaException.class)
                    .hasMessageContaining("bloqueada");
        }

        @Test
        @DisplayName("lanza AutenticacionException cuando la contraseña es incorrecta")
        void iniciarSesionContrasenaIncorrecta() {
            IniciarSesionRequest req = new IniciarSesionRequest();
            req.setCorreo(correo);
            req.setContrasena("wrongpassword");

            when(credencialRepository.findByCorreoAcceso(correo)).thenReturn(Optional.of(credencialBase));

            assertThatThrownBy(() -> authService.iniciarSesion(req))
                    .isInstanceOf(AutenticacionException.class)
                    .hasMessageContaining("Correo o contraseña incorrectos");
        }

        @Test
        @DisplayName("actualiza fecha de último login al iniciar sesión")
        void iniciarSesionActualizaFechaUltimoLogin() {
            IniciarSesionRequest req = new IniciarSesionRequest();
            req.setCorreo(correo);
            req.setContrasena(contrasena);

            when(credencialRepository.findByCorreoAcceso(correo)).thenReturn(Optional.of(credencialBase));
            when(passwordEncoder.matches(contrasena, contrasenaHash)).thenReturn(true);
            when(jwtUtil.generarToken(usuarioId, correo, List.of("ROLE_USER"))).thenReturn("jwt-token");
            when(jwtUtil.getExpirationMs()).thenReturn(86400000L);

            authService.iniciarSesion(req);

            verify(credencialRepository).save(credencialCaptor.capture());
            assertThat(credencialCaptor.getValue().getFechaUltimoLogin()).isNotNull();
        }

        @Test
        @DisplayName("asigna ROLE_USER por defecto cuando la credencial no tiene rol")
        void iniciarSesionRolPorDefecto() {
            credencialBase.setRolAcceso(null);

            IniciarSesionRequest req = new IniciarSesionRequest();
            req.setCorreo(correo);
            req.setContrasena(contrasena);

            when(credencialRepository.findByCorreoAcceso(correo)).thenReturn(Optional.of(credencialBase));
            when(passwordEncoder.matches(contrasena, contrasenaHash)).thenReturn(true);
            when(jwtUtil.generarToken(usuarioId, correo, List.of("ROLE_USER"))).thenReturn("jwt-token");
            when(jwtUtil.getExpirationMs()).thenReturn(86400000L);

            IniciarSesionResponse response = authService.iniciarSesion(req);

            assertThat(response.getRol()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("soporta error del servicio de analytics al iniciar sesión")
        void iniciarSesionAnalyticsCaido() {
            IniciarSesionRequest req = new IniciarSesionRequest();
            req.setCorreo(correo);
            req.setContrasena(contrasena);

            when(credencialRepository.findByCorreoAcceso(correo)).thenReturn(Optional.of(credencialBase));
            when(passwordEncoder.matches(contrasena, contrasenaHash)).thenReturn(true);
            when(jwtUtil.generarToken(usuarioId, correo, List.of("ROLE_USER"))).thenReturn("jwt-token");
            when(jwtUtil.getExpirationMs()).thenReturn(86400000L);
            when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                    .thenThrow(new RuntimeException("Analytics service unavailable"));

            IniciarSesionResponse response = authService.iniciarSesion(req);

            assertThat(response.getToken()).isEqualTo("jwt-token");
        }
    }

    @Nested
    @DisplayName("cerrarSesion")
    class CerrarSesion {

        @Test
        @DisplayName("cierra sesión exitosamente")
        void cerrarSesionExitoso() {
            CerrarSesionRequest req = new CerrarSesionRequest();
            req.setToken("valid-token");

            when(jwtUtil.esTokenValido("valid-token")).thenReturn(true);
            when(sesionJWTRepository.existsByToken("valid-token")).thenReturn(false);

            Claims claims = mock(Claims.class);
            when(jwtUtil.validarYObtenerClaims("valid-token")).thenReturn(claims);
            when(jwtUtil.obtenerRoles("valid-token")).thenReturn(List.of("ROLE_USER"));
            when(jwtUtil.obtenerUsuarioId("valid-token")).thenReturn(usuarioId);

            Date now = new Date();
            when(claims.getIssuedAt()).thenReturn(now);
            when(claims.getExpiration()).thenReturn(new Date(now.getTime() + 3600000));

            MensajeResponse response = authService.cerrarSesion(req);

            assertThat(response.getMensaje()).isEqualTo("Sesión cerrada exitosamente.");
            verify(sesionJWTRepository).save(any(SesionJWT.class));
        }

        @Test
        @DisplayName("lanza TokenInvalidoException cuando el token no es válido")
        void cerrarSesionTokenInvalido() {
            CerrarSesionRequest req = new CerrarSesionRequest();
            req.setToken("invalid-token");

            when(jwtUtil.esTokenValido("invalid-token")).thenReturn(false);

            assertThatThrownBy(() -> authService.cerrarSesion(req))
                    .isInstanceOf(TokenInvalidoException.class)
                    .hasMessageContaining("token");
        }

        @Test
        @DisplayName("retorna mensaje si la sesión ya estaba cerrada")
        void cerrarSesionYaCerrada() {
            CerrarSesionRequest req = new CerrarSesionRequest();
            req.setToken("already-logged-out");

            when(jwtUtil.esTokenValido("already-logged-out")).thenReturn(true);
            when(sesionJWTRepository.existsByToken("already-logged-out")).thenReturn(true);

            MensajeResponse response = authService.cerrarSesion(req);

            assertThat(response.getMensaje()).isEqualTo("La sesión ya estaba cerrada.");
            verify(sesionJWTRepository, never()).save(any());
        }

        @Test
        @DisplayName("asigna ROLE_USER por defecto cuando el token no tiene roles")
        void cerrarSesionRolesVacios() {
            CerrarSesionRequest req = new CerrarSesionRequest();
            req.setToken("token-sin-roles");

            when(jwtUtil.esTokenValido("token-sin-roles")).thenReturn(true);
            when(sesionJWTRepository.existsByToken("token-sin-roles")).thenReturn(false);

            Claims claims = mock(Claims.class);
            when(jwtUtil.validarYObtenerClaims("token-sin-roles")).thenReturn(claims);
            when(jwtUtil.obtenerRoles("token-sin-roles")).thenReturn(List.of());
            when(jwtUtil.obtenerUsuarioId("token-sin-roles")).thenReturn(usuarioId);

            Date now = new Date();
            when(claims.getIssuedAt()).thenReturn(now);
            when(claims.getExpiration()).thenReturn(new Date(now.getTime() + 3600000));

            MensajeResponse response = authService.cerrarSesion(req);

            assertThat(response.getMensaje()).isEqualTo("Sesión cerrada exitosamente.");
            verify(sesionJWTRepository).save(any(SesionJWT.class));
        }

        @Test
        @DisplayName("asigna ROLE_USER por defecto cuando el token tiene roles null")
        void cerrarSesionRolesNull() {
            CerrarSesionRequest req = new CerrarSesionRequest();
            req.setToken("token-roles-null");

            when(jwtUtil.esTokenValido("token-roles-null")).thenReturn(true);
            when(sesionJWTRepository.existsByToken("token-roles-null")).thenReturn(false);

            Claims claims = mock(Claims.class);
            when(jwtUtil.validarYObtenerClaims("token-roles-null")).thenReturn(claims);
            when(jwtUtil.obtenerRoles("token-roles-null")).thenReturn(null);
            when(jwtUtil.obtenerUsuarioId("token-roles-null")).thenReturn(usuarioId);

            Date now = new Date();
            when(claims.getIssuedAt()).thenReturn(now);
            when(claims.getExpiration()).thenReturn(new Date(now.getTime() + 3600000));

            MensajeResponse response = authService.cerrarSesion(req);

            assertThat(response.getMensaje()).isEqualTo("Sesión cerrada exitosamente.");
            verify(sesionJWTRepository).save(any(SesionJWT.class));
        }
    }

    @Nested
    @DisplayName("autenticarJWT")
    class AutenticarJWT {

        @Test
        @DisplayName("retorna válido cuando el token es válido y no está en blacklist")
        void autenticarJWTValido() {
            AutenticarJWTRequest req = new AutenticarJWTRequest();
            req.setToken("valid-token");

            when(jwtUtil.esTokenValido("valid-token")).thenReturn(true);
            when(sesionJWTRepository.existsByToken("valid-token")).thenReturn(false);
            when(jwtUtil.obtenerUsuarioId("valid-token")).thenReturn(usuarioId);
            when(jwtUtil.obtenerCorreo("valid-token")).thenReturn(correo);
            when(jwtUtil.obtenerRoles("valid-token")).thenReturn(List.of("ROLE_USER"));

            AutenticarJWTResponse response = authService.autenticarJWT(req);

            assertThat(response.isValido()).isTrue();
            assertThat(response.getUsuarioId()).isEqualTo(usuarioId);
            assertThat(response.getCorreo()).isEqualTo(correo);
        }

        @Test
        @DisplayName("retorna no válido cuando el token expiró")
        void autenticarJWTExpirado() {
            AutenticarJWTRequest req = new AutenticarJWTRequest();
            req.setToken("expired-token");

            when(jwtUtil.esTokenValido("expired-token")).thenReturn(false);

            AutenticarJWTResponse response = authService.autenticarJWT(req);

            assertThat(response.isValido()).isFalse();
        }

        @Test
        @DisplayName("retorna no válido cuando el token está en blacklist")
        void autenticarJWTEnBlacklist() {
            AutenticarJWTRequest req = new AutenticarJWTRequest();
            req.setToken("blacklisted-token");

            when(jwtUtil.esTokenValido("blacklisted-token")).thenReturn(true);
            when(sesionJWTRepository.existsByToken("blacklisted-token")).thenReturn(true);

            AutenticarJWTResponse response = authService.autenticarJWT(req);

            assertThat(response.isValido()).isFalse();
        }
    }

    @Nested
    @DisplayName("cambiarCorreo")
    class CambiarCorreo {

        @Test
        @DisplayName("cambia correo exitosamente")
        void cambiarCorreoExitoso() {
            CambiarCorreoRequest req = new CambiarCorreoRequest();
            req.setUsuarioId(usuarioId);
            req.setNuevoCorreo("nuevo@eco.cl");
            req.setContrasenaActual(contrasena);

            when(credencialRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(credencialBase));
            when(passwordEncoder.matches(contrasena, contrasenaHash)).thenReturn(true);
            when(credencialRepository.existsByCorreoAcceso("nuevo@eco.cl")).thenReturn(false);

            MensajeResponse response = authService.cambiarCorreo(req);

            assertThat(response.getMensaje()).isEqualTo("Correo actualizado exitosamente.");
            verify(credencialRepository).save(credencialCaptor.capture());
            assertThat(credencialCaptor.getValue().getCorreoAcceso()).isEqualTo("nuevo@eco.cl");
        }

        @Test
        @DisplayName("lanza CredencialNotFoundException cuando no se encuentra el usuario")
        void cambiarCorreoUsuarioNoExiste() {
            CambiarCorreoRequest req = new CambiarCorreoRequest();
            req.setUsuarioId(999L);
            req.setNuevoCorreo("nuevo@eco.cl");
            req.setContrasenaActual(contrasena);

            when(credencialRepository.findByUsuarioId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.cambiarCorreo(req))
                    .isInstanceOf(CredencialNotFoundException.class)
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("lanza CorreoDuplicadoException cuando el nuevo correo ya está en uso")
        void cambiarCorreoYaEnUso() {
            CambiarCorreoRequest req = new CambiarCorreoRequest();
            req.setUsuarioId(usuarioId);
            req.setNuevoCorreo("ocupado@eco.cl");
            req.setContrasenaActual(contrasena);

            when(credencialRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(credencialBase));
            when(passwordEncoder.matches(contrasena, contrasenaHash)).thenReturn(true);
            when(credencialRepository.existsByCorreoAcceso("ocupado@eco.cl")).thenReturn(true);

            assertThatThrownBy(() -> authService.cambiarCorreo(req))
                    .isInstanceOf(CorreoDuplicadoException.class)
                    .hasMessageContaining("ocupado@eco.cl");
        }

        @Test
        @DisplayName("lanza AutenticacionException cuando la contraseña actual es incorrecta")
        void cambiarCorreoContrasenaIncorrecta() {
            CambiarCorreoRequest req = new CambiarCorreoRequest();
            req.setUsuarioId(usuarioId);
            req.setNuevoCorreo("nuevo@eco.cl");
            req.setContrasenaActual("wrong-password");

            when(credencialRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(credencialBase));
            when(passwordEncoder.matches("wrong-password", contrasenaHash)).thenReturn(false);

            assertThatThrownBy(() -> authService.cambiarCorreo(req))
                    .isInstanceOf(AutenticacionException.class)
                    .hasMessageContaining("Contraseña incorrecta");
        }
    }

    @Nested
    @DisplayName("cambiarContrasena")
    class CambiarContrasena {

        @Test
        @DisplayName("cambia contraseña exitosamente")
        void cambiarContrasenaExitoso() {
            CambiarContrasenaRequest req = new CambiarContrasenaRequest();
            req.setUsuarioId(usuarioId);
            req.setContrasenaActual(contrasena);
            req.setNuevaContrasena("newpass1234");

            when(credencialRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(credencialBase));
            when(passwordEncoder.matches(contrasena, contrasenaHash)).thenReturn(true);
            when(passwordEncoder.encode("newpass1234")).thenReturn("encoded");

            MensajeResponse response = authService.cambiarContrasena(req);

            assertThat(response.getMensaje()).isEqualTo("Contraseña actualizada exitosamente.");
            verify(credencialRepository).save(any(Credencial.class));
        }

        @Test
        @DisplayName("lanza CredencialNotFoundException cuando el usuario no existe")
        void cambiarContrasenaUsuarioNoExiste() {
            CambiarContrasenaRequest req = new CambiarContrasenaRequest();
            req.setUsuarioId(999L);
            req.setContrasenaActual(contrasena);
            req.setNuevaContrasena("newpass1234");

            when(credencialRepository.findByUsuarioId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.cambiarContrasena(req))
                    .isInstanceOf(CredencialNotFoundException.class);
        }

        @Test
        @DisplayName("lanza AutenticacionException cuando la contraseña actual es incorrecta")
        void cambiarContrasenaActualIncorrecta() {
            CambiarContrasenaRequest req = new CambiarContrasenaRequest();
            req.setUsuarioId(usuarioId);
            req.setContrasenaActual("wrong-password");
            req.setNuevaContrasena("newpass1234");

            when(credencialRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(credencialBase));
            when(passwordEncoder.matches("wrong-password", contrasenaHash)).thenReturn(false);

            assertThatThrownBy(() -> authService.cambiarContrasena(req))
                    .isInstanceOf(AutenticacionException.class)
                    .hasMessageContaining("Contraseña actual incorrecta");
        }
    }

    @Nested
    @DisplayName("recuperarCredenciales")
    class RecuperarCredenciales {

        @Test
        @DisplayName("genera código de recuperación exitosamente")
        void recuperarCredencialesExitoso() {
            RecuperarCredencialesRequest req = new RecuperarCredencialesRequest();
            req.setCorreo(correo);

            when(credencialRepository.findByCorreoAcceso(correo)).thenReturn(Optional.of(credencialBase));
            when(tokenRecuperacionRepository.save(any(TokenRecuperacion.class))).thenReturn(null);

            MensajeResponse response = authService.recuperarCredenciales(req);

            assertThat(response.getMensaje()).contains("Código de recuperación generado");
            verify(tokenRecuperacionRepository).save(any(TokenRecuperacion.class));
        }

        @Test
        @DisplayName("lanza CredencialNotFoundException cuando el correo no existe")
        void recuperarCredencialesCorreoNoExiste() {
            RecuperarCredencialesRequest req = new RecuperarCredencialesRequest();
            req.setCorreo("noexiste@eco.cl");

            when(credencialRepository.findByCorreoAcceso("noexiste@eco.cl")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.recuperarCredenciales(req))
                    .isInstanceOf(CredencialNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("restablecerConToken")
    class RestablecerConToken {

        @Test
        @DisplayName("restablece contraseña exitosamente con token válido")
        void restablecerConTokenExitoso() {
            RestablecerConTokenRequest req = new RestablecerConTokenRequest();
            req.setCodigo("valid-code");
            req.setNuevaContrasena("newpass1234");

            TokenRecuperacion tokenRec = TokenRecuperacion.builder()
                    .credencial(credencialBase)
                    .codigoAlfanumerico("valid-code")
                    .consumido(false)
                    .expiracion(LocalDateTime.now().plusHours(1))
                    .build();

            when(tokenRecuperacionRepository.buscarTokenActivo(eq("valid-code"), any(LocalDateTime.class)))
                    .thenReturn(Optional.of(tokenRec));
            when(passwordEncoder.encode("newpass1234")).thenReturn("encoded");
            when(credencialRepository.save(credencialBase)).thenReturn(null);

            MensajeResponse response = authService.restablecerConToken(req);

            assertThat(response.getMensaje()).isEqualTo("Contraseña restablecida exitosamente.");
            assertThat(tokenRec.getConsumido()).isTrue();
        }

        @Test
        @DisplayName("lanza TokenInvalidoException cuando el código es inválido")
        void restablecerConTokenInvalido() {
            RestablecerConTokenRequest req = new RestablecerConTokenRequest();
            req.setCodigo("invalid-code");
            req.setNuevaContrasena("newpass1234");

            when(tokenRecuperacionRepository.buscarTokenActivo(eq("invalid-code"), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.restablecerConToken(req))
                    .isInstanceOf(TokenInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("inhabilitarCredenciales")
    class InhabilitarCredenciales {

        @Test
        @DisplayName("inhabilita credenciales exitosamente")
        void inhabilitarCredencialesExitoso() {
            InhabilitarCredencialesRequest req = new InhabilitarCredencialesRequest();
            req.setUsuarioId(usuarioId);

            when(credencialRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(credencialBase));

            MensajeResponse response = authService.inhabilitarCredenciales(req);

            assertThat(response.getMensaje()).isEqualTo("Credenciales inhabilitadas. La cuenta ha sido bloqueada.");
            assertThat(credencialBase.getCuentaBloqueada()).isTrue();
            verify(credencialRepository).save(credencialBase);
        }

        @Test
        @DisplayName("lanza CredencialNotFoundException cuando el usuario no existe")
        void inhabilitarCredencialesUsuarioNoExiste() {
            InhabilitarCredencialesRequest req = new InhabilitarCredencialesRequest();
            req.setUsuarioId(999L);

            when(credencialRepository.findByUsuarioId(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.inhabilitarCredenciales(req))
                    .isInstanceOf(CredencialNotFoundException.class);
        }
    }
}
