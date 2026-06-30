package com.ecomarket.catalogoinventarioservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EspecificacionTecnicaTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        Producto producto = new Producto();
        producto.setId(1L);
        EspecificacionTecnica et = new EspecificacionTecnica(1L, "Color", "Rojo", producto);
        assertEquals(1L, et.getId());
        assertEquals("Color", et.getClave());
        assertEquals("Rojo", et.getValor());
        assertEquals(producto, et.getProducto());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        EspecificacionTecnica et = new EspecificacionTecnica();
        et.setId(2L);
        et.setClave("Peso");
        et.setValor("1.5 kg");
        assertEquals("Peso", et.getClave());
        assertEquals("1.5 kg", et.getValor());
    }
}
