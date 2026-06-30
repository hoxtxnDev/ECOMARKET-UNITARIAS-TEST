package com.ecomarket.pedidos.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PerfilUsuarioDTOTest {

    @Test
    void constructorAndGettersWork() {
        PerfilUsuarioDTO dto = new PerfilUsuarioDTO(1L, "Juan", "juan@email.com");

        assertEquals(1L, dto.getId());
        assertEquals("Juan", dto.getNombre());
        assertEquals("juan@email.com", dto.getCorreo());
    }
}
