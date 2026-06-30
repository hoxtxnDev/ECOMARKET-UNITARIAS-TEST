package com.ecomarket.pedidos.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ItemPedidoTest {

    @Test
    void builderAndGettersSettersWork() {
        ItemPedido item = ItemPedido.builder()
                .id(1L)
                .pedidoId(10L)
                .productoId(100L)
                .cantidad(3)
                .precioUnitarioHistorico(25.0)
                .build();
        assertEquals(1L, item.getId());
        assertEquals(10L, item.getPedidoId());
        assertEquals(100L, item.getProductoId());
        assertEquals(3, item.getCantidad());
        assertEquals(25.0, item.getPrecioUnitarioHistorico());
    }

    @Test
    void allArgsConstructorWorks() {
        ItemPedido item = new ItemPedido(1L, 10L, 100L, 5, 50.0);
        assertEquals(5, item.getCantidad());
        assertEquals(50.0, item.getPrecioUnitarioHistorico());
    }
}
