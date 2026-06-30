package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TicketSoporteDTOTest {

    static class TicketSoporteDTOSub extends TicketSoporteDTO {
        public TicketSoporteDTOSub(Long id, Long clienteId, String asunto, String categoria, String estado, LocalDateTime fechaCreacion) {
            super(id, clienteId, asunto, categoria, estado, fechaCreacion);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof TicketSoporteDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        TicketSoporteDTO dto = new TicketSoporteDTO(1L, 10L, "Problema con pedido", "RECLAMO", "ABIERTO", now);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getClienteId());
        assertEquals("Problema con pedido", dto.getAsunto());
        assertEquals("RECLAMO", dto.getCategoria());
        assertEquals("ABIERTO", dto.getEstado());
        assertEquals(now, dto.getFechaCreacion());
    }

    @Test
    void noArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        TicketSoporteDTO dto = new TicketSoporteDTO();
        dto.setId(2L);
        dto.setClienteId(20L);
        dto.setAsunto("Devolución");
        dto.setCategoria("GARANTIA");
        dto.setEstado("CERRADO");
        dto.setFechaCreacion(now);

        assertEquals(2L, dto.getId());
        assertEquals(20L, dto.getClienteId());
        assertEquals("Devolución", dto.getAsunto());
        assertEquals("GARANTIA", dto.getCategoria());
        assertEquals("CERRADO", dto.getEstado());
        assertEquals(now, dto.getFechaCreacion());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        TicketSoporteDTO dto = new TicketSoporteDTO(1L, 10L, "a", "c", "e", now);
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        TicketSoporteDTO obj = new TicketSoporteDTO(1L, 10L, "a", "c", "e", now);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        TicketSoporteDTO obj = new TicketSoporteDTO(1L, 10L, "a", "c", "e", now);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        TicketSoporteDTO a = new TicketSoporteDTO(null, null, null, null, null, null);
        TicketSoporteDTO b = new TicketSoporteDTO(null, null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        TicketSoporteDTO a = new TicketSoporteDTO(1L, 10L, "a", "c", "e", now);
        TicketSoporteDTO b = new TicketSoporteDTO(1L, 10L, "a", "c", "e", now);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);

        // f1: id
        assertNotEquals(new TicketSoporteDTO(null, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, "a", "c", "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(null, 10L, "a", "c", "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(2L, 10L, "a", "c", "e", now));

        // f2: clienteId
        assertNotEquals(new TicketSoporteDTO(1L, null, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, "a", "c", "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, null, "a", "c", "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 20L, "a", "c", "e", now));

        // f3: asunto
        assertNotEquals(new TicketSoporteDTO(1L, 10L, null, "c", "e", now), new TicketSoporteDTO(1L, 10L, "a", "c", "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, null, "c", "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, "a2", "c", "e", now));

        // f4: categoria
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", null, "e", now), new TicketSoporteDTO(1L, 10L, "a", "c", "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, "a", null, "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, "a", "c2", "e", now));

        // f5: estado
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", null, now), new TicketSoporteDTO(1L, 10L, "a", "c", "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, "a", "c", null, now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, "a", "c", "e2", now));

        // f6: fechaCreacion
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", null), new TicketSoporteDTO(1L, 10L, "a", "c", "e", now));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, "a", "c", "e", null));
        assertNotEquals(new TicketSoporteDTO(1L, 10L, "a", "c", "e", now), new TicketSoporteDTO(1L, 10L, "a", "c", "e", now.plusDays(1)));
    }

    @Test
    void testHashCodeCoverage() {
        TicketSoporteDTO allNull = new TicketSoporteDTO(null, null, null, null, null, null);
        allNull.hashCode();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        TicketSoporteDTO allNonNull = new TicketSoporteDTO(1L, 10L, "a", "c", "e", now);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        TicketSoporteDTO base = new TicketSoporteDTO(1L, 10L, "a", "c", "e", now);
        TicketSoporteDTOSub sub = new TicketSoporteDTOSub(1L, 10L, "a", "c", "e", now);
        assertNotEquals(base, sub);
    }
}
