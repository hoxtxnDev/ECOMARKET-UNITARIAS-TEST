package com.ecomarket.soporteservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RolDTOTest {

    @Test
    void settersWork() {
        RolDTO dto = new RolDTO();
        dto.setIdRol(1L);
        dto.setNombre("ADMIN");

        assertEquals(1L, dto.getIdRol());
        assertEquals("ADMIN", dto.getNombre());
    }
}
