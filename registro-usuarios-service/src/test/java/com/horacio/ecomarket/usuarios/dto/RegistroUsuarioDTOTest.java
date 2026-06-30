package com.horacio.ecomarket.usuarios.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RegistroUsuarioDTOTest {

    @Test
    void settersWork() {
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setNombre("Juan");
        dto.setCorreo("juan@email.com");
        dto.setContrasenaInicial("pass12345");
        dto.setTelefono("555-0100");
        dto.setRolId(1L);
        dto.setEstadoPerfilId(1L);

        assertEquals("Juan", dto.getNombre());
        assertEquals("juan@email.com", dto.getCorreo());
        assertEquals("pass12345", dto.getContrasenaInicial());
        assertEquals("555-0100", dto.getTelefono());
        assertEquals(1L, dto.getRolId());
        assertEquals(1L, dto.getEstadoPerfilId());
    }
}
