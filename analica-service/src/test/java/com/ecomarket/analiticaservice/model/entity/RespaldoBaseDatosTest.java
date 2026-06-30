package com.ecomarket.analiticaservice.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.ecomarket.analiticaservice.model.reference.EstadoRespaldo;

class RespaldoBaseDatosTest {

    static class RespaldoBaseDatosSub extends RespaldoBaseDatos {
        public RespaldoBaseDatosSub(Long id, LocalDateTime fechaRespaldo, Double tamanoMegabytes, EstadoRespaldo estado, String rutaAlmacenamiento) {
            super(id, fechaRespaldo, tamanoMegabytes, estado, rutaAlmacenamiento);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof RespaldoBaseDatosSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        EstadoRespaldo estado = new EstadoRespaldo(1L, "EXITOSO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        RespaldoBaseDatos r = new RespaldoBaseDatos(1L, now, 150.5, estado, "/backups/db.sql");
        assertEquals(1L, r.getId());
        assertEquals(now, r.getFechaRespaldo());
        assertEquals(150.5, r.getTamanoMegabytes());
        assertEquals(estado, r.getEstado());
        assertEquals("/backups/db.sql", r.getRutaAlmacenamiento());
    }

    @Test
    void noArgsConstructorAndSetters() {
        EstadoRespaldo estado = new EstadoRespaldo(2L, "FALLIDO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        RespaldoBaseDatos r = new RespaldoBaseDatos();
        r.setId(2L);
        r.setFechaRespaldo(now);
        r.setTamanoMegabytes(200.0);
        r.setEstado(estado);
        r.setRutaAlmacenamiento("/backups/fail.sql");

        assertEquals(2L, r.getId());
        assertEquals(now, r.getFechaRespaldo());
        assertEquals(200.0, r.getTamanoMegabytes());
        assertEquals(estado, r.getEstado());
        assertEquals("/backups/fail.sql", r.getRutaAlmacenamiento());
    }

    @Test
    void testToString() {
        EstadoRespaldo estado = new EstadoRespaldo(1L, "EXITOSO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        RespaldoBaseDatos r = new RespaldoBaseDatos(1L, now, 150.5, estado, "/backups/db.sql");
        assertNotNull(r.toString());
    }

    @Test
    void testIdentity() {
        EstadoRespaldo estado = new EstadoRespaldo(1L, "EXITOSO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        RespaldoBaseDatos obj = new RespaldoBaseDatos(1L, now, 150.5, estado, "/backups/db.sql");
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        EstadoRespaldo estado = new EstadoRespaldo(1L, "EXITOSO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        RespaldoBaseDatos obj = new RespaldoBaseDatos(1L, now, 150.5, estado, "/backups/db.sql");
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        RespaldoBaseDatos a = new RespaldoBaseDatos(null, null, null, null, null);
        RespaldoBaseDatos b = new RespaldoBaseDatos(null, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        EstadoRespaldo estado = new EstadoRespaldo(1L, "EXITOSO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        RespaldoBaseDatos a = new RespaldoBaseDatos(1L, now, 150.5, estado, "/backups/db.sql");
        RespaldoBaseDatos b = new RespaldoBaseDatos(1L, now, 150.5, estado, "/backups/db.sql");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        EstadoRespaldo estado = new EstadoRespaldo(1L, "EXITOSO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        String path = "/backups/db.sql";

        assertNotEquals(new RespaldoBaseDatos(null, now, 150.5, estado, path), new RespaldoBaseDatos(1L, now, 150.5, estado, path));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(null, now, 150.5, estado, path));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(2L, now, 150.5, estado, path));

        assertNotEquals(new RespaldoBaseDatos(1L, null, 150.5, estado, path), new RespaldoBaseDatos(1L, now, 150.5, estado, path));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(1L, null, 150.5, estado, path));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(1L, now.plusDays(1), 150.5, estado, path));

        assertNotEquals(new RespaldoBaseDatos(1L, now, null, estado, path), new RespaldoBaseDatos(1L, now, 150.5, estado, path));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(1L, now, null, estado, path));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(1L, now, 200.0, estado, path));

        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, null, path), new RespaldoBaseDatos(1L, now, 150.5, estado, path));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(1L, now, 150.5, null, path));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(1L, now, 150.5, new EstadoRespaldo(2L, "FAIL"), path));

        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, null), new RespaldoBaseDatos(1L, now, 150.5, estado, path));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(1L, now, 150.5, estado, null));
        assertNotEquals(new RespaldoBaseDatos(1L, now, 150.5, estado, path), new RespaldoBaseDatos(1L, now, 150.5, estado, "/other/path"));
    }

    @Test
    void testHashCodeCoverage() {
        RespaldoBaseDatos allNull = new RespaldoBaseDatos(null, null, null, null, null);
        allNull.hashCode();
        EstadoRespaldo estado = new EstadoRespaldo(1L, "EXITOSO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        RespaldoBaseDatos allNonNull = new RespaldoBaseDatos(1L, now, 150.5, estado, "/backups/db.sql");
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        EstadoRespaldo estado = new EstadoRespaldo(1L, "EXITOSO");
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        RespaldoBaseDatos base = new RespaldoBaseDatos(1L, now, 150.5, estado, "/backups/db.sql");
        RespaldoBaseDatosSub sub = new RespaldoBaseDatosSub(1L, now, 150.5, estado, "/backups/db.sql");
        assertNotEquals(base, sub);
    }
}
