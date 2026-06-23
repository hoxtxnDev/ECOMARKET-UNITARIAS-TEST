package com.ecomarket.pedidos.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RolDTOTest {

    @Test
    void constructorAndGettersWork() {
        RolDTO dto = new RolDTO(1L, "ADMIN", "Administrador");

        assertEquals(1L, dto.getId());
        assertEquals("ADMIN", dto.getNombre());
        assertEquals("Administrador", dto.getDescripcion());
    }
}
