package com.horacio.ecomarket.usuarios.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.horacio.ecomarket.usuarios.dto.ErrorResponseDTO;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();

        request = new MockHttpServletRequest();
        request.setRequestURI("/api/usuarios/test");
    }

    @Nested
    @DisplayName("handleRuntimeException")
    class RuntimeExceptionTest {

        @Test
        @DisplayName("Debería retornar 400 BAD REQUEST con el mensaje de la excepción")
        void testHandleRuntimeException() {
            RuntimeException ex = new RuntimeException("Error genérico de negocio");
            ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleRuntimeException(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("Error genérico de negocio");
            assertThat(response.getBody().getPath()).isEqualTo("/api/usuarios/test");
        }
    }

    @Nested
    @DisplayName("handleValidationExceptions")
    class ValidationExceptionTest {

        @Test
        @DisplayName("Debería retornar 400 BAD REQUEST y un mapa con los errores de los campos")
        void testHandleValidationExceptions() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);

            FieldError fieldError = new FieldError("usuarioDTO", "correo", "El correo no es válido");
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
            when(ex.getBindingResult()).thenReturn(bindingResult);

            ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleValidationExceptions(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("La validación de los datos ha fallado. Revisa los detalles.");

            assertThat(response.getBody().getDetails())
                    .isNotNull()
                    .containsEntry("correo", "El correo no es válido");
        }
    }

    @Nested
    @DisplayName("handleDatabaseExceptions")
    class DatabaseExceptionTest {

        @Test
        @DisplayName("Debería retornar 409 CONFLICT extrayendo la causa más específica")
        void testHandleDatabaseExceptionsConCausaEspecifica() {
            Throwable causaRaiz = new Throwable("El correo 'test@eco.cl' ya existe (Unique Constraint)");
            DataIntegrityViolationException ex = new DataIntegrityViolationException("Error general de Hibernate", causaRaiz);

            ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleDatabaseExceptions(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
            assertThat(response.getBody().getError()).isEqualTo("Database Conflict");
            assertThat(response.getBody().getMessage()).isEqualTo("Error de integridad en la base de datos: El correo 'test@eco.cl' ya existe (Unique Constraint)");
        }

        @Test
        @DisplayName("Debería retornar 409 CONFLICT usando el mensaje general si no hay causa específica")
        void testHandleDatabaseExceptionsSinCausaEspecifica() {
            DataIntegrityViolationException ex = new DataIntegrityViolationException("Error de integridad sin detalles");

            ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleDatabaseExceptions(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("Error de integridad en la base de datos: Error de integridad sin detalles");
        }

        @Test
        @DisplayName("Debería retornar 409 CONFLICT usando el mensaje general si getMostSpecificCause es null")
        void testHandleDatabaseExceptionsCausaEspecificaNull() {
            DataIntegrityViolationException ex = mock(DataIntegrityViolationException.class);
            when(ex.getMostSpecificCause()).thenReturn(null);
            when(ex.getMessage()).thenReturn("Mensaje de error directamente de la excepción");

            ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleDatabaseExceptions(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("Error de integridad en la base de datos: Mensaje de error directamente de la excepción");
        }
    }

    @Nested
    @DisplayName("handleGeneralException")
    class GeneralExceptionTest {

        @Test
        @DisplayName("Debería retornar 500 INTERNAL SERVER ERROR")
        void testHandleGeneralException() {
            Exception ex = new Exception("Se cayó la base de datos de producción");

            ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleGeneralException(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(500);

            assertThat(response.getBody().getMessage()).isEqualTo("Ocurrio un error inesperado en el servidor.");
            assertThat(response.getBody().getPath()).isEqualTo("/api/usuarios/test");
        }
    }

    @Nested
    @DisplayName("handleRecursoNoEncontrado")
    class RecursoNoEncontradoTest {

        @Test
        @DisplayName("Debería retornar 404 NOT FOUND")
        void testHandleRecursoNoEncontrado() {
            RecursoNoEncontradoException ex = new RecursoNoEncontradoException("Dirección no encontrada.");
            ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleRecursoNoEncontrado(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(404);
            assertThat(response.getBody().getMessage()).isEqualTo("Dirección no encontrada.");
        }
    }

    @Nested
    @DisplayName("handleCorreoDuplicado")
    class CorreoDuplicadoTest {

        @Test
        @DisplayName("Debería retornar 409 CONFLICT")
        void testHandleCorreoDuplicado() {
            CorreoDuplicadoException ex = new CorreoDuplicadoException("Correo duplicado");
            ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleCorreoDuplicado(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
            assertThat(response.getBody().getMessage()).isEqualTo("Correo duplicado");
        }
    }

    @Nested
    @DisplayName("handleTelefonoDuplicado")
    class TelefonoDuplicadoTest {

        @Test
        @DisplayName("Debería retornar 409 CONFLICT")
        void testHandleTelefonoDuplicado() {
            TelefonoDuplicadoException ex = new TelefonoDuplicadoException("Teléfono duplicado");
            ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleTelefonoDuplicado(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
            assertThat(response.getBody().getMessage()).isEqualTo("Teléfono duplicado");
        }
    }
}
