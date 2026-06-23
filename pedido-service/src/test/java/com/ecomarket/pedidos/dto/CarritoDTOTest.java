package com.ecomarket.pedidos.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class CarritoDTOTest {

    @Test
    void builderCreatesCarrito() {
        ItemCarritoDTO item = ItemCarritoDTO.builder()
                .productoId(100L)
                .cantidad(2)
                .precioUnitarioAgregado(50.0)
                .build();

        CarritoDTO dto = CarritoDTO.builder()
                .id(1L)
                .clienteId(10L)
                .subtotal(100.0)
                .items(List.of(item))
                .build();

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getClienteId());
        assertEquals(100.0, dto.getSubtotal());
        assertEquals(1, dto.getItems().size());
    }
}
