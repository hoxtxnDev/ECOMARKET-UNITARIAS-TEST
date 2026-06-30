package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClienteDTOTest {

    static class ClienteDTOSub extends ClienteDTO {
        public ClienteDTOSub(Long id, String nombre, String email, String telefono) {
            super(id, nombre, email, telefono);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof ClienteDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        ClienteDTO dto = new ClienteDTO(1L, "Juan", "juan@email.com", "555-0100");

        assertEquals(1L, dto.getId());
        assertEquals("Juan", dto.getNombre());
        assertEquals("juan@email.com", dto.getEmail());
        assertEquals("555-0100", dto.getTelefono());
    }

    @Test
    void noArgsConstructorAndSetters() {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(2L);
        dto.setNombre("Ana");
        dto.setEmail("ana@email.com");
        dto.setTelefono("555-0200");

        assertEquals(2L, dto.getId());
        assertEquals("Ana", dto.getNombre());
        assertEquals("ana@email.com", dto.getEmail());
        assertEquals("555-0200", dto.getTelefono());
    }

    @Test
    void testToString() {
        ClienteDTO dto = new ClienteDTO(1L, "Juan", "juan@email.com", "555-0100");
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        ClienteDTO obj = new ClienteDTO(1L, "Juan", "juan@email.com", "555-0100");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        ClienteDTO obj = new ClienteDTO(1L, "Juan", "juan@email.com", "555-0100");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        ClienteDTO a = new ClienteDTO(null, null, null, null);
        ClienteDTO b = new ClienteDTO(null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        ClienteDTO a = new ClienteDTO(1L, "Juan", "juan@email.com", "555-0100");
        ClienteDTO b = new ClienteDTO(1L, "Juan", "juan@email.com", "555-0100");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        // f1: id
        assertNotEquals(new ClienteDTO(null, "N", "e", "t"), new ClienteDTO(1L, "N", "e", "t"));
        assertNotEquals(new ClienteDTO(1L, "N", "e", "t"), new ClienteDTO(null, "N", "e", "t"));
        assertNotEquals(new ClienteDTO(1L, "N", "e", "t"), new ClienteDTO(2L, "N", "e", "t"));

        // f2: nombre
        assertNotEquals(new ClienteDTO(1L, null, "e", "t"), new ClienteDTO(1L, "N", "e", "t"));
        assertNotEquals(new ClienteDTO(1L, "N", "e", "t"), new ClienteDTO(1L, null, "e", "t"));
        assertNotEquals(new ClienteDTO(1L, "N", "e", "t"), new ClienteDTO(1L, "N2", "e", "t"));

        // f3: email
        assertNotEquals(new ClienteDTO(1L, "N", null, "t"), new ClienteDTO(1L, "N", "e", "t"));
        assertNotEquals(new ClienteDTO(1L, "N", "e", "t"), new ClienteDTO(1L, "N", null, "t"));
        assertNotEquals(new ClienteDTO(1L, "N", "e", "t"), new ClienteDTO(1L, "N", "e2", "t"));

        // f4: telefono
        assertNotEquals(new ClienteDTO(1L, "N", "e", null), new ClienteDTO(1L, "N", "e", "t"));
        assertNotEquals(new ClienteDTO(1L, "N", "e", "t"), new ClienteDTO(1L, "N", "e", null));
        assertNotEquals(new ClienteDTO(1L, "N", "e", "t"), new ClienteDTO(1L, "N", "e", "t2"));
    }

    @Test
    void testHashCodeCoverage() {
        ClienteDTO allNull = new ClienteDTO(null, null, null, null);
        allNull.hashCode();
        ClienteDTO allNonNull = new ClienteDTO(1L, "Juan", "juan@email.com", "555-0100");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        ClienteDTO base = new ClienteDTO(1L, "N", "e", "t");
        ClienteDTOSub sub = new ClienteDTOSub(1L, "N", "e", "t");
        assertNotEquals(base, sub);
    }
}
