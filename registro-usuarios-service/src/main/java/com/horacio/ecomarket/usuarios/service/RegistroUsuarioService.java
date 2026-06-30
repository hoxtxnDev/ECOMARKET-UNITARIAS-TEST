package com.horacio.ecomarket.usuarios.service;

import com.horacio.ecomarket.usuarios.exception.CorreoDuplicadoException;
import com.horacio.ecomarket.usuarios.exception.RecursoNoEncontradoException;
import com.horacio.ecomarket.usuarios.exception.TelefonoDuplicadoException;
import com.horacio.ecomarket.usuarios.model.Permiso;
import com.horacio.ecomarket.usuarios.model.PerfilUsuario;
import com.horacio.ecomarket.usuarios.model.Rol;
import com.horacio.ecomarket.usuarios.repository.PerfilUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final RestTemplate restTemplate;

    @Transactional
    public PerfilUsuario registrarCuenta(PerfilUsuario perfilUsuario, String contrasenaInicial) {
        repository.findByCorreo(perfilUsuario.getCorreo())
                .ifPresent(u -> {
                    throw new CorreoDuplicadoException("El correo ya está registrado: " + perfilUsuario.getCorreo());
                });

        if (perfilUsuario.getTelefono() != null && !perfilUsuario.getTelefono().isBlank()) {
            validarTelefonoNoDuplicado(perfilUsuario.getTelefono(), null);
        }

        perfilUsuario.setFechaCreacion(LocalDateTime.now());
        PerfilUsuario guardado = repository.save(perfilUsuario);

        String rolNombre = perfilUsuario.getRol() != null ? "ROLE_" + perfilUsuario.getRol().getNombre() : "ROLE_USER";

        Map<String, Object> request = new HashMap<>();
        request.put("usuarioId", guardado.getId());
        request.put("correo", perfilUsuario.getCorreo());
        request.put("contrasena", contrasenaInicial);
        request.put("rol", rolNombre);

        String url = "http://localhost:8086/api/sesion/credencial";
        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            log.error("Error al crear credenciales en iniciosesion-service", e);
            throw new RuntimeException("Error al crear credenciales de acceso: " + e.getMessage());
        }

        registrarLog(guardado.getId(), "REGISTRO_USUARIO", "Usuario registrado exitosamente con correo: " + perfilUsuario.getCorreo());

        return guardado;
    }

    @Transactional
    public PerfilUsuario modificarDatosUsuario(Long id, PerfilUsuario datosNuevos) {
        PerfilUsuario existente = buscarPorId(id);

        if (datosNuevos.getTelefono() != null && !datosNuevos.getTelefono().isBlank()
                && !datosNuevos.getTelefono().equals(existente.getTelefono())) {
            validarTelefonoNoDuplicado(datosNuevos.getTelefono(), id);
        }

        existente.setNombre(datosNuevos.getNombre());
        existente.setTelefono(datosNuevos.getTelefono());

        if (!existente.getCorreo().equals(datosNuevos.getCorreo())) {
            repository.findByCorreo(datosNuevos.getCorreo())
                    .ifPresent(u -> {
                        throw new CorreoDuplicadoException("El correo ya está en uso: " + datosNuevos.getCorreo());
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
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuarioId));
    }

    @Transactional(readOnly = true)
    public PerfilUsuario buscarPorCorreo(String correo) {
        return repository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con correo: " + correo));
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

    private String normalizarTelefono(String telefono) {
        String soloDigitos = telefono.replaceAll("\\D", "");
        if (soloDigitos.startsWith("56")) {
            if (soloDigitos.length() > 2) {
                soloDigitos = soloDigitos.substring(2);
            }
        }
        return soloDigitos;
    }

    private boolean mismoTelefonoNormalizado(String telefono1, String telefono2) {
        if (telefono2 == null) return false;
        String norm1 = normalizarTelefono(telefono1);
        String norm2 = normalizarTelefono(telefono2);
        return norm1.equals(norm2);
    }

    private void validarTelefonoNoDuplicado(String telefono, Long excluirId) {
        String normalizado = normalizarTelefono(telefono);

        List<PerfilUsuario> todos = repository.findAll();
        for (PerfilUsuario u : todos) {
            if (excluirId != null && u.getId().equals(excluirId)) continue;
            if (mismoTelefonoNormalizado(telefono, u.getTelefono())) {
                throw new TelefonoDuplicadoException("El teléfono ya está registrado por otro usuario.");
            }
        }
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
