package com.ecomarket.catalogoinventarioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SucursalDTOTest {

    @Test
    void constructorAndGettersWork() {
        SucursalDTO dto = new SucursalDTO(1L, "Central", "Av. Principal 123", "555-0100", 5L, true);

        assertEquals(1L, dto.getId());
        assertEquals("Central", dto.getNombre());
        assertEquals("Av. Principal 123", dto.getDireccion());
        assertEquals("555-0100", dto.getTelefono());
        assertEquals(5L, dto.getGerenteCargoId());
        assertTrue(dto.getActiva());
    }
}
