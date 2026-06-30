package com.ecomarket.envioservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class RutaTransporteTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        List<Long> envios = List.of(1L, 2L);
        RutaTransporte r = new RutaTransporte(1L, 10L, now, true, envios);
        assertEquals(1L, r.getId());
        assertEquals(10L, r.getTransportistaId());
        assertEquals(now, r.getFechaRuta());
        assertTrue(r.getCompletada());
        assertEquals(envios, r.getEnviosIds());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        RutaTransporte r = new RutaTransporte();
        r.setId(2L);
        r.setCompletada(false);
        assertFalse(r.getCompletada());
    }

    @Test
    void enviosIdsDefaultIsEmptyList() {
        RutaTransporte r = new RutaTransporte();
        assertNotNull(r.getEnviosIds());
        assertTrue(r.getEnviosIds().isEmpty());
    }
}
