package com.ecomarket.catalogoinventarioservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductoTest {

    @Test
    void prePersistSetsFechaCreacion() {
        Producto producto = new Producto();
        producto.prePersist();
        assertNotNull(producto.getFechaCreacion());
    }

}
