package com.ecomarket.analiticaservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NivelAlertaTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        NivelAlerta na = new NivelAlerta(1L, "CRITICO");
        assertEquals(1L, na.getId());
        assertEquals("CRITICO", na.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        NivelAlerta na = new NivelAlerta();
        na.setId(2L);
        na.setNombre("ADVERTENCIA");
        assertEquals(2L, na.getId());
        assertEquals("ADVERTENCIA", na.getNombre());
    }
}
