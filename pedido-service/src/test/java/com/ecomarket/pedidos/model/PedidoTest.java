package com.ecomarket.pedidos.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class PedidoTest {

    @Test
    void builderAndGettersSettersWork() {
        EstadoPedido estado = new EstadoPedido(1L, "PENDIENTE");
        LocalDateTime now = LocalDateTime.now();
        Pedido p = Pedido.builder()
                .id(1L)
                .clienteId(10L)
                .subtotal(100.0)
                .total(120.0)
                .direccionEnvioId(5L)
                .metodoPagoId(3L)
                .estado(estado)
                .fechaCreacion(now)
                .build();
        assertEquals(1L, p.getId());
        assertEquals(10L, p.getClienteId());
        assertEquals(100.0, p.getSubtotal());
        assertEquals(120.0, p.getTotal());
        assertEquals(5L, p.getDireccionEnvioId());
        assertEquals(3L, p.getMetodoPagoId());
        assertEquals(estado, p.getEstado());
        assertEquals(now, p.getFechaCreacion());
    }

    @Test
    void noArgsConstructorWorks() {
        Pedido p = new Pedido();
        assertNull(p.getId());
        assertNull(p.getEstado());
    }
}
