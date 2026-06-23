package com.ecomarket.envioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CrearEnvioRequestDTOTest {

    @Test
    void settersWork() {
        CrearEnvioRequestDTO dto = new CrearEnvioRequestDTO();
        dto.setPedidoId(10L);
        dto.setClienteId(5L);
        dto.setMetodoEnvioId(1L);
        dto.setDireccionId(100L);

        assertEquals(10L, dto.getPedidoId());
        assertEquals(5L, dto.getClienteId());
        assertEquals(1L, dto.getMetodoEnvioId());
        assertEquals(100L, dto.getDireccionId());
    }
}
