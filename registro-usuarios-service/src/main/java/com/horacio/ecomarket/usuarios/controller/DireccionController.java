package com.horacio.ecomarket.usuarios.controller;

import com.horacio.ecomarket.usuarios.dto.CrearDireccionRequest;
import com.horacio.ecomarket.usuarios.model.Direccion;
import com.horacio.ecomarket.usuarios.service.DireccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/direcciones")
@RequiredArgsConstructor
public class DireccionController {

    private final DireccionService direccionService;

    @PostMapping
    public ResponseEntity<Direccion> agregar(
            @RequestHeader("X-User-Id") Long usuarioId,
            @RequestBody CrearDireccionRequest request) {
        Direccion direccion = Direccion.builder()
                .calle(request.getCalle())
                .numero(request.getNumero())
                .departamento(request.getDepartamento())
                .ciudad(request.getCiudad())
                .region(request.getRegion())
                .codigoPostal(request.getCodigoPostal())
                .destinatario(request.getDestinatario())
                .esPredeterminada(request.getEsPredeterminada())
                .build();
        return ResponseEntity.ok(direccionService.agregarDireccion(usuarioId, direccion));
    }

    @PostMapping("/{usuarioId}")
    public ResponseEntity<Direccion> agregarAdmin(
            @PathVariable Long usuarioId,
            @RequestBody CrearDireccionRequest request) {
        Direccion direccion = Direccion.builder()
                .calle(request.getCalle())
                .numero(request.getNumero())
                .departamento(request.getDepartamento())
                .ciudad(request.getCiudad())
                .region(request.getRegion())
                .codigoPostal(request.getCodigoPostal())
                .destinatario(request.getDestinatario())
                .esPredeterminada(request.getEsPredeterminada())
                .build();
        return ResponseEntity.ok(direccionService.agregarDireccion(usuarioId, direccion));
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<Direccion>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(direccionService.listarDirecciones(usuarioId));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Direccion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(direccionService.obtenerPorId(id));
    }

    @GetMapping("/predeterminada/{usuarioId}")
    public ResponseEntity<Direccion> obtenerPredeterminada(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(direccionService.obtenerPredeterminada(usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Direccion> editar(@PathVariable Long id, @RequestBody Direccion direccion) {
        return ResponseEntity.ok(direccionService.editarDireccion(id, direccion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        direccionService.eliminarDireccion(id);
        return ResponseEntity.noContent().build();
    }
}
