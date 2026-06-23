package com.horacio.ecomarket.usuarios.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class ConfigurarPermisosDTOTest {

    @Test
    void settersWork() {
        ConfigurarPermisosDTO dto = new ConfigurarPermisosDTO();
        dto.setPermisoIds(List.of(1L, 2L, 3L));

        assertEquals(3, dto.getPermisoIds().size());
        assertTrue(dto.getPermisoIds().contains(1L));
    }
}
