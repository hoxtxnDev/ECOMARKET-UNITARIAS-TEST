package com.ecomarket.soporteservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ResenaTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        Resena r = new Resena(1L, 100L, 5L, 8, "Muy buen producto, recomendado", now, true);
        assertEquals(1L, r.getId());
        assertEquals(100L, r.getProductoId());
        assertEquals(5L, r.getClienteId());
        assertEquals(8, r.getCalificacionEstrellas());
        assertEquals("Muy buen producto, recomendado", r.getComentario());
        assertEquals(now, r.getFechaPublicacion());
        assertTrue(r.getModeracionAprobado());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        Resena r = new Resena();
        r.setId(2L);
        r.setCalificacionEstrellas(5);
        r.setModeracionAprobado(false);
        assertEquals(5, r.getCalificacionEstrellas());
        assertFalse(r.getModeracionAprobado());
    }
}
