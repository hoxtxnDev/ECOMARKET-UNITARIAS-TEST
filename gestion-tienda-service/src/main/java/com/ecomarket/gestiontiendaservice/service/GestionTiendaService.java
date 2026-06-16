package com.ecomarket.gestiontiendaservice.service;

import com.ecomarket.gestiontiendaservice.model.*;

import java.time.LocalDateTime;
import java.util.List;

public interface GestionTiendaService {

    Sucursal registrarSucursal(String nombre, String direccion, String telefono, Long garanteId);

    Sucursal obtenerDatosSucursal(Long sucursalId);

    List<Sucursal> listarSucursalesActivas();

    PermisoPOS configurarPermisoPOS(PermisoPOS permisoPOS);

    TareaPersonal asignarTareaPersonal(Long empleadoId, Long sucursalId, String titulo,
                                       String descripcionTarea, LocalDateTime limite);

    TareaPersonal actualizarEstadoTarea(Long tareaId, EstadoTareaPersonal nuevoEstado);

    ReglamentoInterno establecerReglamento(ReglamentoInterno reglamentoInterno);

    Boolean administrarHorario(Long sucursalId, List<HorarioAtencion> horarios);

    List<HorarioAtencion> consultarHorariosTienda(Long sucursalId);
}