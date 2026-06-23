package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RespaldoRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        RespaldoRequestDTO dto = new RespaldoRequestDTO(1L, 256.5, "/backups/db.sql");

        assertEquals(1L, dto.getEstadoRespaldoId());
        assertEquals(256.5, dto.getTamanoMegabytes());
        assertEquals("/backups/db.sql", dto.getRutaAlmacenamiento());
    }
}
