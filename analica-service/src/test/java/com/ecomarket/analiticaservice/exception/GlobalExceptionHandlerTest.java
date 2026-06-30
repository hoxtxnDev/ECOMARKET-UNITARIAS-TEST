package com.ecomarket.analiticaservice.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.ecomarket.analiticaservice.dto.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleValidationExceptionsReturnsBadRequestWithDetails() {
        var target = new Object();
        var bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "nombre", "El nombre es obligatorio"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponseDTO> response = handler.handleValidationExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getDetails().containsKey("nombre"));
    }

    @Test
    void handleNoExisteEnBdReturnsNotFound() {
        var ex = new NoExisteEnBdException("Entidad no encontrada");

        ResponseEntity<ErrorResponseDTO> response = handler.handleNoExisteEnBd(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Entidad no encontrada", response.getBody().getMessage());
    }

    @Test
    void handleDatabaseExceptionsReturnsConflict() {
        var ex = new DataIntegrityViolationException("Error BD");

        ResponseEntity<ErrorResponseDTO> response = handler.handleDatabaseExceptions(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("integridad"));
    }

    @Test
    void handleRuntimeExceptionReturnsBadRequest() {
        var ex = new RuntimeException("Error inesperado");

        ResponseEntity<ErrorResponseDTO> response = handler.handleRuntimeException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error inesperado", response.getBody().getMessage());
    }

    @Test
    void handleGeneralExceptionReturnsInternalServerError() {
        var ex = new Exception("Error crítico");

        ResponseEntity<ErrorResponseDTO> response = handler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ocurrió un error inesperado en el servidor.", response.getBody().getMessage());
    }
}
