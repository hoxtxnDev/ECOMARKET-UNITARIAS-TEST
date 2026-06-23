package com.ecomarket.soporteservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NotificacionRequestDTOTest {

    @Test
    void settersWork() {
        NotificacionRequestDTO dto = new NotificacionRequestDTO();
        dto.setDestinatarioId(1L);
        dto.setTitulo("Notificación");
        dto.setMensaje("Su pedido ha sido enviado");
        dto.setCanalId(1L);

        assertEquals(1L, dto.getDestinatarioId());
        assertEquals("Notificación", dto.getTitulo());
        assertEquals("Su pedido ha sido enviado", dto.getMensaje());
        assertEquals(1L, dto.getCanalId());
    }
}
