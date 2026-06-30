package com.ecomarket.soporteservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.ecomarket.soporteservice.model.reference.CategoriaTicket;
import com.ecomarket.soporteservice.model.reference.EstadoTicket;

class TicketSoporteTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        CategoriaTicket cat = new CategoriaTicket(1L, "PROBLEMA_TECNICO");
        EstadoTicket est = new EstadoTicket(1L, "ABIERTO");
        LocalDateTime now = LocalDateTime.now();
        TicketSoporte t = new TicketSoporte(1L, 10L, 5L, 100L, cat, "No puedo acceder", est, now, now.plusDays(2), "Se resolvió");
        assertEquals(1L, t.getId());
        assertEquals(10L, t.getClienteId());
        assertEquals(5L, t.getEmpleadoAsignadoId());
        assertEquals(100L, t.getPedidoRelacionadoId());
        assertEquals(cat, t.getCategoria());
        assertEquals("No puedo acceder", t.getAsunto());
        assertEquals(est, t.getEstado());
        assertEquals(now, t.getFechaCreacion());
        assertEquals(now.plusDays(2), t.getFechaCierre());
        assertEquals("Se resolvió", t.getSolucionResumen());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        TicketSoporte t = new TicketSoporte();
        t.setId(2L);
        t.setAsunto("Problema con pago");
        assertEquals("Problema con pago", t.getAsunto());
    }
}
