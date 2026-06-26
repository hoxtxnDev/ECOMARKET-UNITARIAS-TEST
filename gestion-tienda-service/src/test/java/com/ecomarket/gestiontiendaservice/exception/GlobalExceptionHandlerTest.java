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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    HttpServletRequest request;

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleYaExisteEnDBException retorna 409 CONFLICT")
    void yaExisteEnDB() {
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ErrorResponseDTO> response = handler.handleYaExisteEnDBException(
                new YaExisteEnBdException("El recurso ya existe"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).isEqualTo("El recurso ya existe");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("handleNoExisteEnDBException retorna 404 NOT_FOUND")
    void noExisteEnDB() {
        when(request.getRequestURI()).thenReturn("/api/test");

        ResponseEntity<ErrorResponseDTO> response = handler.handleNoExisteEnDBException(
                new NoExisteEnBdException("El recurso no existe"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("El recurso no existe");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }

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
    @DisplayName("handleHttpMessageNotReadableException retorna 400")
    void httpMessageNotReadable() {
        when(request.getRequestURI()).thenReturn("/api/test");

        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<ErrorResponseDTO> response = handler.handleHttpMessageNotReadableException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("JSON mal formado");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }

    @Test
    @DisplayName("handleMissingParams retorna 400 con nombre del parametro")
    void missingParams() {
        when(request.getRequestURI()).thenReturn("/api/test");

        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("id", "Long");

        ResponseEntity<ErrorResponseDTO> response = handler.handleMissingParams(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("id");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    @DisplayName("handleTypeMismatch retorna 400 con tipo esperado")
    void typeMismatch() {
        when(request.getRequestURI()).thenReturn("/api/test");

        MethodParameter param = mock(MethodParameter.class);
        when(param.getParameterName()).thenReturn("id");
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getParameter()).thenReturn(param);
        when(ex.getRequiredType()).thenReturn((Class) Integer.class);

        ResponseEntity<ErrorResponseDTO> response = handler.handleTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("id");
        assertThat(response.getBody().getMessage()).contains("Integer");
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
