package com.horacio.ecomarket.usuarios.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RolTest {

    @Test
    void builderAndGettersSettersWork() {
        Rol r = Rol.builder()
                .id(1L)
                .nombre("ADMIN")
                .descripcion("Administrador")
                .build();
        assertEquals(1L, r.getId());
        assertEquals("ADMIN", r.getNombre());
        assertEquals("Administrador", r.getDescripcion());
    }

    @Test
    void allArgsConstructorWorks() {
        Rol r = new Rol(2L, "CLIENTE", "Cliente regular");
        assertEquals(2L, r.getId());
        assertEquals("CLIENTE", r.getNombre());
    }
}
