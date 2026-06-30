package com.horacio.ecomarket.usuarios.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoPerfilTest {

    @Test
    void builderAndGettersSettersWork() {
        EstadoPerfil ep = EstadoPerfil.builder()
                .id(1L)
                .nombre("ACTIVO")
                .build();
        assertEquals(1L, ep.getId());
        assertEquals("ACTIVO", ep.getNombre());
    }

    @Test
    void allArgsConstructorWorks() {
        EstadoPerfil ep = new EstadoPerfil(1L, "INACTIVO");
        assertEquals(1L, ep.getId());
        assertEquals("INACTIVO", ep.getNombre());
    }

    @Test
    void noArgsConstructorWorks() {
        EstadoPerfil ep = new EstadoPerfil();
        assertNull(ep.getId());
        assertNull(ep.getNombre());
    }
}
