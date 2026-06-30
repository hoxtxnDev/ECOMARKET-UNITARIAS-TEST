package com.ecomarket.analiticaservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.ecomarket.analiticaservice.model.reference.EstadoRespaldo;

class RespaldoBaseDatosTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoRespaldo estado = new EstadoRespaldo(1L, "EXITOSO");
        LocalDateTime now = LocalDateTime.now();
        RespaldoBaseDatos r = new RespaldoBaseDatos(1L, now, 150.5, estado, "/backups/db.sql");
        assertEquals(1L, r.getId());
        assertEquals(now, r.getFechaRespaldo());
        assertEquals(150.5, r.getTamanoMegabytes());
        assertEquals(estado, r.getEstado());
        assertEquals("/backups/db.sql", r.getRutaAlmacenamiento());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        RespaldoBaseDatos r = new RespaldoBaseDatos();
        r.setId(2L);
        r.setTamanoMegabytes(200.0);
        assertEquals(200.0, r.getTamanoMegabytes());
    }
}
