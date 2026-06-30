package com.ecomarket.gestiontiendaservice.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TareaPersonalTest {

    @Test
    void allArgsConstructorAndGettersWork() {
        EstadoTareaPersonal estado = new EstadoTareaPersonal(1L, "PENDIENTE");
        LocalDateTime now = LocalDateTime.now();
        TareaPersonal t = new TareaPersonal(1L, 10L, 5L, 3L, "Limpieza", "Limpiar estantería", estado, now, now.plusDays(1));
        assertEquals(1L, t.getId());
        assertEquals(10L, t.getEmpleadoId());
        assertEquals(5L, t.getGerenteAsignadoId());
        assertEquals(3L, t.getSucursalId());
        assertEquals("Limpieza", t.getTitulo());
        assertEquals("Limpiar estantería", t.getDescripcion());
        assertEquals(estado, t.getEstado());
        assertEquals(now, t.getFechaAsignacion());
        assertEquals(now.plusDays(1), t.getFechaLimite());
    }

    @Test
    void noArgsConstructorAndSettersWork() {
        TareaPersonal t = new TareaPersonal();
        t.setId(2L);
        t.setTitulo("Inventario");
        t.setDescripcion("Contar productos");
        assertEquals("Inventario", t.getTitulo());
        assertEquals("Contar productos", t.getDescripcion());
    }
}
