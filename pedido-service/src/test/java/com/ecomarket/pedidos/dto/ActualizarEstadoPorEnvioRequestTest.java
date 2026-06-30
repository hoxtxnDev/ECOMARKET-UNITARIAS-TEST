package com.ecomarket.pedidos.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ActualizarEstadoPorEnvioRequestTest {

    @Test
    void settersWork() {
        ActualizarEstadoPorEnvioRequest dto = new ActualizarEstadoPorEnvioRequest();
        dto.setPedidoId(1L);
        dto.setEstadoEnvioNombre("ENVIADO");

        assertEquals(1L, dto.getPedidoId());
        assertEquals("ENVIADO", dto.getEstadoEnvioNombre());
    }
}
