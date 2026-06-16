package com.ecomarket.pedidos.exception;

import com.ecomarket.pedidos.controller.PedidoController;
import com.ecomarket.pedidos.dto.ErrorResponseDTO;
import com.ecomarket.pedidos.service.PedidoService;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mvc;

    @MockitoBean PedidoService pedidoService;

    @Nested
    @DisplayName("400 BAD REQUEST — RuntimeException")
    class RuntimeExcepcionGenerica {

        @Test
        @DisplayName("pedido no encontrado → 400 con status, error, message y path")
        void runtimeExceptionDevuelve400() throws Exception {
            when(pedidoService.buscarPorId(99L))
                    .thenThrow(new RuntimeException("Pedido no encontrado"));

            mvc.perform(get("/api/pedidos/99"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Pedido no encontrado"))
                    .andExpect(jsonPath("$.path").value("/api/pedidos/99"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("409 CONFLICT — DataIntegrityViolationException")
    class DbIntegrity {

        @Test
        @DisplayName("violación de integridad en BD → 409 con mensaje genérico")
        void dataIntegrityDevuelve409() throws Exception {
            when(pedidoService.buscarPorId(1L))
                    .thenThrow(new DataIntegrityViolationException("constraint violation"));

            mvc.perform(get("/api/pedidos/1"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"))
                    .andExpect(jsonPath("$.message").value("Error de integridad en la base de datos."));
        }
    }

    @Nested
    @DisplayName("400 BAD REQUEST — MethodArgumentNotValidException")
    class Validacion {

        @Test
        @DisplayName("campo inválido → 400 con 'details' mapeando el campo al mensaje")
        void validationExceptionDevuelve400() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();

            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("pedido", "clienteId", "no debe estar nulo");
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

            MethodParameter methodParameter = mock(MethodParameter.class);
            MethodArgumentNotValidException ex =
                    new MethodArgumentNotValidException(methodParameter, bindingResult);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/pedidos/generar/5/1");

            ResponseEntity<ErrorResponseDTO> response = handler.handleValidationExceptions(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getMessage())
                    .isEqualTo("La validación de los datos ha fallado. Revisa los detalles.");
            assertThat(response.getBody().getDetails()).containsEntry("clienteId", "no debe estar nulo");
            assertThat(response.getBody().getPath()).isEqualTo("/api/pedidos/generar/5/1");
        }
    }

    @Nested
    @DisplayName("500 INTERNAL SERVER ERROR — Exception genérica")
    class ErrorInterno {

        @Test
        @DisplayName("error no controlado → 500 con mensaje genérico")
        void exceptionGeneralDevuelve500() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();

            Exception excepcionCualquiera = new Exception("NullPointerException en el cálculo del total");

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/pedidos/1/estado/2");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleGeneralException(excepcionCualquiera, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().getMessage())
                    .isEqualTo("Ocurrió un error inesperado en el servidor.");
            assertThat(response.getBody().getPath()).isEqualTo("/api/pedidos/1/estado/2");
        }
    }
}
