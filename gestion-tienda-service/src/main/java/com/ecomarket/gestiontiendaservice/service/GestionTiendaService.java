package com.ecomarket.gestiontiendaservice.service;

import com.ecomarket.gestiontiendaservice.client.RegistroUsuariosClient;
import com.ecomarket.gestiontiendaservice.dto.SucursalRequestDTO;
import com.ecomarket.gestiontiendaservice.exception.NoExisteEnBdException;
import com.ecomarket.gestiontiendaservice.exception.YaExisteEnBdException;
import com.ecomarket.gestiontiendaservice.model.*;
import com.ecomarket.gestiontiendaservice.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionTiendaService {


    private final SucursalRepository sucursalRepository;

    private final PermisoPOSRepository permisoPOSRepository;

    private final ReglamentoInternoRepository reglamentoInternoRepository;

    private final HorarioAtencionRepository horarioAtencionRepository;

    private final TareaPersonalRepository tareaPersonalRepository;

    private final EstadoTareaPersonalRepository estadoTareaPersonalRepository;

    private final RegistroUsuariosClient registroUsuariosClient;


    public Sucursal registrarSucursal(SucursalRequestDTO dto) {
        if (dto.getGerenteCargoId() != null) {
            validarRolGerente(dto.getGerenteCargoId());
        }
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setTelefono(dto.getTelefono());
        sucursal.setGerenteCargoId(dto.getGerenteCargoId());
        sucursal.setActiva(true);
        sucursal.setFechaInauguracion(LocalDateTime.now());
        return sucursalRepository.save(sucursal);
    }


    public Sucursal obtenerDatosSucursal(Long sucursalId) {
        return sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new NoExisteEnBdException("Sucursal no encontrada: " + sucursalId));
    }


    public List<Sucursal> listarSucursalesActivas() {
        return sucursalRepository.findByActivaTrue();
    }


    public Sucursal asignarGerente(Long sucursalId, Long gerenteCargoId) {
        Sucursal sucursal = obtenerDatosSucursal(sucursalId);
        validarRolGerente(gerenteCargoId);
        sucursal.setGerenteCargoId(gerenteCargoId);
        return sucursalRepository.save(sucursal);
    }

    private void validarRolGerente(Long usuarioId) {
        String rol = registroUsuariosClient.obtenerRolNombrePorUsuarioId(usuarioId);
        if (rol == null) {
            throw new NoExisteEnBdException("Usuario no encontrado con ID: " + usuarioId);
        }
        if (!"GERENTE".equalsIgnoreCase(rol)) {
            throw new YaExisteEnBdException("El usuario " + usuarioId + " tiene el rol '" + rol + "', no es GERENTE.");
        }
    }

    private void validarRolEmpleado(Long usuarioId) {
        String rol = registroUsuariosClient.obtenerRolNombrePorUsuarioId(usuarioId);
        if (rol == null) {
            throw new NoExisteEnBdException("Usuario no encontrado con ID: " + usuarioId);
        }
        if (!"EMPLEADO".equalsIgnoreCase(rol)) {
            throw new YaExisteEnBdException("El usuario " + usuarioId + " tiene el rol '" + rol + "', no es EMPLEADO.");
        }
    }


    public PermisoPOS configurarPermisoPOS(PermisoPOS permisoPOS) {
        validarRolEmpleado(permisoPOS.getRolEmpleado());
        return permisoPOSRepository.save(permisoPOS);
    }


    public TareaPersonal asignarTareaPersonal(TareaPersonal tarea) {
        validarRolEmpleado(tarea.getEmpleadoId());
        if (tarea.getGerenteAsignadoId() != null) {
            validarRolGerente(tarea.getGerenteAsignadoId());
        }
        EstadoTareaPersonal estadoPendiente = estadoTareaPersonalRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new NoExisteEnBdException("Estado PENDIENTE no encontrado"));
        tarea.setEstado(estadoPendiente);
        tarea.setFechaAsignacion(LocalDateTime.now());
        return tareaPersonalRepository.save(tarea);
    }


    public TareaPersonal actualizarEstadoTarea(Long tareaId, Long estadoId) {
        TareaPersonal tarea = tareaPersonalRepository.findById(tareaId)
                .orElseThrow(() -> new NoExisteEnBdException("Tarea no encontrada: " + tareaId));
        EstadoTareaPersonal estado = estadoTareaPersonalRepository.findById(estadoId)
                .orElseThrow(() -> new NoExisteEnBdException("Estado de tarea no encontrado: " + estadoId));
        tarea.setEstado(estado);
        return tareaPersonalRepository.save(tarea);
    }


    public ReglamentoInterno establecerReglamento(ReglamentoInterno reglamentoInterno) {
        return reglamentoInternoRepository.save(reglamentoInterno);
    }


    public Boolean administrarHorario(Long sucursalId, List<HorarioAtencion> horarios) {
        List<HorarioAtencion> existentes = horarioAtencionRepository.findBySucursalId(sucursalId);
        horarioAtencionRepository.deleteAll(existentes);
        horarios.forEach(h -> h.setSucursalId(sucursalId));
        horarioAtencionRepository.saveAll(horarios);
        return true;
    }


    public List<HorarioAtencion> consultarHorariosTienda(Long sucursalId) {
        return horarioAtencionRepository.findBySucursalId(sucursalId);
    }

    // ── EstadoTareaPersonal ──


    public List<EstadoTareaPersonal> listarEstadosTarea() {
        return estadoTareaPersonalRepository.findAll();
    }


    public EstadoTareaPersonal obtenerEstadoTarea(Long id) {
        return estadoTareaPersonalRepository.findById(id)
                .orElseThrow(() -> new NoExisteEnBdException("Estado de tarea no encontrado: " + id));
    }


    public EstadoTareaPersonal crearEstadoTarea(EstadoTareaPersonal estado) {
        return estadoTareaPersonalRepository.save(estado);
    }


    public EstadoTareaPersonal editarEstadoTarea(Long id, EstadoTareaPersonal datos) {
        EstadoTareaPersonal existente = obtenerEstadoTarea(id);
        existente.setNombre(datos.getNombre());
        return estadoTareaPersonalRepository.save(existente);
    }


    public boolean eliminarEstadoTarea(Long id) {
        if (!estadoTareaPersonalRepository.existsById(id)) return false;
        estadoTareaPersonalRepository.deleteById(id);
        return true;
    }
}