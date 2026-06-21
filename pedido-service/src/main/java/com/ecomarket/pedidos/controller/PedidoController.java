package com.ecomarket.pedidos.controller;

import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping("/generar/{clienteId}/{carritoId}")
    public ResponseEntity<Pedido> generarPedido(
            @PathVariable Long clienteId,
            @PathVariable Long carritoId,
            @RequestParam(required = false) Long direccionEnvioId) {
        return ResponseEntity.ok(pedidoService.generarPedidoDesdeCarrito(clienteId, carritoId, direccionEnvioId));
    }

    @PutMapping("/{pedidoId}/estado/{estadoId}")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long pedidoId, @PathVariable Long estadoId) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(pedidoId, estadoId));
    }

    @PutMapping("/{pedidoId}/estado-nombre")
    public ResponseEntity<Pedido> actualizarEstadoPorNombre(@PathVariable Long pedidoId, @RequestBody String nombreEstado) {
        return ResponseEntity.ok(pedidoService.actualizarEstadoPorNombre(pedidoId, nombreEstado));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> obtenerHistorial(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.obtenerHistorialCliente(clienteId));
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.buscarPorId(pedidoId));
    }
}
