package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CarritoDTOTest {

    @Test
    void constructorAndGettersWork() {
        CarritoDTO dto = new CarritoDTO(1L, 10L, 100L, 3, 25.50);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getClienteId());
        assertEquals(100L, dto.getProductoId());
        assertEquals(3, dto.getCantidad());
        assertEquals(25.50, dto.getPrecioUnitario());
    }
}
