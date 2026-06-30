package com.ecomarket.catalogoinventarioservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CategoriaProductoTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        CategoriaProducto cp = new CategoriaProducto(1L, "Electrónica");
        assertEquals(1L, cp.getId());
        assertEquals("Electrónica", cp.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        CategoriaProducto cp = new CategoriaProducto();
        cp.setId(2L);
        cp.setNombre("Hogar");
        assertEquals(2L, cp.getId());
        assertEquals("Hogar", cp.getNombre());
    }
}
