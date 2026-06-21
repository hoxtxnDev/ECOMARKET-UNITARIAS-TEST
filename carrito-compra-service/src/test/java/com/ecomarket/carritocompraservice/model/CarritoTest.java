package com.ecomarket.carritocompraservice.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class CarritoTest {

    @Test
    void calcularTotalReturnsZeroWhenItemsIsNull() {
        Carrito carrito = new Carrito();
        carrito.setItems(null);
        assertEquals(0.0, carrito.calcularTotal());
    }

    @Test
    void calcularTotalReturnsZeroWhenItemsIsEmpty() {
        Carrito carrito = new Carrito();
        carrito.setItems(List.of());
        assertEquals(0.0, carrito.calcularTotal());
    }

    @Test
    void calcularTotalSumsItemSubtTotals() {
        Carrito carrito = new Carrito();
        ItemCarrito item1 = new ItemCarrito(1L, carrito, 100L, 2, 10.0, 1);
        ItemCarrito item2 = new ItemCarrito(2L, carrito, 200L, 3, 15.0, 2);
        carrito.setItems(List.of(item1, item2));
        assertEquals(65.0, carrito.calcularTotal());
    }
}
