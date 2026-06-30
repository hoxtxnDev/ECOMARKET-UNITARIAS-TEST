package com.ecomarket.analiticaservice.model.reference;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NivelAlertaTest {

    static class NivelAlertaSub extends NivelAlerta {
        public NivelAlertaSub(Long id, String nombre) { super(id, nombre); }
        @Override
        public boolean canEqual(Object other) { return other instanceof NivelAlertaSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        NivelAlerta na = new NivelAlerta(1L, "CRITICO");
        assertEquals(1L, na.getId());
        assertEquals("CRITICO", na.getNombre());
    }

    @Test
    void noArgsConstructorAndSetters() {
        NivelAlerta na = new NivelAlerta();
        na.setId(2L);
        na.setNombre("ADVERTENCIA");

        assertEquals(2L, na.getId());
        assertEquals("ADVERTENCIA", na.getNombre());
    }

    @Test
    void testToString() {
        NivelAlerta na = new NivelAlerta(1L, "CRITICO");
        assertNotNull(na.toString());
    }

    @Test
    void testIdentity() {
        NivelAlerta obj = new NivelAlerta(1L, "TEST");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        NivelAlerta obj = new NivelAlerta(1L, "TEST");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        NivelAlerta a = new NivelAlerta(null, null);
        NivelAlerta b = new NivelAlerta(null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        NivelAlerta a = new NivelAlerta(1L, "TEST");
        NivelAlerta b = new NivelAlerta(1L, "TEST");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        assertNotEquals(new NivelAlerta(null, "TEST"), new NivelAlerta(1L, "TEST"));
        assertNotEquals(new NivelAlerta(1L, "TEST"), new NivelAlerta(null, "TEST"));
        assertNotEquals(new NivelAlerta(1L, "TEST"), new NivelAlerta(2L, "TEST"));

        assertNotEquals(new NivelAlerta(1L, null), new NivelAlerta(1L, "TEST"));
        assertNotEquals(new NivelAlerta(1L, "TEST"), new NivelAlerta(1L, null));
        assertNotEquals(new NivelAlerta(1L, "TEST"), new NivelAlerta(1L, "OTHER"));
    }

    @Test
    void testHashCodeCoverage() {
        NivelAlerta allNull = new NivelAlerta(null, null);
        allNull.hashCode();
        NivelAlerta allNonNull = new NivelAlerta(1L, "TEST");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        NivelAlerta base = new NivelAlerta(1L, "TEST");
        NivelAlertaSub sub = new NivelAlertaSub(1L, "TEST");
        assertNotEquals(base, sub);
    }
}
