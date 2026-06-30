package com.ecomarket.envioservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.ecomarket.envioservice.model.reference.EstadoEnvio;

class HistorialEnvioTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoEnvio estado = new EstadoEnvio(1L, "EN_TRANSITO");
        LocalDateTime now = LocalDateTime.now();
        HistorialEnvio h = new HistorialEnvio(1L, 10L, estado, now, "Paquete en camino");
        assertEquals(1L, h.getId());
        assertEquals(10L, h.getEnvioId());
        assertEquals(estado, h.getEstado());
        assertEquals(now, h.getFechaActualizacion());
        assertEquals("Paquete en camino", h.getObservacion());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        HistorialEnvio h = new HistorialEnvio();
        h.setId(2L);
        h.setObservacion("Entregado");
        assertEquals("Entregado", h.getObservacion());
    }
}
