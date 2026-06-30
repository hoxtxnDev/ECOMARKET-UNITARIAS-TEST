package com.ecomarket.carritocompraservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ItemCarritoTest {

    @Test
    void calcularSubtotalItemMultipliesCantidadByPrecio() {
        ItemCarrito item = new ItemCarrito(1L, new Carrito(), 100L, 3, 25.0, 1);
        assertEquals(75.0, item.calcularSubtotalItem());
    }
}
