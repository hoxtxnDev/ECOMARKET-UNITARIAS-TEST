package com.horacio.ecomarket.usuarios.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PermisoTest {

    @Test
    void builderAndGettersSettersWork() {
        Permiso p = Permiso.builder()
                .id(1L)
                .nombre("LEER_PRODUCTOS")
                .descripcion("Permite leer productos")
                .build();
        assertEquals(1L, p.getId());
        assertEquals("LEER_PRODUCTOS", p.getNombre());
        assertEquals("Permite leer productos", p.getDescripcion());
    }

    @Test
    void allArgsConstructorWorks() {
        Permiso p = new Permiso(1L, "EDITAR_USUARIOS", "Permite editar usuarios");
        assertEquals("EDITAR_USUARIOS", p.getNombre());
        assertEquals("Permite editar usuarios", p.getDescripcion());
    }
}
