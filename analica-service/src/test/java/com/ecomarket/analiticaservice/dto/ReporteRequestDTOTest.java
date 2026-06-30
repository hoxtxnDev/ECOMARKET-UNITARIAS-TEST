package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReporteRequestDTOTest {

    static class ReporteRequestDTOSub extends ReporteRequestDTO {
        public ReporteRequestDTOSub(Long solicitanteId, Long tipoReporteId) {
            super(solicitanteId, tipoReporteId);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof ReporteRequestDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        ReporteRequestDTO dto = new ReporteRequestDTO(1L, 3L);

        assertEquals(1L, dto.getSolicitanteId());
        assertEquals(3L, dto.getTipoReporteId());
    }

    @Test
    void noArgsConstructorAndSetters() {
        ReporteRequestDTO dto = new ReporteRequestDTO();
        dto.setSolicitanteId(2L);
        dto.setTipoReporteId(4L);

        assertEquals(2L, dto.getSolicitanteId());
        assertEquals(4L, dto.getTipoReporteId());
    }

    @Test
    void testToString() {
        ReporteRequestDTO dto = new ReporteRequestDTO(1L, 3L);
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        ReporteRequestDTO obj = new ReporteRequestDTO(1L, 3L);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        ReporteRequestDTO obj = new ReporteRequestDTO(1L, 3L);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        ReporteRequestDTO a = new ReporteRequestDTO(null, null);
        ReporteRequestDTO b = new ReporteRequestDTO(null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        ReporteRequestDTO a = new ReporteRequestDTO(1L, 3L);
        ReporteRequestDTO b = new ReporteRequestDTO(1L, 3L);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        // f1: solicitanteId
        assertNotEquals(new ReporteRequestDTO(null, 3L), new ReporteRequestDTO(1L, 3L));
        assertNotEquals(new ReporteRequestDTO(1L, 3L), new ReporteRequestDTO(null, 3L));
        assertNotEquals(new ReporteRequestDTO(1L, 3L), new ReporteRequestDTO(2L, 3L));

        // f2: tipoReporteId
        assertNotEquals(new ReporteRequestDTO(1L, null), new ReporteRequestDTO(1L, 3L));
        assertNotEquals(new ReporteRequestDTO(1L, 3L), new ReporteRequestDTO(1L, null));
        assertNotEquals(new ReporteRequestDTO(1L, 3L), new ReporteRequestDTO(1L, 4L));
    }

    @Test
    void testHashCodeCoverage() {
        ReporteRequestDTO allNull = new ReporteRequestDTO(null, null);
        allNull.hashCode();
        ReporteRequestDTO allNonNull = new ReporteRequestDTO(1L, 3L);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        ReporteRequestDTO base = new ReporteRequestDTO(1L, 3L);
        ReporteRequestDTOSub sub = new ReporteRequestDTOSub(1L, 3L);
        assertNotEquals(base, sub);
    }
}
