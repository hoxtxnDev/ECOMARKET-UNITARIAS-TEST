package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClienteDTOTest {

    @Test
    void constructorAndGettersWork() {
        ClienteDTO dto = new ClienteDTO(1L, "Juan", "juan@email.com", "555-0100");

        assertEquals(1L, dto.getId());
        assertEquals("Juan", dto.getNombre());
        assertEquals("juan@email.com", dto.getEmail());
        assertEquals("555-0100", dto.getTelefono());
    }
}
