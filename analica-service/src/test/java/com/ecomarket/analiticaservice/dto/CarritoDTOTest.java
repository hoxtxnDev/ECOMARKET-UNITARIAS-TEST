package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CarritoDTOTest {

    static class CarritoDTOSub extends CarritoDTO {
        public CarritoDTOSub(Long id, Long clienteId, Long productoId, Integer cantidad, Double precioUnitario) {
            super(id, clienteId, productoId, cantidad, precioUnitario);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof CarritoDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        CarritoDTO dto = new CarritoDTO(1L, 10L, 100L, 3, 25.50);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getClienteId());
        assertEquals(100L, dto.getProductoId());
        assertEquals(3, dto.getCantidad());
        assertEquals(25.50, dto.getPrecioUnitario());
    }

    @Test
    void noArgsConstructorAndSetters() {
        CarritoDTO dto = new CarritoDTO();
        dto.setId(2L);
        dto.setClienteId(20L);
        dto.setProductoId(200L);
        dto.setCantidad(5);
        dto.setPrecioUnitario(99.99);

        assertEquals(2L, dto.getId());
        assertEquals(20L, dto.getClienteId());
        assertEquals(200L, dto.getProductoId());
        assertEquals(5, dto.getCantidad());
        assertEquals(99.99, dto.getPrecioUnitario());
    }

    @Test
    void testToString() {
        CarritoDTO dto = new CarritoDTO(1L, 10L, 100L, 3, 25.5);
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        CarritoDTO obj = new CarritoDTO(1L, 10L, 100L, 3, 25.50);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        CarritoDTO obj = new CarritoDTO(1L, 10L, 100L, 3, 25.50);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        CarritoDTO a = new CarritoDTO(null, null, null, null, null);
        CarritoDTO b = new CarritoDTO(null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        CarritoDTO a = new CarritoDTO(1L, 10L, 100L, 3, 25.50);
        CarritoDTO b = new CarritoDTO(1L, 10L, 100L, 3, 25.50);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        // f1: id
        assertNotEquals(new CarritoDTO(null, 10L, 100L, 3, 25.5), new CarritoDTO(1L, 10L, 100L, 3, 25.5));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(null, 10L, 100L, 3, 25.5));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(2L, 10L, 100L, 3, 25.5));

        // f2: clienteId
        assertNotEquals(new CarritoDTO(1L, null, 100L, 3, 25.5), new CarritoDTO(1L, 10L, 100L, 3, 25.5));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(1L, null, 100L, 3, 25.5));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(1L, 20L, 100L, 3, 25.5));

        // f3: productoId
        assertNotEquals(new CarritoDTO(1L, 10L, null, 3, 25.5), new CarritoDTO(1L, 10L, 100L, 3, 25.5));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(1L, 10L, null, 3, 25.5));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(1L, 10L, 200L, 3, 25.5));

        // f4: cantidad
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, null, 25.5), new CarritoDTO(1L, 10L, 100L, 3, 25.5));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(1L, 10L, 100L, null, 25.5));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(1L, 10L, 100L, 4, 25.5));

        // f5: precioUnitario
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, null), new CarritoDTO(1L, 10L, 100L, 3, 25.5));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(1L, 10L, 100L, 3, null));
        assertNotEquals(new CarritoDTO(1L, 10L, 100L, 3, 25.5), new CarritoDTO(1L, 10L, 100L, 3, 99.9));
    }

    @Test
    void testHashCodeCoverage() {
        CarritoDTO allNull = new CarritoDTO(null, null, null, null, null);
        allNull.hashCode();
        CarritoDTO allNonNull = new CarritoDTO(1L, 10L, 100L, 3, 25.50);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        CarritoDTO base = new CarritoDTO(1L, 10L, 100L, 3, 25.5);
        CarritoDTOSub sub = new CarritoDTOSub(1L, 10L, 100L, 3, 25.5);
        assertNotEquals(base, sub);
    }
}
