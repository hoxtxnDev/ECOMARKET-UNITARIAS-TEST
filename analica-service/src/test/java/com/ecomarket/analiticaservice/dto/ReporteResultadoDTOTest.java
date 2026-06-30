package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ReporteResultadoDTOTest {

    static class ReporteResultadoDTOSub extends ReporteResultadoDTO {
        public ReporteResultadoDTOSub(String tipoReporte, Long totalRegistros, List<Map<String, Object>> datos, List<String> serviciosNoDisponibles) {
            super(tipoReporte, totalRegistros, datos, serviciosNoDisponibles);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof ReporteResultadoDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        List<Map<String, Object>> datos = List.of(Map.of("id", 1));
        List<String> noDisponibles = List.of("servicio1");
        ReporteResultadoDTO dto = new ReporteResultadoDTO("USUARIOS", 5L, datos, noDisponibles);

        assertEquals("USUARIOS", dto.getTipoReporte());
        assertEquals(5L, dto.getTotalRegistros());
        assertEquals(1, dto.getDatos().size());
        assertEquals(1, dto.getServiciosNoDisponibles().size());
    }

    @Test
    void noArgsConstructorAndSetters() {
        List<Map<String, Object>> datos = List.of(Map.of("key", "val"));
        List<String> noDisponibles = List.of("svc1", "svc2");
        ReporteResultadoDTO dto = new ReporteResultadoDTO();
        dto.setTipoReporte("VENTAS");
        dto.setTotalRegistros(10L);
        dto.setDatos(datos);
        dto.setServiciosNoDisponibles(noDisponibles);

        assertEquals("VENTAS", dto.getTipoReporte());
        assertEquals(10L, dto.getTotalRegistros());
        assertEquals(1, dto.getDatos().size());
        assertEquals(2, dto.getServiciosNoDisponibles().size());
    }

    @Test
    void testToString() {
        ReporteResultadoDTO dto = new ReporteResultadoDTO("T", 5L, List.of(), List.of());
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        List<Map<String, Object>> datos = List.of(Map.of("id", 1));
        List<String> noDisponibles = List.of("s1");
        ReporteResultadoDTO obj = new ReporteResultadoDTO("USUARIOS", 5L, datos, noDisponibles);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        List<Map<String, Object>> datos = List.of(Map.of("id", 1));
        List<String> noDisponibles = List.of("s1");
        ReporteResultadoDTO obj = new ReporteResultadoDTO("USUARIOS", 5L, datos, noDisponibles);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        ReporteResultadoDTO a = new ReporteResultadoDTO(null, null, null, null);
        ReporteResultadoDTO b = new ReporteResultadoDTO(null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        List<Map<String, Object>> datos = List.of(Map.of("id", 1));
        List<String> noDisponibles = List.of("s1");
        ReporteResultadoDTO a = new ReporteResultadoDTO("USUARIOS", 5L, datos, noDisponibles);
        ReporteResultadoDTO b = new ReporteResultadoDTO("USUARIOS", 5L, datos, noDisponibles);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        List<Map<String, Object>> datos = List.of(Map.of("id", 1));
        List<String> noDisponibles = List.of("s1");

        // f1: tipoReporte
        assertNotEquals(new ReporteResultadoDTO(null, 5L, datos, noDisponibles), new ReporteResultadoDTO("T1", 5L, datos, noDisponibles));
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, datos, noDisponibles), new ReporteResultadoDTO(null, 5L, datos, noDisponibles));
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, datos, noDisponibles), new ReporteResultadoDTO("T2", 5L, datos, noDisponibles));

        // f2: totalRegistros
        assertNotEquals(new ReporteResultadoDTO("T1", null, datos, noDisponibles), new ReporteResultadoDTO("T1", 5L, datos, noDisponibles));
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, datos, noDisponibles), new ReporteResultadoDTO("T1", null, datos, noDisponibles));
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, datos, noDisponibles), new ReporteResultadoDTO("T1", 10L, datos, noDisponibles));

        // f3: datos
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, null, noDisponibles), new ReporteResultadoDTO("T1", 5L, datos, noDisponibles));
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, datos, noDisponibles), new ReporteResultadoDTO("T1", 5L, null, noDisponibles));
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, datos, noDisponibles), new ReporteResultadoDTO("T1", 5L, List.of(Map.of("id", 2)), noDisponibles));

        // f4: serviciosNoDisponibles
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, datos, null), new ReporteResultadoDTO("T1", 5L, datos, noDisponibles));
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, datos, noDisponibles), new ReporteResultadoDTO("T1", 5L, datos, null));
        assertNotEquals(new ReporteResultadoDTO("T1", 5L, datos, noDisponibles), new ReporteResultadoDTO("T1", 5L, datos, List.of("s2")));
    }

    @Test
    void testHashCodeCoverage() {
        ReporteResultadoDTO allNull = new ReporteResultadoDTO(null, null, null, null);
        allNull.hashCode();
        List<Map<String, Object>> datos = List.of(Map.of("id", 1));
        List<String> noDisponibles = List.of("s1");
        ReporteResultadoDTO allNonNull = new ReporteResultadoDTO("USUARIOS", 5L, datos, noDisponibles);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        List<Map<String, Object>> datos = List.of(Map.of("id", 1));
        List<String> noDisponibles = List.of("s1");
        ReporteResultadoDTO base = new ReporteResultadoDTO("T", 5L, datos, noDisponibles);
        ReporteResultadoDTOSub sub = new ReporteResultadoDTOSub("T", 5L, datos, noDisponibles);
        assertNotEquals(base, sub);
    }
}
