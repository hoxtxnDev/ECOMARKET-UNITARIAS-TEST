package com.horacio.ecomarket.usuarios.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UsuarioDTOTest {

    @Test
    void settersWork() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Juan");
        dto.setCorreo("juan@email.com");
        dto.setPassword("pass12345");
        dto.setTelefono("555-0100");

        assertEquals("Juan", dto.getNombre());
        assertEquals("juan@email.com", dto.getCorreo());
        assertEquals("pass12345", dto.getPassword());
        assertEquals("555-0100", dto.getTelefono());
    }
}
