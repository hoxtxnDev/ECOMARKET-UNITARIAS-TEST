package com.ecomarket.carritocompraservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ErrorResponseDTOTest {

    @Test
    void noArgsConstructor() {
        ErrorResponseDTO dto = new ErrorResponseDTO();
        assertNull(dto.getTimestamp());
        assertEquals(0, dto.getStatus());
        assertNull(dto.getError());
        assertNull(dto.getMessage());
        assertNull(dto.getPath());
        assertNull(dto.getDetails());
    }

    @Test
    void allArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> details = new HashMap<>();
        details.put("field", "error");
        
        ErrorResponseDTO dto = new ErrorResponseDTO(now, 400, "Bad Request", "Invalid", "/api/test", details);
        
        assertEquals(now, dto.getTimestamp());
        assertEquals(400, dto.getStatus());
        assertEquals("Bad Request", dto.getError());
        assertEquals("Invalid", dto.getMessage());
        assertEquals("/api/test", dto.getPath());
        assertEquals(details, dto.getDetails());
    }

    @Test
    void builderCreatesErrorResponse() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> details = new HashMap<>();
        details.put("field", "error message");

        ErrorResponseDTO dto = ErrorResponseDTO.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Invalid input")
                .path("/api/test")
                .details(details)
                .build();

        assertEquals(now, dto.getTimestamp());
        assertEquals(400, dto.getStatus());
        assertEquals("Bad Request", dto.getError());
        assertEquals("Invalid input", dto.getMessage());
        assertEquals("/api/test", dto.getPath());
        assertEquals(details, dto.getDetails());
    }

    @Test
    void settersWork() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> details = new HashMap<>();
        details.put("field", "error message");

        ErrorResponseDTO dto = new ErrorResponseDTO();
        dto.setTimestamp(now);
        dto.setStatus(400);
        dto.setError("Bad Request");
        dto.setMessage("Invalid input");
        dto.setPath("/api/test");
        dto.setDetails(details);

        assertEquals(now, dto.getTimestamp());
        assertEquals(400, dto.getStatus());
        assertEquals("Bad Request", dto.getError());
        assertEquals("Invalid input", dto.getMessage());
        assertEquals("/api/test", dto.getPath());
        assertEquals(details, dto.getDetails());
    }

    @Test
    void testToString() {
        ErrorResponseDTO dto = new ErrorResponseDTO();
        String str = dto.toString();
        assertNotNull(str);
    }

    @Test
    void testEquals() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponseDTO dto1 = new ErrorResponseDTO(now, 400, "Bad", "Invalid", "/api", null);
        ErrorResponseDTO dto2 = new ErrorResponseDTO(now, 400, "Bad", "Invalid", "/api", null);
        ErrorResponseDTO dto3 = new ErrorResponseDTO(now, 500, "Error", "Error", "/api", null);
        
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponseDTO dto1 = new ErrorResponseDTO(now, 400, "Bad", "Invalid", "/api", null);
        ErrorResponseDTO dto2 = new ErrorResponseDTO(now, 400, "Bad", "Invalid", "/api", null);
        
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
