package com.ecomarket.envioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TransportistaDTOTest {

    @Test
    void settersWork() {
        TransportistaDTO dto = new TransportistaDTO();
        dto.setUsuarioId(1L);
        dto.setNombre("Carlos");
        dto.setApellido("López");
        dto.setCorreo("carlos@transportes.com");

        assertEquals(1L, dto.getUsuarioId());
        assertEquals("Carlos", dto.getNombre());
        assertEquals("López", dto.getApellido());
        assertEquals("carlos@transportes.com", dto.getCorreo());
    }
}
