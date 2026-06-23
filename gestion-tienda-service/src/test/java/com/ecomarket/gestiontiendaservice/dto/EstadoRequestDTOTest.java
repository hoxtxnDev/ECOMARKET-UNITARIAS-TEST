package com.ecomarket.gestiontiendaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EstadoRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        EstadoRequestDTO dto = new EstadoRequestDTO(1L);

        assertEquals(1L, dto.getEstadoId());
    }
}
