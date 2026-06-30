package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InventarioStockDTOTest {

    static class InventarioStockDTOSub extends InventarioStockDTO {
        public InventarioStockDTOSub(Long id, Long productoId, Integer cantidadDisponible, String sucursal) {
            super(id, productoId, cantidadDisponible, sucursal);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof InventarioStockDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        InventarioStockDTO dto = new InventarioStockDTO(1L, 100L, 50, "Sucursal Central");

        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getProductoId());
        assertEquals(50, dto.getCantidadDisponible());
        assertEquals("Sucursal Central", dto.getSucursal());
    }

    @Test
    void noArgsConstructorAndSetters() {
        InventarioStockDTO dto = new InventarioStockDTO();
        dto.setId(2L);
        dto.setProductoId(200L);
        dto.setCantidadDisponible(100);
        dto.setSucursal("Sucursal Norte");

        assertEquals(2L, dto.getId());
        assertEquals(200L, dto.getProductoId());
        assertEquals(100, dto.getCantidadDisponible());
        assertEquals("Sucursal Norte", dto.getSucursal());
    }

    @Test
    void testToString() {
        InventarioStockDTO dto = new InventarioStockDTO(1L, 100L, 50, "S");
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        InventarioStockDTO obj = new InventarioStockDTO(1L, 100L, 50, "Central");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        InventarioStockDTO obj = new InventarioStockDTO(1L, 100L, 50, "Central");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        InventarioStockDTO a = new InventarioStockDTO(null, null, null, null);
        InventarioStockDTO b = new InventarioStockDTO(null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        InventarioStockDTO a = new InventarioStockDTO(1L, 100L, 50, "Central");
        InventarioStockDTO b = new InventarioStockDTO(1L, 100L, 50, "Central");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        // f1: id
        assertNotEquals(new InventarioStockDTO(null, 100L, 50, "S"), new InventarioStockDTO(1L, 100L, 50, "S"));
        assertNotEquals(new InventarioStockDTO(1L, 100L, 50, "S"), new InventarioStockDTO(null, 100L, 50, "S"));
        assertNotEquals(new InventarioStockDTO(1L, 100L, 50, "S"), new InventarioStockDTO(2L, 100L, 50, "S"));

        // f2: productoId
        assertNotEquals(new InventarioStockDTO(1L, null, 50, "S"), new InventarioStockDTO(1L, 100L, 50, "S"));
        assertNotEquals(new InventarioStockDTO(1L, 100L, 50, "S"), new InventarioStockDTO(1L, null, 50, "S"));
        assertNotEquals(new InventarioStockDTO(1L, 100L, 50, "S"), new InventarioStockDTO(1L, 200L, 50, "S"));

        // f3: cantidadDisponible
        assertNotEquals(new InventarioStockDTO(1L, 100L, null, "S"), new InventarioStockDTO(1L, 100L, 50, "S"));
        assertNotEquals(new InventarioStockDTO(1L, 100L, 50, "S"), new InventarioStockDTO(1L, 100L, null, "S"));
        assertNotEquals(new InventarioStockDTO(1L, 100L, 50, "S"), new InventarioStockDTO(1L, 100L, 60, "S"));

        // f4: sucursal
        assertNotEquals(new InventarioStockDTO(1L, 100L, 50, null), new InventarioStockDTO(1L, 100L, 50, "S"));
        assertNotEquals(new InventarioStockDTO(1L, 100L, 50, "S"), new InventarioStockDTO(1L, 100L, 50, null));
        assertNotEquals(new InventarioStockDTO(1L, 100L, 50, "S"), new InventarioStockDTO(1L, 100L, 50, "S2"));
    }

    @Test
    void testHashCodeCoverage() {
        InventarioStockDTO allNull = new InventarioStockDTO(null, null, null, null);
        allNull.hashCode();
        InventarioStockDTO allNonNull = new InventarioStockDTO(1L, 100L, 50, "Central");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        InventarioStockDTO base = new InventarioStockDTO(1L, 100L, 50, "S");
        InventarioStockDTOSub sub = new InventarioStockDTOSub(1L, 100L, 50, "S");
        assertNotEquals(base, sub);
    }
}
