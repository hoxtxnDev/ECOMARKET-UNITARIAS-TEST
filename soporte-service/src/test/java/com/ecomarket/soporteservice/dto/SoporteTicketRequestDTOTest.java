package com.ecomarket.soporteservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SoporteTicketRequestDTOTest {

    @Test
    void settersWork() {
        SoporteTicketRequestDTO dto = new SoporteTicketRequestDTO();
        dto.setClienteId(1L);
        dto.setCategoriaId(2L);
        dto.setAsunto("Problema con mi pedido");
        dto.setPedidoId(10L);

        assertEquals(1L, dto.getClienteId());
        assertEquals(2L, dto.getCategoriaId());
        assertEquals("Problema con mi pedido", dto.getAsunto());
        assertEquals(10L, dto.getPedidoId());
    }
}
