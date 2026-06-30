package com.ecomarket.pedidos.controller;

import com.ecomarket.pedidos.dto.ActualizarEstadoPorEnvioRequest;
import com.ecomarket.pedidos.model.Pedido;
import com.ecomarket.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping({"/generar", "/generar/{direccionEnvioId}"})
    public ResponseEntity<Pedido> generarPedido(
            @RequestHeader("X-User-Id") Long clienteId,
            @PathVariable(required = false) Long direccionEnvioId) {
        return ResponseEntity.ok(pedidoService.generarPedidoDesdeCarrito(clienteId, direccionEnvioId));
    }

    @PutMapping("/{pedidoId}/estado/{estadoId}")
    public ResponseEntity<Pedido> actualizarEstado(@PathVariable Long pedidoId, @PathVariable Long estadoId) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(pedidoId, estadoId));
    }

    @PutMapping("/{pedidoId}/estado-nombre")
    public ResponseEntity<Pedido> actualizarEstadoPorNombre(@PathVariable Long pedidoId, @RequestBody String nombreEstado) {
        return ResponseEntity.ok(pedidoService.actualizarEstadoPorNombre(pedidoId, nombreEstado));
    }

    @GetMapping("/cliente")
    public ResponseEntity<List<Pedido>> obtenerHistorial(
            @RequestHeader("X-User-Id") Long clienteId) {
        return ResponseEntity.ok(pedidoService.obtenerHistorialCliente(clienteId));
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<Pedido> obtenerPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.buscarPorId(pedidoId));
    }

    @PostMapping("/internal/actualizar-por-envio")
    public ResponseEntity<Void> actualizarPorEnvio(@Valid @RequestBody ActualizarEstadoPorEnvioRequest request) {
        pedidoService.actualizarEstadoPorEnvio(request.getPedidoId(), request.getEstadoEnvioNombre());
        return ResponseEntity.ok().build();
    }
}
