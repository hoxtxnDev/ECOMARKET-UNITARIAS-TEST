package com.ecomarket.soporteservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoPedidoDTOTest {

    @Test
    void settersWork() {
        EstadoPedidoDTO dto = new EstadoPedidoDTO();
        dto.setIdEstadoPedido(1L);
        dto.setNombre("ENTREGADO");

        assertEquals(1L, dto.getIdEstadoPedido());
        assertEquals("ENTREGADO", dto.getNombre());
    }
}
