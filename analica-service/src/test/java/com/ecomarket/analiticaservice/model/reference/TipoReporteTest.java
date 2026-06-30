package com.ecomarket.analiticaservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TipoReporteTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        TipoReporte tr = new TipoReporte(1L, "VENTAS");
        assertEquals(1L, tr.getId());
        assertEquals("VENTAS", tr.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        TipoReporte tr = new TipoReporte();
        tr.setId(2L);
        tr.setNombre("INVENTARIO");
        assertEquals(2L, tr.getId());
        assertEquals("INVENTARIO", tr.getNombre());
    }
}
