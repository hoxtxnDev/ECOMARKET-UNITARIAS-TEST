package com.ecomarket.catalogoinventarioservice.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockGlobalTest {

    private StockGlobal stockGlobal;

    @BeforeEach
    void setUp() {
        stockGlobal = new StockGlobal();
        stockGlobal.setId(1L);
        stockGlobal.setProductoId(1L);
        stockGlobal.setCantidadDisponible(100);
    }

    @Test
    void prePersistSetsUltimaActualizacion() {
        assertNull(stockGlobal.getUltimaActualizacion());
        stockGlobal.prePersist();
        assertNotNull(stockGlobal.getUltimaActualizacion());
    }

    @Test
    void incrementarStockIncreasesQuantity() {
        stockGlobal.incrementarStock(50);
        assertEquals(150, stockGlobal.getCantidadDisponible());
        assertNotNull(stockGlobal.getUltimaActualizacion());
    }

    @Test
    void disminuirStockDecreasesQuantity() {
        stockGlobal.disminuirStock(30);
        assertEquals(70, stockGlobal.getCantidadDisponible());
        assertNotNull(stockGlobal.getUltimaActualizacion());
    }

    @Test
    void hayStockReturnsTrueWhenSufficient() {
        assertTrue(stockGlobal.hayStock(100));
        assertTrue(stockGlobal.hayStock(50));
    }

    @Test
    void hayStockReturnsFalseWhenInsufficient() {
        assertFalse(stockGlobal.hayStock(101));
        assertFalse(stockGlobal.hayStock(200));
    }

    @Test
    void hayStockReturnsTrueWhenExactMatch() {
        assertTrue(stockGlobal.hayStock(100));
    }
}
