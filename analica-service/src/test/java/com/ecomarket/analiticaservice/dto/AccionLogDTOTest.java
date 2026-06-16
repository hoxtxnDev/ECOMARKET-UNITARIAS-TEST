package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AccionLogDTOTest {

    @Test
    void builderCreatesAccionLog() {
        AccionLogDTO dto = AccionLogDTO.builder()
                .microservicio("test")
                .accion("CREAR")
                .usuarioId(1L)
                .detalles("detalle")
                .build();

        assertEquals("test", dto.getMicroservicio());
        assertEquals("CREAR", dto.getAccion());
        assertEquals(1L, dto.getUsuarioId());
    }
}
