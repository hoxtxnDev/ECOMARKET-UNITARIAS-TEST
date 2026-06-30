package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class PagoDTOTest {

    static class PagoDTOSub extends PagoDTO {
        public PagoDTOSub(Long id, Long pedidoId, Double monto, String metodoPago, String estado, LocalDateTime fechaPago) {
            super(id, pedidoId, monto, metodoPago, estado, fechaPago);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof PagoDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PagoDTO dto = new PagoDTO(1L, 10L, 500.0, "TARJETA", "COMPLETADO", now);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getPedidoId());
        assertEquals(500.0, dto.getMonto());
        assertEquals("TARJETA", dto.getMetodoPago());
        assertEquals("COMPLETADO", dto.getEstado());
        assertEquals(now, dto.getFechaPago());
    }

    @Test
    void noArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PagoDTO dto = new PagoDTO();
        dto.setId(2L);
        dto.setPedidoId(20L);
        dto.setMonto(250.0);
        dto.setMetodoPago("EFECTIVO");
        dto.setEstado("PENDIENTE");
        dto.setFechaPago(now);

        assertEquals(2L, dto.getId());
        assertEquals(20L, dto.getPedidoId());
        assertEquals(250.0, dto.getMonto());
        assertEquals("EFECTIVO", dto.getMetodoPago());
        assertEquals("PENDIENTE", dto.getEstado());
        assertEquals(now, dto.getFechaPago());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PagoDTO dto = new PagoDTO(1L, 10L, 500.0, "TARJETA", "COMPLETADO", now);
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PagoDTO obj = new PagoDTO(1L, 10L, 500.0, "TARJETA", "COMPLETADO", now);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PagoDTO obj = new PagoDTO(1L, 10L, 500.0, "TARJETA", "COMPLETADO", now);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        PagoDTO a = new PagoDTO(null, null, null, null, null, null);
        PagoDTO b = new PagoDTO(null, null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PagoDTO a = new PagoDTO(1L, 10L, 500.0, "TARJETA", "COMPLETADO", now);
        PagoDTO b = new PagoDTO(1L, 10L, 500.0, "TARJETA", "COMPLETADO", now);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);

        // f1: id
        assertNotEquals(new PagoDTO(null, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 10L, 500.0, "M", "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(null, 10L, 500.0, "M", "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(2L, 10L, 500.0, "M", "E", now));

        // f2: pedidoId
        assertNotEquals(new PagoDTO(1L, null, 500.0, "M", "E", now), new PagoDTO(1L, 10L, 500.0, "M", "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, null, 500.0, "M", "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 20L, 500.0, "M", "E", now));

        // f3: monto
        assertNotEquals(new PagoDTO(1L, 10L, null, "M", "E", now), new PagoDTO(1L, 10L, 500.0, "M", "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 10L, null, "M", "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 10L, 600.0, "M", "E", now));

        // f4: metodoPago
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, null, "E", now), new PagoDTO(1L, 10L, 500.0, "M", "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 10L, 500.0, null, "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 10L, 500.0, "M2", "E", now));

        // f5: estado
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", null, now), new PagoDTO(1L, 10L, 500.0, "M", "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 10L, 500.0, "M", null, now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 10L, 500.0, "M", "E2", now));

        // f6: fechaPago
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", null), new PagoDTO(1L, 10L, 500.0, "M", "E", now));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 10L, 500.0, "M", "E", null));
        assertNotEquals(new PagoDTO(1L, 10L, 500.0, "M", "E", now), new PagoDTO(1L, 10L, 500.0, "M", "E", now.plusDays(1)));
    }

    @Test
    void testHashCodeCoverage() {
        PagoDTO allNull = new PagoDTO(null, null, null, null, null, null);
        allNull.hashCode();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PagoDTO allNonNull = new PagoDTO(1L, 10L, 500.0, "TARJETA", "COMPLETADO", now);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PagoDTO base = new PagoDTO(1L, 10L, 500.0, "M", "E", now);
        PagoDTOSub sub = new PagoDTOSub(1L, 10L, 500.0, "M", "E", now);
        assertNotEquals(base, sub);
    }
}
