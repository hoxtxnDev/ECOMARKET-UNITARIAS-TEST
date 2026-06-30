package com.ecomarket.analiticaservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ErrorResponseDTOTest {

    static class ErrorResponseDTOSub extends ErrorResponseDTO {
        public ErrorResponseDTOSub(LocalDateTime timestamp, int status, String error, String message, String path, Map<String, String> details) {
            super(timestamp, status, error, message, path, details);
        }
        @Override
        public boolean canEqual(Object other) { return other instanceof ErrorResponseDTOSub; }
    }

    @Test
    void allArgsConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Map<String, String> details = Map.of("campo", "error");
        ErrorResponseDTO dto = new ErrorResponseDTO(now, 400, "Bad Request", "msg", "/api/test", details);

        assertEquals(now, dto.getTimestamp());
        assertEquals(400, dto.getStatus());
        assertEquals("Bad Request", dto.getError());
        assertEquals("msg", dto.getMessage());
        assertEquals("/api/test", dto.getPath());
        assertEquals(details, dto.getDetails());
    }

    @Test
    void noArgsConstructorAndSetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Map<String, String> details = Map.of("key", "val");
        ErrorResponseDTO dto = new ErrorResponseDTO();
        dto.setTimestamp(now);
        dto.setStatus(500);
        dto.setError("Internal Error");
        dto.setMessage("error msg");
        dto.setPath("/api/error");
        dto.setDetails(details);

        assertEquals(now, dto.getTimestamp());
        assertEquals(500, dto.getStatus());
        assertEquals("Internal Error", dto.getError());
        assertEquals("error msg", dto.getMessage());
        assertEquals("/api/error", dto.getPath());
        assertEquals(details, dto.getDetails());
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        ErrorResponseDTO dto = new ErrorResponseDTO(now, 400, "e", "m", "p", Map.of());
        assertNotNull(dto.toString());
    }

    @Test
    void testIdentity() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Map<String, String> details = Map.of("campo", "error");
        ErrorResponseDTO obj = new ErrorResponseDTO(now, 400, "Bad Request", "msg", "/api/test", details);
        assertTrue(obj.equals(obj));
    }

    @Test
    void testNullAndType() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Map<String, String> details = Map.of("campo", "error");
        ErrorResponseDTO obj = new ErrorResponseDTO(now, 400, "Bad Request", "msg", "/api/test", details);
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("string"));
    }

    @Test
    void testAllNulls() {
        ErrorResponseDTO a = new ErrorResponseDTO(null, 0, null, null, null, null);
        ErrorResponseDTO b = new ErrorResponseDTO(null, 0, null, null, null, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testFullEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Map<String, String> details = Map.of("campo", "error");
        ErrorResponseDTO a = new ErrorResponseDTO(now, 400, "Bad Request", "msg", "/api/test", details);
        ErrorResponseDTO b = new ErrorResponseDTO(now, 400, "Bad Request", "msg", "/api/test", details);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testSequentialMismatches() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Map<String, String> details = Map.of("k", "v");

        // f1: timestamp
        assertNotEquals(new ErrorResponseDTO(null, 400, "e", "m", "p", details), new ErrorResponseDTO(now, 400, "e", "m", "p", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", "p", details), new ErrorResponseDTO(null, 400, "e", "m", "p", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", "p", details), new ErrorResponseDTO(now.plusDays(1), 400, "e", "m", "p", details));

        // f2: status
        assertNotEquals(new ErrorResponseDTO(now, 0, "e", "m", "p", details), new ErrorResponseDTO(now, 400, "e", "m", "p", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", "p", details), new ErrorResponseDTO(now, 0, "e", "m", "p", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", "p", details), new ErrorResponseDTO(now, 500, "e", "m", "p", details));

        // f3: error
        assertNotEquals(new ErrorResponseDTO(now, 400, null, "m", "p", details), new ErrorResponseDTO(now, 400, "e1", "m", "p", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e1", "m", "p", details), new ErrorResponseDTO(now, 400, null, "m", "p", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e1", "m", "p", details), new ErrorResponseDTO(now, 400, "e2", "m", "p", details));

        // f4: message
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", null, "p", details), new ErrorResponseDTO(now, 400, "e", "m1", "p", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m1", "p", details), new ErrorResponseDTO(now, 400, "e", null, "p", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m1", "p", details), new ErrorResponseDTO(now, 400, "e", "m2", "p", details));

        // f5: path
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", null, details), new ErrorResponseDTO(now, 400, "e", "m", "p1", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", "p1", details), new ErrorResponseDTO(now, 400, "e", "m", null, details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", "p1", details), new ErrorResponseDTO(now, 400, "e", "m", "p2", details));

        // f6: details
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", "p", null), new ErrorResponseDTO(now, 400, "e", "m", "p", details));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", "p", details), new ErrorResponseDTO(now, 400, "e", "m", "p", null));
        assertNotEquals(new ErrorResponseDTO(now, 400, "e", "m", "p", details), new ErrorResponseDTO(now, 400, "e", "m", "p", Map.of("x", "y")));
    }

    @Test
    void testHashCodeCoverage() {
        ErrorResponseDTO allNull = new ErrorResponseDTO(null, 0, null, null, null, null);
        allNull.hashCode();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Map<String, String> details = Map.of("campo", "error");
        ErrorResponseDTO allNonNull = new ErrorResponseDTO(now, 400, "Bad Request", "msg", "/api/test", details);
        allNonNull.hashCode();
    }

    @Test
    void testSubclassEquality() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        ErrorResponseDTO base = new ErrorResponseDTO(now, 400, "e", "m", "p", Map.of());
        ErrorResponseDTOSub sub = new ErrorResponseDTOSub(now, 400, "e", "m", "p", Map.of());
        assertNotEquals(base, sub);
    }

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        Map<String, String> details = Map.of("key", "val");
        ErrorResponseDTO.ErrorResponseDTOBuilder builder = ErrorResponseDTO.builder()
                .timestamp(now)
                .status(404)
                .error("Not Found")
                .message("msg")
                .path("/api")
                .details(details);
        assertNotNull(builder.toString());
        ErrorResponseDTO dto = builder.build();
        assertEquals(now, dto.getTimestamp());
        assertEquals(404, dto.getStatus());
        assertEquals("Not Found", dto.getError());
        assertEquals("msg", dto.getMessage());
        assertEquals("/api", dto.getPath());
        assertEquals(details, dto.getDetails());
    }
}
