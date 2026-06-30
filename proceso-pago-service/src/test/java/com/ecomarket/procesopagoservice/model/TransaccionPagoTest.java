package com.ecomarket.procesopagoservice.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TransaccionPagoTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        MetodoPagoTransaccion metodo = new MetodoPagoTransaccion(1L, "TARJETA");
        EstadoPago estado = new EstadoPago(1L, "COMPLETADO");
        LocalDateTime now = LocalDateTime.now();
        TransaccionPago t = new TransaccionPago(
                1L, 10L, 5L, 100.0, 10.0, 90.0,
                metodo, estado, 99L,
                "token123", "AUTH456", "idemp-key",
                now, now.plusMinutes(5), now, "OK");
        assertEquals(1L, t.getId());
        assertEquals(10L, t.getPedidoId());
        assertEquals(5L, t.getClienteId());
        assertEquals(100.0, t.getMontoSubtotal());
        assertEquals(10.0, t.getMontoDescuento());
        assertEquals(90.0, t.getMontoTotal());
        assertEquals(metodo, t.getMetodoPago());
        assertEquals(estado, t.getEstado());
        assertEquals(99L, t.getCuponUtilizadoId());
        assertEquals("token123", t.getTokenTransbank());
        assertEquals("AUTH456", t.getCodigoAutorizacion());
        assertEquals("idemp-key", t.getIdempotencyKey());
        assertEquals(now, t.getFechaInicio());
        assertEquals(now.plusMinutes(5), t.getFechaFin());
        assertEquals(now, t.getFechaUltimaActualizacion());
        assertEquals("OK", t.getMensajeError());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        TransaccionPago t = new TransaccionPago();
        t.setId(1L);
        t.setMontoTotal(200.0);
        t.setIdempotencyKey("key-123");
        assertEquals(200.0, t.getMontoTotal());
        assertEquals("key-123", t.getIdempotencyKey());
    }
}
