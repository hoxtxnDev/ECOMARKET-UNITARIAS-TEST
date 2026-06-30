package com.ecomarket.soporteservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MensajeChatRequestDTOTest {

    @Test
    void settersWork() {
        MensajeChatRequestDTO dto = new MensajeChatRequestDTO();
        dto.setTicketId(1L);
        dto.setContenido("Hola, necesito ayuda");

        assertEquals(1L, dto.getTicketId());
        assertEquals("Hola, necesito ayuda", dto.getContenido());
    }
}
