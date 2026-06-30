package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RespaldoRequestDTOTest {

    static class RespaldoRequestDTOSub extends RespaldoRequestDTO {
        public RespaldoRequestDTOSub(Long estadoRespaldoId, Double tamanoMegabytes, String rutaAlmacenamiento) {
            super(estadoRespaldoId, tamanoMegabytes, rutaAlmacenamiento);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof RespaldoRequestDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        RespaldoRequestDTO dto = new RespaldoRequestDTO(1L, 256.5, "/backups/db.sql");

        assertEquals(1L, dto.getEstadoRespaldoId());
        assertEquals(256.5, dto.getTamanoMegabytes());
        assertEquals("/backups/db.sql", dto.getRutaAlmacenamiento());
    }

    @Test
    void noArgsConstructorAndSetters() {
        RespaldoRequestDTO dto = new RespaldoRequestDTO();
        dto.setEstadoRespaldoId(2L);
        dto.setTamanoMegabytes(512.0);
        dto.setRutaAlmacenamiento("/backups/backup2.sql");

        assertEquals(2L, dto.getEstadoRespaldoId());
        assertEquals(512.0, dto.getTamanoMegabytes());
        assertEquals("/backups/backup2.sql", dto.getRutaAlmacenamiento());
    }

    @Test
    void testToString() {
        RespaldoRequestDTO dto = new RespaldoRequestDTO(1L, 256.5, "/p");
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        RespaldoRequestDTO obj = new RespaldoRequestDTO(1L, 256.5, "/p");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        RespaldoRequestDTO obj = new RespaldoRequestDTO(1L, 256.5, "/p");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        RespaldoRequestDTO a = new RespaldoRequestDTO(null, null, null);
        RespaldoRequestDTO b = new RespaldoRequestDTO(null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        RespaldoRequestDTO a = new RespaldoRequestDTO(1L, 256.5, "/p");
        RespaldoRequestDTO b = new RespaldoRequestDTO(1L, 256.5, "/p");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        // f1: estadoRespaldoId
        assertNotEquals(new RespaldoRequestDTO(null, 256.5, "/p"), new RespaldoRequestDTO(1L, 256.5, "/p"));
        assertNotEquals(new RespaldoRequestDTO(1L, 256.5, "/p"), new RespaldoRequestDTO(null, 256.5, "/p"));
        assertNotEquals(new RespaldoRequestDTO(1L, 256.5, "/p"), new RespaldoRequestDTO(2L, 256.5, "/p"));

        // f2: tamanoMegabytes
        assertNotEquals(new RespaldoRequestDTO(1L, null, "/p"), new RespaldoRequestDTO(1L, 256.5, "/p"));
        assertNotEquals(new RespaldoRequestDTO(1L, 256.5, "/p"), new RespaldoRequestDTO(1L, null, "/p"));
        assertNotEquals(new RespaldoRequestDTO(1L, 256.5, "/p"), new RespaldoRequestDTO(1L, 512.0, "/p"));

        // f3: rutaAlmacenamiento
        assertNotEquals(new RespaldoRequestDTO(1L, 256.5, null), new RespaldoRequestDTO(1L, 256.5, "/p"));
        assertNotEquals(new RespaldoRequestDTO(1L, 256.5, "/p"), new RespaldoRequestDTO(1L, 256.5, null));
        assertNotEquals(new RespaldoRequestDTO(1L, 256.5, "/p"), new RespaldoRequestDTO(1L, 256.5, "/p2"));
    }

    @Test
    void testHashCodeCoverage() {
        RespaldoRequestDTO allNull = new RespaldoRequestDTO(null, null, null);
        allNull.hashCode();
        RespaldoRequestDTO allNonNull = new RespaldoRequestDTO(1L, 256.5, "/p");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        RespaldoRequestDTO base = new RespaldoRequestDTO(1L, 256.5, "/p");
        RespaldoRequestDTOSub sub = new RespaldoRequestDTOSub(1L, 256.5, "/p");
        assertNotEquals(base, sub);
    }
}
