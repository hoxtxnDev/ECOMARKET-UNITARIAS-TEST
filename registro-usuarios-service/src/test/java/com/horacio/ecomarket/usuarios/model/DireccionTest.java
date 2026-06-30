package com.horacio.ecomarket.usuarios.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DireccionTest {

    @Test
    void builderAndGettersSettersWork() {
        Direccion d = Direccion.builder()
                .id(1L)
                .usuarioId(10L)
                .calle("Av. Siempre Viva")
                .numero("742")
                .departamento("A")
                .ciudad("Springfield")
                .region("Centro")
                .codigoPostal("12345")
                .destinatario("Homero")
                .esPredeterminada(true)
                .build();
        assertEquals(1L, d.getId());
        assertEquals(10L, d.getUsuarioId());
        assertEquals("Av. Siempre Viva", d.getCalle());
        assertEquals("742", d.getNumero());
        assertEquals("A", d.getDepartamento());
        assertEquals("Springfield", d.getCiudad());
        assertEquals("Centro", d.getRegion());
        assertEquals("12345", d.getCodigoPostal());
        assertEquals("Homero", d.getDestinatario());
        assertTrue(d.getEsPredeterminada());
    }

    @Test
    void noArgsAndAllArgsConstructorsWork() {
        Direccion d = new Direccion(1L, 10L, "Calle", "123", "Depto", "Ciudad", "Region", "CP", "Dest", false);
        assertNotNull(d);
        assertEquals("Calle", d.getCalle());
    }

    @Test
    void defaultEsPredeterminadaIsFalse() {
        Direccion d = new Direccion();
        assertFalse(d.getEsPredeterminada());
    }
}
