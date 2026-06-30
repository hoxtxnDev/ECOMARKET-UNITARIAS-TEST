package com.horacio.ecomarket.usuarios.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PerfilUsuarioTest {

    @Test
    void builderAndGettersSettersWork() {
        Rol rol = new Rol(1L, "ADMIN", "Admin");
        EstadoPerfil ep = new EstadoPerfil(1L, "ACTIVO");
        PerfilUsuario p = PerfilUsuario.builder()
                .id(1L)
                .nombre("Juan")
                .correo("juan@test.com")
                .telefono("123456789")
                .rol(rol)
                .estadoPerfil(ep)
                .build();
        assertEquals(1L, p.getId());
        assertEquals("Juan", p.getNombre());
        assertEquals("juan@test.com", p.getCorreo());
        assertEquals("123456789", p.getTelefono());
        assertEquals(rol, p.getRol());
        assertEquals(ep, p.getEstadoPerfil());
    }

    @Test
    void permisosDefaultIsEmptyList() {
        PerfilUsuario p = new PerfilUsuario();
        assertNotNull(p.getPermisos());
        assertTrue(p.getPermisos().isEmpty());
    }
}
