package com.ecomarket.procesopagoservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomarket.procesopagoservice.model.EstadoPago;
import com.ecomarket.procesopagoservice.repository.EstadoPagoRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/estado-pago")
@RequiredArgsConstructor
public class EstadoPagoController {

    private final EstadoPagoRepository repository;

    @GetMapping
    public List<EstadoPago> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoPago> obtener(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoPago> crear(@Valid @RequestBody EstadoPago estado) {
        return ResponseEntity.status(201).body(repository.save(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoPago> actualizar(@PathVariable Long id, @Valid @RequestBody EstadoPago datos) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    return ResponseEntity.ok(repository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
