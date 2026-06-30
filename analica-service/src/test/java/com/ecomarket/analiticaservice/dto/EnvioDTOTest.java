package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EnvioDTOTest {

    static class EnvioDTOSub extends EnvioDTO {
        public EnvioDTOSub(Long id, Long pedidoId, Long transportistaId, String estado, String metodoEnvio, LocalDateTime fechaCreacion) {
            super(id, pedidoId, transportistaId, estado, metodoEnvio, fechaCreacion);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof EnvioDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        EnvioDTO dto = new EnvioDTO(1L, 10L, 100L, "ENVIADO", "DOMICILIO", now);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getPedidoId());
        assertEquals(100L, dto.getTransportistaId());
        assertEquals("ENVIADO", dto.getEstado());
        assertEquals("DOMICILIO", dto.getMetodoEnvio());
        assertEquals(now, dto.getFechaCreacion());
    }

    @Test
    void noArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        EnvioDTO dto = new EnvioDTO();
        dto.setId(2L);
        dto.setPedidoId(20L);
        dto.setTransportistaId(200L);
        dto.setEstado("PENDIENTE");
        dto.setMetodoEnvio("RECOGIDA");
        dto.setFechaCreacion(now);

        assertEquals(2L, dto.getId());
        assertEquals(20L, dto.getPedidoId());
        assertEquals(200L, dto.getTransportistaId());
        assertEquals("PENDIENTE", dto.getEstado());
        assertEquals("RECOGIDA", dto.getMetodoEnvio());
        assertEquals(now, dto.getFechaCreacion());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        EnvioDTO dto = new EnvioDTO(1L, 10L, 100L, "E", "M", now);
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        EnvioDTO obj = new EnvioDTO(1L, 10L, 100L, "ENVIADO", "DOMICILIO", now);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        EnvioDTO obj = new EnvioDTO(1L, 10L, 100L, "ENVIADO", "DOMICILIO", now);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        EnvioDTO a = new EnvioDTO(null, null, null, null, null, null);
        EnvioDTO b = new EnvioDTO(null, null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        EnvioDTO a = new EnvioDTO(1L, 10L, 100L, "ENVIADO", "DOMICILIO", now);
        EnvioDTO b = new EnvioDTO(1L, 10L, 100L, "ENVIADO", "DOMICILIO", now);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);

        // f1: id
        assertNotEquals(new EnvioDTO(null, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 10L, 100L, "E", "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(null, 10L, 100L, "E", "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(2L, 10L, 100L, "E", "M", now));

        // f2: pedidoId
        assertNotEquals(new EnvioDTO(1L, null, 100L, "E", "M", now), new EnvioDTO(1L, 10L, 100L, "E", "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, null, 100L, "E", "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 20L, 100L, "E", "M", now));

        // f3: transportistaId
        assertNotEquals(new EnvioDTO(1L, 10L, null, "E", "M", now), new EnvioDTO(1L, 10L, 100L, "E", "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 10L, null, "E", "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 10L, 200L, "E", "M", now));

        // f4: estado
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, null, "M", now), new EnvioDTO(1L, 10L, 100L, "E", "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 10L, 100L, null, "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 10L, 100L, "E2", "M", now));

        // f5: metodoEnvio
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", null, now), new EnvioDTO(1L, 10L, 100L, "E", "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 10L, 100L, "E", null, now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 10L, 100L, "E", "M2", now));

        // f6: fechaCreacion
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", null), new EnvioDTO(1L, 10L, 100L, "E", "M", now));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 10L, 100L, "E", "M", null));
        assertNotEquals(new EnvioDTO(1L, 10L, 100L, "E", "M", now), new EnvioDTO(1L, 10L, 100L, "E", "M", now.plusDays(1)));
    }

    @Test
    void testHashCodeCoverage() {
        EnvioDTO allNull = new EnvioDTO(null, null, null, null, null, null);
        allNull.hashCode();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        EnvioDTO allNonNull = new EnvioDTO(1L, 10L, 100L, "ENVIADO", "DOMICILIO", now);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        EnvioDTO base = new EnvioDTO(1L, 10L, 100L, "E", "M", now);
        EnvioDTOSub sub = new EnvioDTOSub(1L, 10L, 100L, "E", "M", now);
        assertNotEquals(base, sub);
    }
}
