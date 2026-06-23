package com.ecomarket.soporteservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MensajeChatRequestDTOTest {

    @Test
    void settersWork() {
        MensajeChatRequestDTO dto = new MensajeChatRequestDTO();
        dto.setTicketId(1L);
        dto.setRemitenteId(10L);
        dto.setEsCliente(true);
        dto.setContenido("Hola, necesito ayuda");

        assertEquals(1L, dto.getTicketId());
        assertEquals(10L, dto.getRemitenteId());
        assertTrue(dto.getEsCliente());
        assertEquals("Hola, necesito ayuda", dto.getContenido());
    }
}
