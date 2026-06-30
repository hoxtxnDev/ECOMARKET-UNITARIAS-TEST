package com.horacio.ecomarket.usuarios.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CrearDireccionRequestTest {

    @Test
    void settersWork() {
        CrearDireccionRequest dto = new CrearDireccionRequest();
        dto.setCalle("Av. Siempre Viva");
        dto.setNumero("742");
        dto.setDepartamento("A");
        dto.setCiudad("Springfield");
        dto.setRegion("USA");
        dto.setCodigoPostal("12345");
        dto.setDestinatario("Homero");
        dto.setEsPredeterminada(true);

        assertEquals("Av. Siempre Viva", dto.getCalle());
        assertEquals("742", dto.getNumero());
        assertEquals("A", dto.getDepartamento());
        assertEquals("Springfield", dto.getCiudad());
        assertEquals("USA", dto.getRegion());
        assertEquals("12345", dto.getCodigoPostal());
        assertEquals("Homero", dto.getDestinatario());
        assertTrue(dto.getEsPredeterminada());
    }
}
