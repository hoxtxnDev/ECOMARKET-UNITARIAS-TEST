package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetodoEnvioDTOTest {

    @Test
    void constructorAndGettersWork() {
        MetodoEnvioDTO dto = new MetodoEnvioDTO(1L, "DOMICILIO");

        assertEquals(1L, dto.getId());
        assertEquals("DOMICILIO", dto.getNombre());
    }
}
