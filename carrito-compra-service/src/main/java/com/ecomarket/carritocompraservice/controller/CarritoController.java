package com.ecomarket.carritocompraservice.controller;

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

    @GetMapping("/{clienteId}")
    public ResponseEntity<Carrito> obtenerCarrito(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoActivo(clienteId));
    }

    @PostMapping
    public ResponseEntity<Carrito> anadirProducto(
            @RequestBody AnadirProductoRequestDTO dto) {
        return ResponseEntity.ok(carritoService.anadirProducto(
                dto.getUsuarioId(),
                dto.getProductoId(),
                dto.getCantidad()));
    }

    @DeleteMapping("/{clienteId}/item/{itemId}")
    public ResponseEntity<Carrito> removerProducto(
            @PathVariable Long clienteId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.removerProducto(clienteId, itemId));
    }

    @PutMapping("/{clienteId}/envio")
    public ResponseEntity<Carrito> seleccionarEnvio(
            @PathVariable Long clienteId,
            @RequestBody SeleccionRequestDTO dto) {
        return ResponseEntity.ok(carritoService.seleccionarEnvio(clienteId, dto.getId()));
    }

    @PutMapping("/{clienteId}/pago")
    public ResponseEntity<Carrito> seleccionarPago(
            @PathVariable Long clienteId,
            @RequestBody SeleccionRequestDTO dto) {
        return ResponseEntity.ok(carritoService.seleccionarMetodoPago(clienteId, dto.getId()));
    }

    @DeleteMapping("/{clienteId}/vaciar")
    public ResponseEntity<Boolean> vaciarCarrito(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.vaciarCarrito(clienteId));
    }

    @PostMapping("/{clienteId}/checkout")
    public ResponseEntity<Long> iniciarCompra(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.iniciarProcesoCompra(clienteId));
    }

    @PutMapping("/{clienteId}/cerrar")
    public ResponseEntity<Void> cerrarCarrito(@PathVariable Long clienteId) {
        carritoService.cerrarCarrito(clienteId);
        return ResponseEntity.ok().build();
    }
}
