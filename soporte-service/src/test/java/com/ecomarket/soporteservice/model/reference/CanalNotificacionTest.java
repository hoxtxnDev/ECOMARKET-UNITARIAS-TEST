package com.ecomarket.soporteservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CanalNotificacionTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        CanalNotificacion cn = new CanalNotificacion(1L, "EMAIL");
        assertEquals(1L, cn.getId());
        assertEquals("EMAIL", cn.getNombre());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        CanalNotificacion cn = new CanalNotificacion();
        cn.setId(2L);
        cn.setNombre("SMS");
        assertEquals(2L, cn.getId());
        assertEquals("SMS", cn.getNombre());
    }
}
