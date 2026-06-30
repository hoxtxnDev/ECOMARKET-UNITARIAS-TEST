package com.horacio.ecomarket.usuarios.controller;

import com.horacio.ecomarket.usuarios.dto.ModificarUsuarioDTO;
import com.horacio.ecomarket.usuarios.dto.RegistroUsuarioDTO;
import com.horacio.ecomarket.usuarios.dto.ConfigurarPermisosDTO;
import com.horacio.ecomarket.usuarios.model.EstadoPerfil;
import com.horacio.ecomarket.usuarios.model.Permiso;
import com.horacio.ecomarket.usuarios.model.PerfilUsuario;
import com.horacio.ecomarket.usuarios.model.Rol;
import com.horacio.ecomarket.usuarios.exception.RecursoNoEncontradoException;
import com.horacio.ecomarket.usuarios.repository.EstadoPerfilRepository;
import com.horacio.ecomarket.usuarios.repository.PermisoRepository;
import com.horacio.ecomarket.usuarios.repository.RolRepository;
import com.horacio.ecomarket.usuarios.service.RegistroUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final RegistroUsuarioService service;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final EstadoPerfilRepository estadoPerfilRepository;

    @PostMapping("/registro")
    public ResponseEntity<PerfilUsuario> registrar(@Valid @RequestBody RegistroUsuarioDTO dto) {
        PerfilUsuario perfil = buildPerfilDesdeRegistroDTO(dto);
        PerfilUsuario creado = service.registrarCuenta(perfil, dto.getContrasenaInicial());
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilUsuario> modificar(
            @PathVariable Long id,
            @Valid @RequestBody ModificarUsuarioDTO dto) {
        PerfilUsuario datosNuevos = buildPerfilDesdeModificarDTO(dto);
        return ResponseEntity.ok(service.modificarDatosUsuario(id, datosNuevos));
    }

    @GetMapping
    public ResponseEntity<List<PerfilUsuario>> listarTodos() {
        return ResponseEntity.ok(service.listarUsuarios());
    }

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<PerfilUsuario>> listarPorRol(@PathVariable Long rolId) {
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + rolId));
        return ResponseEntity.ok(service.listarPorRol(rol));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilUsuario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<PerfilUsuario> buscarPorCorreo(@PathVariable String correo) {
        return ResponseEntity.ok(service.buscarPorCorreo(correo));
    }

    @PutMapping("/{id}/permisos")
    public ResponseEntity<Boolean> configurarPermisos(
            @PathVariable Long id,
            @Valid @RequestBody ConfigurarPermisosDTO dto) {
        List<Permiso> permisos = dto.getPermisoIds().stream()
                .map(pid -> permisoRepository.findById(pid)
                        .orElseThrow(() -> new RecursoNoEncontradoException("Permiso no encontrado con ID: " + pid)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(service.configurarPermisos(id, permisos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> eliminar(@PathVariable Long id) {
        return ResponseEntity.ok(service.eliminarUsuario(id));
    }

    private PerfilUsuario buildPerfilDesdeRegistroDTO(RegistroUsuarioDTO dto) {
        return buildPerfil(dto.getNombre(), dto.getCorreo(), dto.getTelefono(),
                dto.getRolId(), dto.getEstadoPerfilId());
    }

    private PerfilUsuario buildPerfilDesdeModificarDTO(ModificarUsuarioDTO dto) {
        return buildPerfil(dto.getNombre(), dto.getCorreo(), dto.getTelefono(),
                dto.getRolId(), dto.getEstadoPerfilId());
    }

    private PerfilUsuario buildPerfil(String nombre, String correo, String telefono,
                                      Long rolId, Long estadoPerfilId) {
        PerfilUsuario.PerfilUsuarioBuilder builder = PerfilUsuario.builder()
                .nombre(nombre).correo(correo).telefono(telefono);

        if (rolId != null) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + rolId));
            builder.rol(rol);
        }

        if (estadoPerfilId != null) {
            EstadoPerfil estado = estadoPerfilRepository.findById(estadoPerfilId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "EstadoPerfil no encontrado con ID: " + estadoPerfilId));
            builder.estadoPerfil(estado);
        }

        return builder.build();
    }
}
