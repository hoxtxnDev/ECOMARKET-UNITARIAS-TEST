package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AlertaRequestDTOTest {

    static class AlertaRequestDTOSub extends AlertaRequestDTO {
        public AlertaRequestDTOSub(Long nivelAlertaId, String mensaje, String moduloOrigen) {
            super(nivelAlertaId, mensaje, moduloOrigen);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof AlertaRequestDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        AlertaRequestDTO dto = new AlertaRequestDTO(1L, "Error crítico", "módulo-test");

        assertEquals(1L, dto.getNivelAlertaId());
        assertEquals("Error crítico", dto.getMensaje());
        assertEquals("módulo-test", dto.getModuloOrigen());
    }

    @Test
    void noArgsConstructorAndSetters() {
        AlertaRequestDTO dto = new AlertaRequestDTO();
        dto.setNivelAlertaId(2L);
        dto.setMensaje("Advertencia");
        dto.setModuloOrigen("otro-modulo");

        assertEquals(2L, dto.getNivelAlertaId());
        assertEquals("Advertencia", dto.getMensaje());
        assertEquals("otro-modulo", dto.getModuloOrigen());
    }

    @Test
    void testToString() {
        AlertaRequestDTO dto = new AlertaRequestDTO(1L, "Error", "mod");
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        AlertaRequestDTO obj = new AlertaRequestDTO(1L, "Error", "mod");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        AlertaRequestDTO obj = new AlertaRequestDTO(1L, "Error", "mod");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        AlertaRequestDTO a = new AlertaRequestDTO(null, null, null);
        AlertaRequestDTO b = new AlertaRequestDTO(null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        AlertaRequestDTO a = new AlertaRequestDTO(1L, "Error", "mod");
        AlertaRequestDTO b = new AlertaRequestDTO(1L, "Error", "mod");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        // f1: nivelAlertaId
        assertNotEquals(new AlertaRequestDTO(null, "msg", "mod"), new AlertaRequestDTO(1L, "msg", "mod"));
        assertNotEquals(new AlertaRequestDTO(1L, "msg", "mod"), new AlertaRequestDTO(null, "msg", "mod"));
        assertNotEquals(new AlertaRequestDTO(1L, "msg", "mod"), new AlertaRequestDTO(2L, "msg", "mod"));

        // f2: mensaje
        assertNotEquals(new AlertaRequestDTO(1L, null, "mod"), new AlertaRequestDTO(1L, "msg", "mod"));
        assertNotEquals(new AlertaRequestDTO(1L, "msg", "mod"), new AlertaRequestDTO(1L, null, "mod"));
        assertNotEquals(new AlertaRequestDTO(1L, "msg", "mod"), new AlertaRequestDTO(1L, "msg2", "mod"));

        // f3: moduloOrigen
        assertNotEquals(new AlertaRequestDTO(1L, "msg", null), new AlertaRequestDTO(1L, "msg", "mod"));
        assertNotEquals(new AlertaRequestDTO(1L, "msg", "mod"), new AlertaRequestDTO(1L, "msg", null));
        assertNotEquals(new AlertaRequestDTO(1L, "msg", "mod"), new AlertaRequestDTO(1L, "msg", "mod2"));
    }

    @Test
    void testHashCodeCoverage() {
        AlertaRequestDTO allNull = new AlertaRequestDTO(null, null, null);
        allNull.hashCode();
        AlertaRequestDTO allNonNull = new AlertaRequestDTO(1L, "Error", "mod");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        AlertaRequestDTO base = new AlertaRequestDTO(1L, "TEST", "MOD");
        AlertaRequestDTOSub sub = new AlertaRequestDTOSub(1L, "TEST", "MOD");
        assertNotEquals(base, sub);
    }
}
