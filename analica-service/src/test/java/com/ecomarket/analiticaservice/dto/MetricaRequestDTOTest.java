package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MetricaRequestDTOTest {

    static class MetricaRequestDTOSub extends MetricaRequestDTO {
        public MetricaRequestDTOSub(String claveMetrica, Double valorNumerico, String valorTexto) {
            super(claveMetrica, valorNumerico, valorTexto);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof MetricaRequestDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        MetricaRequestDTO dto = new MetricaRequestDTO("ventas_diarias", 1500.0, "pico");

        assertEquals("ventas_diarias", dto.getClaveMetrica());
        assertEquals(1500.0, dto.getValorNumerico());
        assertEquals("pico", dto.getValorTexto());
    }

    @Test
    void noArgsConstructorAndSetters() {
        MetricaRequestDTO dto = new MetricaRequestDTO();
        dto.setClaveMetrica("usuarios_nuevos");
        dto.setValorNumerico(100.0);
        dto.setValorTexto("meta");

        assertEquals("usuarios_nuevos", dto.getClaveMetrica());
        assertEquals(100.0, dto.getValorNumerico());
        assertEquals("meta", dto.getValorTexto());
    }

    @Test
    void testToString() {
        MetricaRequestDTO dto = new MetricaRequestDTO("C", 1.0, "T");
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        MetricaRequestDTO obj = new MetricaRequestDTO("ventas", 1500.0, "pico");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        MetricaRequestDTO obj = new MetricaRequestDTO("ventas", 1500.0, "pico");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        MetricaRequestDTO a = new MetricaRequestDTO(null, null, null);
        MetricaRequestDTO b = new MetricaRequestDTO(null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        MetricaRequestDTO a = new MetricaRequestDTO("ventas", 1500.0, "pico");
        MetricaRequestDTO b = new MetricaRequestDTO("ventas", 1500.0, "pico");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        // f1: claveMetrica
        assertNotEquals(new MetricaRequestDTO(null, 1.0, "t"), new MetricaRequestDTO("c1", 1.0, "t"));
        assertNotEquals(new MetricaRequestDTO("c1", 1.0, "t"), new MetricaRequestDTO(null, 1.0, "t"));
        assertNotEquals(new MetricaRequestDTO("c1", 1.0, "t"), new MetricaRequestDTO("c2", 1.0, "t"));

        // f2: valorNumerico
        assertNotEquals(new MetricaRequestDTO("c1", null, "t"), new MetricaRequestDTO("c1", 1.0, "t"));
        assertNotEquals(new MetricaRequestDTO("c1", 1.0, "t"), new MetricaRequestDTO("c1", null, "t"));
        assertNotEquals(new MetricaRequestDTO("c1", 1.0, "t"), new MetricaRequestDTO("c1", 2.0, "t"));

        // f3: valorTexto
        assertNotEquals(new MetricaRequestDTO("c1", 1.0, null), new MetricaRequestDTO("c1", 1.0, "t"));
        assertNotEquals(new MetricaRequestDTO("c1", 1.0, "t"), new MetricaRequestDTO("c1", 1.0, null));
        assertNotEquals(new MetricaRequestDTO("c1", 1.0, "t"), new MetricaRequestDTO("c1", 1.0, "t2"));
    }

    @Test
    void testHashCodeCoverage() {
        MetricaRequestDTO allNull = new MetricaRequestDTO(null, null, null);
        allNull.hashCode();
        MetricaRequestDTO allNonNull = new MetricaRequestDTO("ventas", 1500.0, "pico");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        MetricaRequestDTO base = new MetricaRequestDTO("C", 1.0, "T");
        MetricaRequestDTOSub sub = new MetricaRequestDTOSub("C", 1.0, "T");
        assertNotEquals(base, sub);
    }
}
