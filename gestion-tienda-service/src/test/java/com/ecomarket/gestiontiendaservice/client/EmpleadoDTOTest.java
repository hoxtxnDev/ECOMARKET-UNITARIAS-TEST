package com.ecomarket.gestiontiendaservice.client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmpleadoDTOTest {

    @Test
    void constructorAndGettersWork() {
        RolDTO rol = new RolDTO(1L, "VENDEDOR", "Vendedor de tienda");
        EmpleadoDTO dto = new EmpleadoDTO(1L, "Juan", "juan@email.com", "555-0100", rol);

        assertEquals(1L, dto.getId());
        assertEquals("Juan", dto.getNombre());
        assertEquals("juan@email.com", dto.getCorreo());
        assertEquals("555-0100", dto.getTelefono());
        assertNotNull(dto.getRol());
        assertEquals("VENDEDOR", dto.getRol().getNombre());
    }
}
