package com.ecomarket.soporteservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.ecomarket.soporteservice.model.reference.CanalNotificacion;

class NotificacionTest {

    @Test
    void builderAndGettersSettersWork() {
        CanalNotificacion canal = new CanalNotificacion(1L, "EMAIL");
        LocalDateTime now = LocalDateTime.now();
        Notificacion n = Notificacion.builder()
                .id(1L)
                .destinatarioId(10L)
                .canal(canal)
                .titulo("Bienvenido")
                .cuerpo("Gracias por registrarte")
                .fechaEnvioNotificacion(now)
                .enviadaConExito(true)
                .build();
        assertEquals(1L, n.getId());
        assertEquals(10L, n.getDestinatarioId());
        assertEquals(canal, n.getCanal());
        assertEquals("Bienvenido", n.getTitulo());
        assertEquals("Gracias por registrarte", n.getCuerpo());
        assertEquals(now, n.getFechaEnvioNotificacion());
        assertTrue(n.getEnviadaConExito());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        Notificacion n = new Notificacion();
        n.setId(2L);
        n.setTitulo("Alerta");
        n.setEnviadaConExito(false);
        assertEquals("Alerta", n.getTitulo());
        assertFalse(n.getEnviadaConExito());
    }
}
