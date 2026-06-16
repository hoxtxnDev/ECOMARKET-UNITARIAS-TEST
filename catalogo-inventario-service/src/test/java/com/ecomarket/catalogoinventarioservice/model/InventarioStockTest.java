package com.ecomarket.catalogoinventarioservice.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InventarioStockTest {

    @Test
    void hayStockReturnsTrueWhenQuantityIsWithinAvailable() {
        InventarioStock stock = new InventarioStock();
        stock.setCantidadDisponible(10);
        assertTrue(stock.hayStock(5));
    }

    @Test
    void hayStockReturnsTrueWhenQuantityEqualsAvailable() {
        InventarioStock stock = new InventarioStock();
        stock.setCantidadDisponible(10);
        assertTrue(stock.hayStock(10));
    }

    @Test
    void hayStockReturnsFalseWhenQuantityExceedsAvailable() {
        InventarioStock stock = new InventarioStock();
        stock.setCantidadDisponible(5);
        assertFalse(stock.hayStock(10));
    }

    @Test
    void hayStockReturnsFalseWhenAvailableIsZero() {
        InventarioStock stock = new InventarioStock();
        stock.setCantidadDisponible(0);
        assertFalse(stock.hayStock(1));
    }

}
