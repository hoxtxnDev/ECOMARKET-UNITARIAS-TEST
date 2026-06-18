package com.horacio.ecomarket.usuarios.service;

import com.horacio.ecomarket.usuarios.model.Credencial;
import com.horacio.ecomarket.usuarios.model.Permiso;
import com.horacio.ecomarket.usuarios.model.PerfilUsuario;
import com.horacio.ecomarket.usuarios.model.Rol;
import com.horacio.ecomarket.usuarios.repository.CredencialRepository;
import com.horacio.ecomarket.usuarios.repository.PerfilUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroUsuarioService {

    private final PerfilUsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final CredencialRepository credencialRepository;
    private final RestTemplate restTemplate;


    @Transactional
    public PerfilUsuario registrarCuenta(PerfilUsuario perfilUsuario, String contrasenaInicial) {
        repository.findByCorreo(perfilUsuario.getCorreo())
                .ifPresent(u -> {
                    throw new RuntimeException("El correo ya está registrado: " + perfilUsuario.getCorreo());
                });

        perfilUsuario.setFechaCreacion(LocalDateTime.now());
        PerfilUsuario guardado = repository.save(perfilUsuario);

        String rolNombre = perfilUsuario.getRol() != null ? "ROLE_" + perfilUsuario.getRol().getNombre() : "ROLE_USER";

        Credencial credencial = Credencial.builder()
                .usuarioId(guardado.getId())
                .correoAcceso(perfilUsuario.getCorreo())
                .contrasenaHash(passwordEncoder.encode(contrasenaInicial))
                .cuentaBloqueada(false)
                .rolAcceso(rolNombre)
                .build();

        credencialRepository.save(credencial);

        registrarLog(guardado.getId(), "REGISTRO_USUARIO", "Usuario registrado exitosamente con correo: " + perfilUsuario.getCorreo());

        return guardado;
    }


    @Transactional
    public PerfilUsuario modificarDatosUsuario(Long id, PerfilUsuario datosNuevos) {
        PerfilUsuario existente = buscarPorId(id);

        existente.setNombre(datosNuevos.getNombre());
        existente.setTelefono(datosNuevos.getTelefono());

        if (!existente.getCorreo().equals(datosNuevos.getCorreo())) {
            repository.findByCorreo(datosNuevos.getCorreo())
                    .ifPresent(u -> {
                        throw new RuntimeException("El correo ya está en uso: " + datosNuevos.getCorreo());
                    });
            existente.setCorreo(datosNuevos.getCorreo());
        }

        if (datosNuevos.getRol() != null) {
            existente.setRol(datosNuevos.getRol());
        }

        if (datosNuevos.getEstadoPerfil() != null) {
            existente.setEstadoPerfil(datosNuevos.getEstadoPerfil());
        }

        return repository.save(existente);
    }


    @Transactional(readOnly = true)
    public List<PerfilUsuario> listarUsuarios() {
        return repository.findAll();
    }


    @Transactional(readOnly = true)
    public List<PerfilUsuario> listarPorRol(Rol rolUsuario) {
        return repository.findByRol(rolUsuario);
    }


    @Transactional(readOnly = true)
    public PerfilUsuario buscarPorId(Long usuarioId) {
        return repository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
    }


    @Transactional(readOnly = true)
    public PerfilUsuario buscarPorCorreo(String correo) {
        return repository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
    }


    @Transactional
    public Boolean configurarPermisos(Long usuarioId, List<Permiso> nuevosPermisos) {
        PerfilUsuario usuario = buscarPorId(usuarioId);
        usuario.getPermisos().clear();
        usuario.getPermisos().addAll(nuevosPermisos);
        repository.save(usuario);
        return true;
    }


    @Transactional
    public Boolean eliminarUsuario(Long usuarioId) {
        PerfilUsuario usuario = buscarPorId(usuarioId);
        repository.delete(usuario);
        registrarLog(usuarioId, "ELIMINACION_USUARIO", "Usuario eliminado con ID: " + usuarioId);
        return true;
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
