package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class AccionLogDTOTest {

    static class AccionLogDTOSub extends AccionLogDTO {
        public AccionLogDTOSub(String microservicio, String accion, Long usuarioId, String detalles, LocalDateTime fecha) {
            super(microservicio, accion, usuarioId, detalles, fecha);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof AccionLogDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AccionLogDTO dto = new AccionLogDTO("ventas", "CREAR", 1L, "detalle", now);

        assertEquals("ventas", dto.getMicroservicio());
        assertEquals("CREAR", dto.getAccion());
        assertEquals(1L, dto.getUsuarioId());
        assertEquals("detalle", dto.getDetalles());
        assertEquals(now, dto.getFecha());
    }

    @Test
    void noArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AccionLogDTO dto = new AccionLogDTO();
        dto.setMicroservicio("inv");
        dto.setAccion("ACTUALIZAR");
        dto.setUsuarioId(2L);
        dto.setDetalles("otro detalle");
        dto.setFecha(now);

        assertEquals("inv", dto.getMicroservicio());
        assertEquals("ACTUALIZAR", dto.getAccion());
        assertEquals(2L, dto.getUsuarioId());
        assertEquals("otro detalle", dto.getDetalles());
        assertEquals(now, dto.getFecha());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AccionLogDTO dto = new AccionLogDTO("ventas", "CREAR", 1L, "detalle", now);
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AccionLogDTO obj = new AccionLogDTO("ventas", "CREAR", 1L, "detalle", now);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AccionLogDTO obj = new AccionLogDTO("ventas", "CREAR", 1L, "detalle", now);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        AccionLogDTO a = new AccionLogDTO(null, null, null, null, null);
        AccionLogDTO b = new AccionLogDTO(null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AccionLogDTO a = new AccionLogDTO("ventas", "CREAR", 1L, "detalle", now);
        AccionLogDTO b = new AccionLogDTO("ventas", "CREAR", 1L, "detalle", now);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);

        // f1: microservicio
        assertNotEquals(new AccionLogDTO(null, "C", 1L, "d", now), new AccionLogDTO("v1", "C", 1L, "d", now));
        assertNotEquals(new AccionLogDTO("v1", "C", 1L, "d", now), new AccionLogDTO(null, "C", 1L, "d", now));
        assertNotEquals(new AccionLogDTO("v1", "C", 1L, "d", now), new AccionLogDTO("v2", "C", 1L, "d", now));

        // f2: accion
        assertNotEquals(new AccionLogDTO("v1", null, 1L, "d", now), new AccionLogDTO("v1", "C1", 1L, "d", now));
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, "d", now), new AccionLogDTO("v1", null, 1L, "d", now));
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, "d", now), new AccionLogDTO("v1", "C2", 1L, "d", now));

        // f3: usuarioId
        assertNotEquals(new AccionLogDTO("v1", "C1", null, "d", now), new AccionLogDTO("v1", "C1", 1L, "d", now));
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, "d", now), new AccionLogDTO("v1", "C1", null, "d", now));
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, "d", now), new AccionLogDTO("v1", "C1", 2L, "d", now));

        // f4: detalles
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, null, now), new AccionLogDTO("v1", "C1", 1L, "d1", now));
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, "d1", now), new AccionLogDTO("v1", "C1", 1L, null, now));
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, "d1", now), new AccionLogDTO("v1", "C1", 1L, "d2", now));

        // f5: fecha
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, "d1", null), new AccionLogDTO("v1", "C1", 1L, "d1", now));
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, "d1", now), new AccionLogDTO("v1", "C1", 1L, "d1", null));
        assertNotEquals(new AccionLogDTO("v1", "C1", 1L, "d1", now), new AccionLogDTO("v1", "C1", 1L, "d1", now.plusDays(1)));
    }

    @Test
    void testHashCodeCoverage() {
        AccionLogDTO allNull = new AccionLogDTO(null, null, null, null, null);
        allNull.hashCode();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AccionLogDTO allNonNull = new AccionLogDTO("ventas", "CREAR", 1L, "detalle", now);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AccionLogDTO base = new AccionLogDTO("v", "C", 1L, "d", now);
        AccionLogDTOSub sub = new AccionLogDTOSub("v", "C", 1L, "d", now);
        assertNotEquals(base, sub);
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        AccionLogDTO.AccionLogDTOBuilder builder = AccionLogDTO.builder()
                .microservicio("ms")
                .accion("ACCION")
                .usuarioId(5L)
                .detalles("det")
                .fecha(now);
        assertNotNull(builder.toString());
        AccionLogDTO dto = builder.build();
        assertEquals("ms", dto.getMicroservicio());
        assertEquals("ACCION", dto.getAccion());
        assertEquals(5L, dto.getUsuarioId());
        assertEquals("det", dto.getDetalles());
        assertEquals(now, dto.getFecha());
    }
}
