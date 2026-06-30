package com.ecomarket.carritocompraservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomarket.carritocompraservice.dto.AnadirProductoRequestDTO;
import com.ecomarket.carritocompraservice.dto.SeleccionRequestDTO;
import com.ecomarket.carritocompraservice.model.Carrito;
import com.ecomarket.carritocompraservice.service.CarritoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {
    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<List<Carrito>> listarCarritos() {
        return ResponseEntity.ok(carritoService.listarTodos());
    }

    @GetMapping("/activo")
    public ResponseEntity<Carrito> obtenerCarritoActivo(@RequestHeader("X-User-Id") Long clienteId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoActivo(clienteId));
    }

    @PostMapping
    public ResponseEntity<Carrito> anadirProducto(
            @RequestHeader("X-User-Id") Long usuarioId,
            @RequestBody AnadirProductoRequestDTO dto) {
        return ResponseEntity.ok(carritoService.anadirProducto(
                usuarioId,
                dto.getProductoId(),
                dto.getCantidad()));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Carrito> removerProducto(
            @RequestHeader("X-User-Id") Long clienteId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.removerProducto(clienteId, itemId));
    }

    @PutMapping("/envio")
    public ResponseEntity<Carrito> seleccionarEnvio(
            @RequestHeader("X-User-Id") Long clienteId,
            @RequestBody SeleccionRequestDTO dto) {
        return ResponseEntity.ok(carritoService.seleccionarEnvio(clienteId, dto.getId()));
    }

    @PutMapping("/pago")
    public ResponseEntity<Carrito> seleccionarPago(
            @RequestHeader("X-User-Id") Long clienteId,
            @RequestBody SeleccionRequestDTO dto) {
        return ResponseEntity.ok(carritoService.seleccionarMetodoPago(clienteId, dto.getId()));
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<Boolean> vaciarCarrito(@RequestHeader("X-User-Id") Long clienteId) {
        return ResponseEntity.ok(carritoService.vaciarCarrito(clienteId));
    }

    @PutMapping("/cerrar")
    public ResponseEntity<Void> cerrarCarrito(@RequestHeader("X-User-Id") Long clienteId) {
        carritoService.cerrarCarrito(clienteId);
        return ResponseEntity.ok().build();
    }
}
