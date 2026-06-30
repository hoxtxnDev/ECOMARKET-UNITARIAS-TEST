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

import com.ecomarket.procesopagoservice.model.MetodoPagoTransaccion;
import com.ecomarket.procesopagoservice.repository.MetodoPagoRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/metodo-pago")
@RequiredArgsConstructor
public class MetodoPagoTransaccionController {

    private final MetodoPagoRepository repository;

    @GetMapping
    public List<MetodoPagoTransaccion> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MetodoPagoTransaccion> obtener(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MetodoPagoTransaccion> crear(@Valid @RequestBody MetodoPagoTransaccion metodo) {
        return ResponseEntity.status(201).body(repository.save(metodo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPagoTransaccion> actualizar(@PathVariable Long id, @Valid @RequestBody MetodoPagoTransaccion datos) {
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
