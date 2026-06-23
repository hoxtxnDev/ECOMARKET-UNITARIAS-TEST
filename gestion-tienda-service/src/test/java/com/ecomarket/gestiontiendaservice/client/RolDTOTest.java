package com.ecomarket.gestiontiendaservice.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RolDTOTest {

    @Test
    void constructorAndGettersWork() {
        RolDTO dto = new RolDTO(1L, "ADMIN", "Administrador del sistema");

        assertEquals(1L, dto.getId());
        assertEquals("ADMIN", dto.getNombre());
        assertEquals("Administrador del sistema", dto.getDescripcion());
    }
}
