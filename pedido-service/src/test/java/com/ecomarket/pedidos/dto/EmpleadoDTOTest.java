package com.ecomarket.pedidos.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmpleadoDTOTest {

    @Test
    void constructorAndGettersWork() {
        RolDTO rol = new RolDTO(1L, "VENDEDOR", "Vendedor");
        EmpleadoDTO dto = new EmpleadoDTO(1L, rol);

        assertEquals(1L, dto.getId());
        assertNotNull(dto.getRol());
        assertEquals("VENDEDOR", dto.getRol().getNombre());
    }
}
