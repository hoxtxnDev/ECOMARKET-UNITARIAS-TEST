package com.ecomarket.gestiontiendaservice.controller;

import com.ecomarket.gestiontiendaservice.dto.EstadoRequestDTO;
import com.ecomarket.gestiontiendaservice.dto.GerenteRequestDTO;
import com.ecomarket.gestiontiendaservice.dto.SucursalRequestDTO;
import com.ecomarket.gestiontiendaservice.model.*;
import com.ecomarket.gestiontiendaservice.service.GestionTiendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tienda")
@RequiredArgsConstructor
public class GestionTiendaController {

    private final GestionTiendaService gestionTiendaService;

    @PostMapping("/sucursal")
    public ResponseEntity<Sucursal> registrarSucursal(@RequestBody @Valid SucursalRequestDTO dto) {
        return ResponseEntity.ok(gestionTiendaService.registrarSucursal(dto));
    }

    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<Sucursal> obtenerSucursal(@PathVariable Long sucursalId) {
        return ResponseEntity.ok(gestionTiendaService.obtenerDatosSucursal(sucursalId));
    }

    @GetMapping("/sucursales/activas")
    public ResponseEntity<List<Sucursal>> listarSucursalesActivas() {
        return ResponseEntity.ok(gestionTiendaService.listarSucursalesActivas());
    }

    @PutMapping("/sucursal/{sucursalId}/gerente")
    public ResponseEntity<Sucursal> asignarGerente(
            @PathVariable Long sucursalId,
            @RequestBody @Valid GerenteRequestDTO dto) {
        return ResponseEntity.ok(gestionTiendaService.asignarGerente(sucursalId, dto.getGerenteCargoId()));
    }

    @PostMapping("/permisos-pos")
    public ResponseEntity<PermisoPOS> configurarPermisoPOS(@RequestBody PermisoPOS permisoPOS) {
        return ResponseEntity.ok(gestionTiendaService.configurarPermisoPOS(permisoPOS));
    }

    @PostMapping("/tarea")
    public ResponseEntity<TareaPersonal> asignarTarea(@RequestBody TareaPersonal tarea) {
        return ResponseEntity.ok(gestionTiendaService.asignarTareaPersonal(tarea));
    }

    @PatchMapping("/tareas/{tareaId}/estado")
    public ResponseEntity<TareaPersonal> actualizarEstadoTarea(
            @PathVariable Long tareaId,
            @RequestBody @Valid EstadoRequestDTO dto) {
        return ResponseEntity.ok(gestionTiendaService.actualizarEstadoTarea(tareaId, dto.getEstadoId()));
    }

    // ── EstadoTareaPersonal CRUD ──

    @GetMapping("/estados-tarea")
    public ResponseEntity<List<EstadoTareaPersonal>> listarEstadosTarea() {
        return ResponseEntity.ok(gestionTiendaService.listarEstadosTarea());
    }

    @GetMapping("/estados-tarea/{id}")
    public ResponseEntity<EstadoTareaPersonal> obtenerEstadoTarea(@PathVariable Long id) {
        return ResponseEntity.ok(gestionTiendaService.obtenerEstadoTarea(id));
    }

    @PostMapping("/estados-tarea")
    public ResponseEntity<EstadoTareaPersonal> crearEstadoTarea(@RequestBody @Valid EstadoTareaPersonal estado) {
        return ResponseEntity.ok(gestionTiendaService.crearEstadoTarea(estado));
    }

    @PutMapping("/estados-tarea/{id}")
    public ResponseEntity<EstadoTareaPersonal> editarEstadoTarea(
            @PathVariable Long id,
            @RequestBody @Valid EstadoTareaPersonal datos) {
        return ResponseEntity.ok(gestionTiendaService.editarEstadoTarea(id, datos));
    }

    @DeleteMapping("/estados-tarea/{id}")
    public ResponseEntity<Boolean> eliminarEstadoTarea(@PathVariable Long id) {
        return ResponseEntity.ok(gestionTiendaService.eliminarEstadoTarea(id));
    }

    @PostMapping("/sucursal/{sucursalId}/reglamento")
    public ResponseEntity<ReglamentoInterno> establecerReglamento(
            @RequestBody ReglamentoInterno reglamentoInterno) {
        return ResponseEntity.ok(gestionTiendaService.establecerReglamento(reglamentoInterno));
    }

    @PutMapping("/sucursal/{sucursalId}/horarios")
    public ResponseEntity<Boolean> administrarHorario(
            @PathVariable Long sucursalId,
            @RequestBody List<HorarioAtencion> horarios) {
        return ResponseEntity.ok(gestionTiendaService.administrarHorario(sucursalId, horarios));
    }

    @GetMapping("/sucursal/{sucursalId}/horarios")
    public ResponseEntity<List<HorarioAtencion>> consultarHorarios(@PathVariable Long sucursalId) {
        return ResponseEntity.ok(gestionTiendaService.consultarHorariosTienda(sucursalId));
    }
}
