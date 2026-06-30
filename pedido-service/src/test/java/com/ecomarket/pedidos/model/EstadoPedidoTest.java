package com.ecomarket.pedidos.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoPedidoTest {

    @Test
    void builderAndGettersSettersWork() {
        EstadoPedido ep = EstadoPedido.builder()
                .id(1L)
                .nombre("PENDIENTE")
                .build();
        assertEquals(1L, ep.getId());
        assertEquals("PENDIENTE", ep.getNombre());
    }

    @Test
    void allArgsConstructorWorks() {
        EstadoPedido ep = new EstadoPedido(1L, "ENVIADO");
        assertEquals("ENVIADO", ep.getNombre());
    }
}
