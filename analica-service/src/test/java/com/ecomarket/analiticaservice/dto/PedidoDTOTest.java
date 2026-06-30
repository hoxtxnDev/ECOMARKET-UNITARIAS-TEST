package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class PedidoDTOTest {

    static class PedidoDTOSub extends PedidoDTO {
        public PedidoDTOSub(Long id, Long clienteId, Double total, LocalDateTime fechaPedido, String estado) {
            super(id, clienteId, total, fechaPedido, estado);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof PedidoDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PedidoDTO dto = new PedidoDTO(1L, 10L, 250.0, now, "PENDIENTE");

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getClienteId());
        assertEquals(250.0, dto.getTotal());
        assertEquals(now, dto.getFechaPedido());
        assertEquals("PENDIENTE", dto.getEstado());
    }

    @Test
    void noArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PedidoDTO dto = new PedidoDTO();
        dto.setId(2L);
        dto.setClienteId(20L);
        dto.setTotal(500.0);
        dto.setFechaPedido(now);
        dto.setEstado("COMPLETADO");

        assertEquals(2L, dto.getId());
        assertEquals(20L, dto.getClienteId());
        assertEquals(500.0, dto.getTotal());
        assertEquals(now, dto.getFechaPedido());
        assertEquals("COMPLETADO", dto.getEstado());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PedidoDTO dto = new PedidoDTO(1L, 10L, 250.0, now, "PENDIENTE");
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PedidoDTO obj = new PedidoDTO(1L, 10L, 250.0, now, "PENDIENTE");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PedidoDTO obj = new PedidoDTO(1L, 10L, 250.0, now, "PENDIENTE");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        PedidoDTO a = new PedidoDTO(null, null, null, null, null);
        PedidoDTO b = new PedidoDTO(null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PedidoDTO a = new PedidoDTO(1L, 10L, 250.0, now, "PENDIENTE");
        PedidoDTO b = new PedidoDTO(1L, 10L, 250.0, now, "PENDIENTE");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);

        // f1: id
        assertNotEquals(new PedidoDTO(null, 10L, 250.0, now, "P"), new PedidoDTO(1L, 10L, 250.0, now, "P"));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(null, 10L, 250.0, now, "P"));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(2L, 10L, 250.0, now, "P"));

        // f2: clienteId
        assertNotEquals(new PedidoDTO(1L, null, 250.0, now, "P"), new PedidoDTO(1L, 10L, 250.0, now, "P"));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(1L, null, 250.0, now, "P"));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(1L, 20L, 250.0, now, "P"));

        // f3: total
        assertNotEquals(new PedidoDTO(1L, 10L, null, now, "P"), new PedidoDTO(1L, 10L, 250.0, now, "P"));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(1L, 10L, null, now, "P"));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(1L, 10L, 500.0, now, "P"));

        // f4: fechaPedido
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, null, "P"), new PedidoDTO(1L, 10L, 250.0, now, "P"));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(1L, 10L, 250.0, null, "P"));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(1L, 10L, 250.0, now.plusDays(1), "P"));

        // f5: estado
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, null), new PedidoDTO(1L, 10L, 250.0, now, "P"));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(1L, 10L, 250.0, now, null));
        assertNotEquals(new PedidoDTO(1L, 10L, 250.0, now, "P"), new PedidoDTO(1L, 10L, 250.0, now, "C"));
    }

    @Test
    void testHashCodeCoverage() {
        PedidoDTO allNull = new PedidoDTO(null, null, null, null, null);
        allNull.hashCode();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PedidoDTO allNonNull = new PedidoDTO(1L, 10L, 250.0, now, "PENDIENTE");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        PedidoDTO base = new PedidoDTO(1L, 10L, 250.0, now, "P");
        PedidoDTOSub sub = new PedidoDTOSub(1L, 10L, 250.0, now, "P");
        assertNotEquals(base, sub);
    }
}
