package com.ecomarket.envioservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DireccionTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        Direccion d = new Direccion(1L, "Av. Principal", "123", "Depto 4", "Santiago", "6500000", -33.45, -70.65);
        assertEquals(1L, d.getId());
        assertEquals("Av. Principal", d.getCalle());
        assertEquals("123", d.getNumero());
        assertEquals("Depto 4", d.getDepartamento());
        assertEquals("Santiago", d.getCiudad());
        assertEquals("6500000", d.getCodigoPostal());
        assertEquals(-33.45, d.getLatitud());
        assertEquals(-70.65, d.getLongitud());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        Direccion d = new Direccion();
        d.setId(2L);
        d.setCalle("Calle Secundaria");
        d.setNumero("456");
        assertEquals("Calle Secundaria", d.getCalle());
        assertEquals("456", d.getNumero());
    }
}
