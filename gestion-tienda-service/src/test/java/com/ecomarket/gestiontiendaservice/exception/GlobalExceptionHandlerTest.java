package com.ecomarket.gestiontiendaservice.exception;

import com.ecomarket.gestiontiendaservice.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    HttpServletRequest request;

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleValidationExceptions retorna 400 con detalles de errores")
    void validationException() {
        when(request.getRequestURI()).thenReturn("/api/test");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(List.of(
                new FieldError("obj", "nombre", "no debe estar vacio"),
                new FieldError("obj", "email", "formato invalido")
        ));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponseDTO> response = handler.handleValidationExceptions(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetails()).containsKeys("nombre", "email");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("handleDatabaseExceptions retorna 409")
    void databaseException() {
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ErrorResponseDTO> response = handler.handleDatabaseExceptions(
                new DataIntegrityViolationException("violacion"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).contains("Error de integridad");
    }

    @Test
    @DisplayName("handleRuntimeException retorna 400 con el mensaje")
    void runtimeException() {
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ErrorResponseDTO> response = handler.handleRuntimeException(
                new RuntimeException("Algo salio mal"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Algo salio mal");
    }

    @Test
    @DisplayName("handleGeneralException retorna 500")
    void generalException() {
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ErrorResponseDTO> response = handler.handleGeneralException(
                new Exception("Error critico"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).contains("error inesperado");
    }
}
