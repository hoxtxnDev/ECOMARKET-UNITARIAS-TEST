package com.ecomarket.pedidos.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ItemCarritoDTOTest {

    @Test
    void builderCreatesItem() {
        ItemCarritoDTO dto = ItemCarritoDTO.builder()
                .productoId(100L)
                .cantidad(3)
                .precioUnitarioAgregado(25.0)
                .build();

        assertEquals(100L, dto.getProductoId());
        assertEquals(3, dto.getCantidad());
        assertEquals(25.0, dto.getPrecioUnitarioAgregado());
    }
}
