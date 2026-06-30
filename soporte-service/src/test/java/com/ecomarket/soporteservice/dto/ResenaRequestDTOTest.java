package com.ecomarket.soporteservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ResenaRequestDTOTest {

    @Test
    void settersWork() {
        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setProductoId(100L);
        dto.setClienteId(10L);
        dto.setCalificacionEstrellas(5);
        dto.setComentario("Excelente producto");

        assertEquals(100L, dto.getProductoId());
        assertEquals(10L, dto.getClienteId());
        assertEquals(5, dto.getCalificacionEstrellas());
        assertEquals("Excelente producto", dto.getComentario());
    }
}
