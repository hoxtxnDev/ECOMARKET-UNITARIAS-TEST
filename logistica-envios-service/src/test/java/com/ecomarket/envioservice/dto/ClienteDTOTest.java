package com.ecomarket.envioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClienteDTOTest {

    @Test
    void settersWork() {
        ClienteDTO dto = new ClienteDTO();
        dto.setClienteId(1L);
        dto.setCorreo("cliente@email.com");
        dto.setNombre("Juan");
        dto.setApellido("Pérez");

        assertEquals(1L, dto.getClienteId());
        assertEquals("cliente@email.com", dto.getCorreo());
        assertEquals("Juan", dto.getNombre());
        assertEquals("Pérez", dto.getApellido());
    }
}
