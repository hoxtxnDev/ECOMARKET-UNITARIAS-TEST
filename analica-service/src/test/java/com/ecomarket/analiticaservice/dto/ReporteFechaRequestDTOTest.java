package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ReporteFechaRequestDTOTest {

    static class ReporteFechaRequestDTOSub extends ReporteFechaRequestDTO {
        public ReporteFechaRequestDTOSub(Long solicitanteId, Long tipoReporteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
            super(solicitanteId, tipoReporteId, fechaInicio, fechaFin);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof ReporteFechaRequestDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 30, 23, 59);
        ReporteFechaRequestDTO dto = new ReporteFechaRequestDTO(1L, 2L, inicio, fin);

        assertEquals(1L, dto.getSolicitanteId());
        assertEquals(2L, dto.getTipoReporteId());
        assertEquals(inicio, dto.getFechaInicio());
        assertEquals(fin, dto.getFechaFin());
    }

    @Test
    void noArgsConstructorAndSetters() {
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 3, 31, 23, 59);
        ReporteFechaRequestDTO dto = new ReporteFechaRequestDTO();
        dto.setSolicitanteId(3L);
        dto.setTipoReporteId(4L);
        dto.setFechaInicio(inicio);
        dto.setFechaFin(fin);

        assertEquals(3L, dto.getSolicitanteId());
        assertEquals(4L, dto.getTipoReporteId());
        assertEquals(inicio, dto.getFechaInicio());
        assertEquals(fin, dto.getFechaFin());
    }

    @Test
    void testToString() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 30, 23, 59);
        ReporteFechaRequestDTO dto = new ReporteFechaRequestDTO(1L, 2L, inicio, fin);
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 30, 23, 59);
        ReporteFechaRequestDTO obj = new ReporteFechaRequestDTO(1L, 2L, inicio, fin);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 30, 23, 59);
        ReporteFechaRequestDTO obj = new ReporteFechaRequestDTO(1L, 2L, inicio, fin);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        ReporteFechaRequestDTO a = new ReporteFechaRequestDTO(null, null, null, null);
        ReporteFechaRequestDTO b = new ReporteFechaRequestDTO(null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 30, 23, 59);
        ReporteFechaRequestDTO a = new ReporteFechaRequestDTO(1L, 2L, inicio, fin);
        ReporteFechaRequestDTO b = new ReporteFechaRequestDTO(1L, 2L, inicio, fin);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 30, 23, 59);

        // f1: solicitanteId
        assertNotEquals(new ReporteFechaRequestDTO(null, 2L, inicio, fin), new ReporteFechaRequestDTO(1L, 2L, inicio, fin));
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, inicio, fin), new ReporteFechaRequestDTO(null, 2L, inicio, fin));
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, inicio, fin), new ReporteFechaRequestDTO(2L, 2L, inicio, fin));

        // f2: tipoReporteId
        assertNotEquals(new ReporteFechaRequestDTO(1L, null, inicio, fin), new ReporteFechaRequestDTO(1L, 2L, inicio, fin));
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, inicio, fin), new ReporteFechaRequestDTO(1L, null, inicio, fin));
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, inicio, fin), new ReporteFechaRequestDTO(1L, 3L, inicio, fin));

        // f3: fechaInicio
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, null, fin), new ReporteFechaRequestDTO(1L, 2L, inicio, fin));
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, inicio, fin), new ReporteFechaRequestDTO(1L, 2L, null, fin));
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, inicio, fin), new ReporteFechaRequestDTO(1L, 2L, inicio.plusDays(1), fin));

        // f4: fechaFin
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, inicio, null), new ReporteFechaRequestDTO(1L, 2L, inicio, fin));
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, inicio, fin), new ReporteFechaRequestDTO(1L, 2L, inicio, null));
        assertNotEquals(new ReporteFechaRequestDTO(1L, 2L, inicio, fin), new ReporteFechaRequestDTO(1L, 2L, inicio, fin.plusDays(1)));
    }

    @Test
    void testHashCodeCoverage() {
        ReporteFechaRequestDTO allNull = new ReporteFechaRequestDTO(null, null, null, null);
        allNull.hashCode();
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 30, 23, 59);
        ReporteFechaRequestDTO allNonNull = new ReporteFechaRequestDTO(1L, 2L, inicio, fin);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        LocalDateTime inicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 6, 30, 23, 59);
        ReporteFechaRequestDTO base = new ReporteFechaRequestDTO(1L, 2L, inicio, fin);
        ReporteFechaRequestDTOSub sub = new ReporteFechaRequestDTOSub(1L, 2L, inicio, fin);
        assertNotEquals(base, sub);
    }
}
