package com.ecomarket.analiticaservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.ecomarket.analiticaservice.model.reference.NivelAlerta;

class AlertaSistemaTest {

    static class AlertaSistemaSub extends AlertaSistema {
        public AlertaSistemaSub(Long id, NivelAlerta nivel, String mensaje, String moduloOrigen, LocalDateTime fechaAlerta, Boolean resuelta) {
            super(id, nivel, mensaje, moduloOrigen, fechaAlerta, resuelta);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof AlertaSistemaSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AlertaSistema a = new AlertaSistema(1L, nivel, "Error crítico", "Módulo ventas", now, false);

        assertEquals(1L, a.getId());
        assertEquals(nivel, a.getNivel());
        assertEquals("Error crítico", a.getMensaje());
        assertEquals("Módulo ventas", a.getModuloOrigen());
        assertEquals(now, a.getFechaAlerta());
        assertFalse(a.getResuelta());
    }

    @Test
    void noArgsConstructorAndSetters() {
        NivelAlerta nivel = new NivelAlerta(2L, "ADVERTENCIA");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AlertaSistema a = new AlertaSistema();
        a.setId(2L);
        a.setNivel(nivel);
        a.setMensaje("Advertencia");
        a.setModuloOrigen("Módulo inv");
        a.setFechaAlerta(now);
        a.setResuelta(true);

        assertEquals(2L, a.getId());
        assertEquals(nivel, a.getNivel());
        assertEquals("Advertencia", a.getMensaje());
        assertEquals("Módulo inv", a.getModuloOrigen());
        assertEquals(now, a.getFechaAlerta());
        assertTrue(a.getResuelta());
    }

    @Test
    void testToString() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AlertaSistema a = new AlertaSistema(1L, nivel, "msg", "mod", now, false);
        assertNotNull(a.toString());
    }

    @Test
    void testIdentity() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AlertaSistema obj = new AlertaSistema(1L, nivel, "Error", "Mod", now, false);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AlertaSistema obj = new AlertaSistema(1L, nivel, "Error", "Mod", now, false);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        AlertaSistema a = new AlertaSistema(null, null, null, null, null, null);
        AlertaSistema b = new AlertaSistema(null, null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AlertaSistema a = new AlertaSistema(1L, nivel, "Error", "Mod", now, false);
        AlertaSistema b = new AlertaSistema(1L, nivel, "Error", "Mod", now, false);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);

        // f1: id
        assertNotEquals(new AlertaSistema(null, nivel, "msg", "mod", now, false), new AlertaSistema(1L, nivel, "msg", "mod", now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(null, nivel, "msg", "mod", now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(2L, nivel, "msg", "mod", now, false));

        // f2: nivel
        assertNotEquals(new AlertaSistema(1L, null, "msg", "mod", now, false), new AlertaSistema(1L, nivel, "msg", "mod", now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, null, "msg", "mod", now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, new NivelAlerta(2L, "WARN"), "msg", "mod", now, false));

        // f3: mensaje
        assertNotEquals(new AlertaSistema(1L, nivel, null, "mod", now, false), new AlertaSistema(1L, nivel, "msg", "mod", now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, nivel, null, "mod", now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, nivel, "msg2", "mod", now, false));

        // f4: moduloOrigen
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", null, now, false), new AlertaSistema(1L, nivel, "msg", "mod", now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, nivel, "msg", null, now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, nivel, "msg", "mod2", now, false));

        // f5: fechaAlerta
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", null, false), new AlertaSistema(1L, nivel, "msg", "mod", now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, nivel, "msg", "mod", null, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, nivel, "msg", "mod", now.plusDays(1), false));

        // f6: resuelta
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, null), new AlertaSistema(1L, nivel, "msg", "mod", now, false));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, nivel, "msg", "mod", now, null));
        assertNotEquals(new AlertaSistema(1L, nivel, "msg", "mod", now, false), new AlertaSistema(1L, nivel, "msg", "mod", now, true));
    }

    @Test
    void testHashCodeCoverage() {
        AlertaSistema allNull = new AlertaSistema(null, null, null, null, null, null);
        allNull.hashCode();
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AlertaSistema allNonNull = new AlertaSistema(1L, nivel, "Error", "Mod", now, false);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AlertaSistema base = new AlertaSistema(1L, nivel, "msg", "mod", now, false);
        AlertaSistemaSub sub = new AlertaSistemaSub(1L, nivel, "msg", "mod", now, false);
        assertNotEquals(base, sub);
    }
}
