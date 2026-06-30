package com.ecomarket.soporteservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class MensajeChatTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        LocalDateTime now = LocalDateTime.now();
        MensajeChat m = new MensajeChat(1L, 10L, 5L, true, "Hola, necesito ayuda", now, false);
        assertEquals(1L, m.getId());
        assertEquals(10L, m.getTicketId());
        assertEquals(5L, m.getRemitenteId());
        assertTrue(m.getEsCliente());
        assertEquals("Hola, necesito ayuda", m.getContenido());
        assertEquals(now, m.getFechaEnvio());
        assertFalse(m.getLeido());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        MensajeChat m = new MensajeChat();
        m.setId(2L);
        m.setContenido("Gracias");
        m.setLeido(true);
        assertEquals("Gracias", m.getContenido());
        assertTrue(m.getLeido());
    }
}
