package com.ecomarket.gestiontiendaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GerenteRequestDTOTest {

    @Test
    void constructorAndGettersWork() {
        GerenteRequestDTO dto = new GerenteRequestDTO(5L);

        assertEquals(5L, dto.getGerenteCargoId());
    }
}
