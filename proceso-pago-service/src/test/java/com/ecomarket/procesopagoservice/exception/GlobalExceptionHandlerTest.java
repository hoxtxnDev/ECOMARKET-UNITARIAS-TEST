package com.ecomarket.procesopagoservice.exception;

import com.ecomarket.procesopagoservice.controller.PagoController;
import com.ecomarket.procesopagoservice.dto.ErrorResponseDTO;
import com.ecomarket.procesopagoservice.service.PagoService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias para GlobalExceptionHandler.
 *
 * Estrategia: RuntimeException, DataIntegrityViolationException y las
 * excepciones personalizadas (RecursoNoEncontradoException,
 * ProcesamientoPagoException, EstadoTransaccionInvalidoException,
 * CuponInvalidoException) se prueban a través de MockMvc forzando
 * al service mock a lanzarlas.
 *
 * MethodArgumentNotValidException no tiene disparador natural aquí porque
 * PagoController.iniciarPago no usa @Valid sobre el @RequestBody, y la
 * Exception genérica tampoco se puede forzar de forma realista a través de
 * Spring MVC. Para esos dos casos se invoca el handler directamente, igual
 * que en iniciosesion-service.
 */
@WebMvcTest(PagoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mvc;

    @MockitoBean PagoService pagoService;

    // ═════════════════════════════════════════════════════════════════════════
    // 400 BAD REQUEST — RuntimeException genérica
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("400 BAD REQUEST — RuntimeException")
    class RuntimeExcepcionGenerica {

        @Test
        @DisplayName("transacción no encontrada → 400 con status, error, message y path")
        void runtimeExceptionDevuelve400() throws Exception {
            when(pagoService.obtenerTransaccion(99L))
                    .thenThrow(new RuntimeException("Transacción no encontrada: 99"));

            mvc.perform(get("/api/pagos/99"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Transacción no encontrada: 99"))
                    .andExpect(jsonPath("$.path").value("/api/pagos/99"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 409 CONFLICT — DataIntegrityViolationException
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("409 CONFLICT — DataIntegrityViolationException")
    class DbIntegrity {

        @Test
        @DisplayName("violación de integridad en BD → 409 con mensaje genérico")
        void dataIntegrityDevuelve409() throws Exception {
            when(pagoService.obtenerTransaccion(1L))
                    .thenThrow(new DataIntegrityViolationException("constraint violation"));

            mvc.perform(get("/api/pagos/1"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"))
                    .andExpect(jsonPath("$.message").value("Error de integridad en la base de datos."));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 400 BAD REQUEST — MethodArgumentNotValidException
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("400 BAD REQUEST — MethodArgumentNotValidException")
    class Validacion {

        @Test
        @DisplayName("campo inválido → 400 con 'details' mapeando el campo al mensaje")
        void validationExceptionDevuelve400() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();

            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("metodoPagoTransaccion", "nombre", "no debe estar vacío");
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

            MethodParameter methodParameter = mock(MethodParameter.class);
            MethodArgumentNotValidException ex =
                    new MethodArgumentNotValidException(methodParameter, bindingResult);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/pagos/iniciar");

            ResponseEntity<ErrorResponseDTO> response = handler.handleValidationExceptions(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getMessage())
                    .isEqualTo("La validación de los datos ha fallado. Revisa los detalles.");
            assertThat(response.getBody().getDetails()).containsEntry("nombre", "no debe estar vacío");
            assertThat(response.getBody().getPath()).isEqualTo("/api/pagos/iniciar");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 500 INTERNAL SERVER ERROR — Exception genérica
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("500 INTERNAL SERVER ERROR — Exception genérica")
    class ErrorInterno {

        @Test
        @DisplayName("error no controlado → 500 con mensaje genérico (oculta el real)")
        void exceptionGeneralDevuelve500() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();

            Exception excepcionCualquiera = new Exception("NullPointerException en el cálculo del descuento");

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/pagos/1/cupon/1");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleGeneralException(excepcionCualquiera, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().getMessage())
                    .isEqualTo("Ocurrió un error inesperado en el servidor.");
            assertThat(response.getBody().getPath()).isEqualTo("/api/pagos/1/cupon/1");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 404 NOT FOUND — RecursoNoEncontradoException
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("404 NOT FOUND — RecursoNoEncontradoException")
    class RecursoNoEncontrado {

        @Test
        @DisplayName("recurso no encontrado → 404 con mensaje descriptivo")
        void recursoNoEncontradoDevuelve404() throws Exception {
            when(pagoService.obtenerTransaccion(99L))
                    .thenThrow(new RecursoNoEncontradoException("Transacción no encontrada: 99"));

            mvc.perform(get("/api/pagos/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("Transacción no encontrada: 99"));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 422 UNPROCESSABLE ENTITY — ProcesamientoPagoException
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("422 UNPROCESSABLE ENTITY — ProcesamientoPagoException")
    class ProcesamientoPago {

        @Test
        @DisplayName("error de procesamiento → 422 con mensaje")
        void procesamientoPagoDevuelve422() throws Exception {
            when(pagoService.enviarBoletaEmail(1L, ""))
                    .thenThrow(new ProcesamientoPagoException("Error al enviar boleta"));

            mvc.perform(post("/api/pagos/1/email"))
                    .andExpect(status().isUnprocessableContent())
                    .andExpect(jsonPath("$.status").value(422))
                    .andExpect(jsonPath("$.error").value("Unprocessable Content"))
                    .andExpect(jsonPath("$.message").value("Error al enviar boleta"));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 400 BAD REQUEST — EstadoTransaccionInvalidoException
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("400 BAD REQUEST — EstadoTransaccionInvalidoException")
    class EstadoInvalido {

        @Test
        @DisplayName("estado inválido → 400 con mensaje")
        void estadoInvalidoDevuelve400() throws Exception {
            when(pagoService.obtenerTransaccion(1L))
                    .thenThrow(new EstadoTransaccionInvalidoException("Estado inválido"));

            mvc.perform(get("/api/pagos/1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Estado inválido"));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // 400 BAD REQUEST — CuponInvalidoException
    // ═════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("400 BAD REQUEST — CuponInvalidoException")
    class CuponInvalido {

        @Test
        @DisplayName("cupón inválido → 400 con mensaje")
        void cuponInvalidoDevuelve400() throws Exception {
            when(pagoService.anadirCuponDescuento(1L, 1L))
                    .thenThrow(new CuponInvalidoException("El cupón ha expirado"));

            mvc.perform(post("/api/pagos/1/cupon/1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("El cupón ha expirado"));
        }
    }
}