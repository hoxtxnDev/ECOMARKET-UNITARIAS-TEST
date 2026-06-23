package com.ecomarket.gestiontiendaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SucursalRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        SucursalRequestDTO dto = new SucursalRequestDTO("Central", "Av. Principal 123", "555-0100", 5L);

        assertEquals("Central", dto.getNombre());
        assertEquals("Av. Principal 123", dto.getDireccion());
        assertEquals("555-0100", dto.getTelefono());
        assertEquals(5L, dto.getGerenteCargoId());
    }
}
