package com.ecomarket.catalogoinventarioservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ErrorResponseDTOTest {

    @Test
    void builderCreatesErrorResponse() {
        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.of(2026, 6, 16, 12, 0))
                .status(400)
                .error("Bad Request")
                .message("Error message")
                .path("/api/test")
                .details(Map.of("campo", "error"))
                .build();

        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertTrue(response.getDetails().containsKey("campo"));
    }
}
