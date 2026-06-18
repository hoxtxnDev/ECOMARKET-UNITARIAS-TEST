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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final CredencialRepository credencialRepository;
    private final TokenRecuperacionRepository tokenRecuperacionRepository;
    private final SesionJWTRepository sesionJWTRepository;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MensajeResponse crearCredencial(CrearCredencialRequest request) {
        if (credencialRepository.existsByCorreoAcceso(request.getCorreo())) {
            throw new CorreoDuplicadoException(
                    "El correo '" + request.getCorreo() + "' ya está registrado.");
        }

        String rol = request.getRol() != null ? request.getRol() : "ROLE_USER";

        Credencial credencial = Credencial.builder()
                .usuarioId(request.getUsuarioId())
                .correoAcceso(request.getCorreo())
                .contrasenaHash(passwordEncoder.encode(request.getContrasena()))
                .cuentaBloqueada(false)
                .rolAcceso(rol)
                .build();

        credencialRepository.save(credencial);
        return MensajeResponse.de("Credencial creada exitosamente.");
    }

    @Override
    @Transactional
    public IniciarSesionResponse iniciarSesion(IniciarSesionRequest request) {
        Credencial credencial = credencialRepository
                .findByCorreoAcceso(request.getCorreo())
                .orElseThrow(() -> new AutenticacionException("Correo o contraseña incorrectos."));

        if (Boolean.TRUE.equals(credencial.getCuentaBloqueada())) {
            throw new CuentaBloqueadaException("La cuenta está bloqueada. Contacte al administrador.");
        }

        if (!passwordEncoder.matches(request.getContrasena(), credencial.getContrasenaHash())) {
            throw new AutenticacionException("Correo o contraseña incorrectos.");
        }

        credencial.setFechaUltimoLogin(LocalDateTime.now());
        credencialRepository.save(credencial);

        String rol = credencial.getRolAcceso() != null ? credencial.getRolAcceso() : "ROLE_USER";
        List<String> roles = List.of(rol);
        String token = jwtUtil.generarToken(credencial.getUsuarioId(), credencial.getCorreoAcceso(), roles);

        registrarLog(credencial.getUsuarioId(), "INICIO_SESION", "Usuario inició sesión exitosamente con correo: " + credencial.getCorreoAcceso());

        return IniciarSesionResponse.builder()
                .token(token)
                .usuarioId(credencial.getUsuarioId())
                .correo(credencial.getCorreoAcceso())
                .rol(rol)
                .expiracionMs(jwtUtil.getExpirationMs())
                .build();
    }

    @Override
    @Transactional
    public MensajeResponse cerrarSesion(CerrarSesionRequest request) {
        String token = request.getToken();

        if (!jwtUtil.esTokenValido(token)) {
            throw new TokenInvalidoException("El token proporcionado no es válido.");
        }

        if (sesionJWTRepository.existsByToken(token)) {
            return MensajeResponse.de("La sesión ya estaba cerrada.");
        }

        Claims claims = jwtUtil.validarYObtenerClaims(token);
        List<String> roles = jwtUtil.obtenerRoles(token);
        String rolAcceso = (roles != null && !roles.isEmpty()) ? roles.get(0) : "ROLE_USER";

        SesionJWT sesion = SesionJWT.builder()
                .token(token)
                .usuarioId(jwtUtil.obtenerUsuarioId(token))
                .rolAcceso(rolAcceso)
                .fechaEmision(claims.getIssuedAt()
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .fechaExpiracion(claims.getExpiration()
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();

        sesionJWTRepository.save(sesion);
        return MensajeResponse.de("Sesión cerrada exitosamente.");
    }

    @Override
    public AutenticarJWTResponse autenticarJWT(AutenticarJWTRequest request) {
        String token = request.getToken();

        if (!jwtUtil.esTokenValido(token)) {
            return AutenticarJWTResponse.builder().valido(false).build();
        }

        if (sesionJWTRepository.existsByToken(token)) {
            return AutenticarJWTResponse.builder().valido(false).build();
        }

        return AutenticarJWTResponse.builder()
                .valido(true)
                .usuarioId(jwtUtil.obtenerUsuarioId(token))
                .correo(jwtUtil.obtenerCorreo(token))
                .roles(jwtUtil.obtenerRoles(token))
                .build();
    }

    @Override
    @Transactional
    public MensajeResponse cambiarCorreo(CambiarCorreoRequest request) {
        Credencial credencial = credencialRepository
                .findByUsuarioId(request.getUsuarioId())
                .orElseThrow(() -> new CredencialNotFoundException(
                        "No se encontraron credenciales para el usuario " + request.getUsuarioId()));

        if (!passwordEncoder.matches(request.getContrasenaActual(), credencial.getContrasenaHash())) {
            throw new AutenticacionException("Contraseña incorrecta.");
        }

        if (credencialRepository.existsByCorreoAcceso(request.getNuevoCorreo())) {
            throw new CorreoDuplicadoException(
                    "El correo '" + request.getNuevoCorreo() + "' ya está en uso.");
        }

        credencial.setCorreoAcceso(request.getNuevoCorreo());
        credencialRepository.save(credencial);
        return MensajeResponse.de("Correo actualizado exitosamente.");
    }

    @Override
    @Transactional
    public MensajeResponse cambiarContrasena(CambiarContrasenaRequest request) {
        Credencial credencial = credencialRepository
                .findByUsuarioId(request.getUsuarioId())
                .orElseThrow(() -> new CredencialNotFoundException(
                        "No se encontraron credenciales para el usuario " + request.getUsuarioId()));

        if (!passwordEncoder.matches(request.getContrasenaActual(), credencial.getContrasenaHash())) {
            throw new AutenticacionException("Contraseña actual incorrecta.");
        }

        credencial.setContrasenaHash(passwordEncoder.encode(request.getNuevaContrasena()));
        credencialRepository.save(credencial);
        return MensajeResponse.de("Contraseña actualizada exitosamente.");
    }

    @Override
    @Transactional
    public MensajeResponse recuperarCredenciales(RecuperarCredencialesRequest request) {
        Credencial credencial = credencialRepository
                .findByCorreoAcceso(request.getCorreo())
                .orElseThrow(() -> new CredencialNotFoundException(
                        "No existe una cuenta asociada a ese correo."));

        String codigo = generarCodigoAlfanumerico();

        TokenRecuperacion tokenRec = TokenRecuperacion.builder()
                .credencial(credencial)
                .codigoAlfanumerico(codigo)
                .expiracion(LocalDateTime.now().plusHours(2))
                .consumido(false)
                .build();

        tokenRecuperacionRepository.save(tokenRec);

        return MensajeResponse.de("Código de recuperación generado: " + codigo
                + " (válido por 2 horas). En producción, se enviaría al correo registrado.");
    }

    @Override
    @Transactional
    public MensajeResponse restablecerConToken(RestablecerConTokenRequest request) {
        TokenRecuperacion tokenRec = tokenRecuperacionRepository
                .buscarTokenActivo(request.getCodigo(), LocalDateTime.now())
                .orElseThrow(() -> new TokenInvalidoException(
                        "El código es inválido, ya fue usado o expiró."));

        Credencial credencial = tokenRec.getCredencial();
        credencial.setContrasenaHash(passwordEncoder.encode(request.getNuevaContrasena()));
        credencial.setCuentaBloqueada(false);
        credencialRepository.save(credencial);

        tokenRec.setConsumido(true);
        tokenRecuperacionRepository.save(tokenRec);

        return MensajeResponse.de("Contraseña restablecida exitosamente.");
    }

    @Override
    @Transactional
    public MensajeResponse inhabilitarCredenciales(InhabilitarCredencialesRequest request) {
        Credencial credencial = credencialRepository
                .findByUsuarioId(request.getUsuarioId())
                .orElseThrow(() -> new CredencialNotFoundException(
                        "No se encontraron credenciales para el usuario " + request.getUsuarioId()));

        credencial.setCuentaBloqueada(true);
        credencialRepository.save(credencial);
        return MensajeResponse.de("Credenciales inhabilitadas. La cuenta ha sido bloqueada.");
    }

    private String generarCodigoAlfanumerico() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void registrarLog(Long usuarioId, String accion, String detalles) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("microservicio", "registro-usuarios-service");
        logEntry.put("accion", accion);
        logEntry.put("usuarioId", usuarioId);
        logEntry.put("detalles", detalles);
        logEntry.put("fecha", LocalDateTime.now());

        try {
            restTemplate.postForEntity("http://localhost:8084/api/analitica/logs", logEntry, String.class);
        } catch (Exception e) {
            log.warn("Error al enviar log a analitica", e);
        }
    }
}
