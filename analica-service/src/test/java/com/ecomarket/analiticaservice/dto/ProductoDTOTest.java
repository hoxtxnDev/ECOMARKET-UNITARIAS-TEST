package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProductoDTOTest {

    static class ProductoDTOSub extends ProductoDTO {
        public ProductoDTOSub(Long id, String nombre, Double precio, String categoria) {
            super(id, nombre, precio, categoria);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof ProductoDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        ProductoDTO dto = new ProductoDTO(1L, "Laptop", 1500.0, "Electrónica");

        assertEquals(1L, dto.getId());
        assertEquals("Laptop", dto.getNombre());
        assertEquals(1500.0, dto.getPrecio());
        assertEquals("Electrónica", dto.getCategoria());
    }

    @Test
    void noArgsConstructorAndSetters() {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(2L);
        dto.setNombre("Mouse");
        dto.setPrecio(25.0);
        dto.setCategoria("Periféricos");

        assertEquals(2L, dto.getId());
        assertEquals("Mouse", dto.getNombre());
        assertEquals(25.0, dto.getPrecio());
        assertEquals("Periféricos", dto.getCategoria());
    }

    @Test
    void testToString() {
        ProductoDTO dto = new ProductoDTO(1L, "Laptop", 1500.0, "Electrónica");
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        ProductoDTO obj = new ProductoDTO(1L, "Laptop", 1500.0, "Electrónica");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        ProductoDTO obj = new ProductoDTO(1L, "Laptop", 1500.0, "Electrónica");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        ProductoDTO a = new ProductoDTO(null, null, null, null);
        ProductoDTO b = new ProductoDTO(null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        ProductoDTO a = new ProductoDTO(1L, "Laptop", 1500.0, "Electrónica");
        ProductoDTO b = new ProductoDTO(1L, "Laptop", 1500.0, "Electrónica");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        // f1: id
        assertNotEquals(new ProductoDTO(null, "N", 1.0, "C"), new ProductoDTO(1L, "N", 1.0, "C"));
        assertNotEquals(new ProductoDTO(1L, "N", 1.0, "C"), new ProductoDTO(null, "N", 1.0, "C"));
        assertNotEquals(new ProductoDTO(1L, "N", 1.0, "C"), new ProductoDTO(2L, "N", 1.0, "C"));

        // f2: nombre
        assertNotEquals(new ProductoDTO(1L, null, 1.0, "C"), new ProductoDTO(1L, "N", 1.0, "C"));
        assertNotEquals(new ProductoDTO(1L, "N", 1.0, "C"), new ProductoDTO(1L, null, 1.0, "C"));
        assertNotEquals(new ProductoDTO(1L, "N", 1.0, "C"), new ProductoDTO(1L, "N2", 1.0, "C"));

        // f3: precio
        assertNotEquals(new ProductoDTO(1L, "N", null, "C"), new ProductoDTO(1L, "N", 1.0, "C"));
        assertNotEquals(new ProductoDTO(1L, "N", 1.0, "C"), new ProductoDTO(1L, "N", null, "C"));
        assertNotEquals(new ProductoDTO(1L, "N", 1.0, "C"), new ProductoDTO(1L, "N", 2.0, "C"));

        // f4: categoria
        assertNotEquals(new ProductoDTO(1L, "N", 1.0, null), new ProductoDTO(1L, "N", 1.0, "C"));
        assertNotEquals(new ProductoDTO(1L, "N", 1.0, "C"), new ProductoDTO(1L, "N", 1.0, null));
        assertNotEquals(new ProductoDTO(1L, "N", 1.0, "C"), new ProductoDTO(1L, "N", 1.0, "C2"));
    }

    @Test
    void testHashCodeCoverage() {
        ProductoDTO allNull = new ProductoDTO(null, null, null, null);
        allNull.hashCode();
        ProductoDTO allNonNull = new ProductoDTO(1L, "Laptop", 1500.0, "Electrónica");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        ProductoDTO base = new ProductoDTO(1L, "N", 1.0, "C");
        ProductoDTOSub sub = new ProductoDTOSub(1L, "N", 1.0, "C");
        assertNotEquals(base, sub);
    }
}
