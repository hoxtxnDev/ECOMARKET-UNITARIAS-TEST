package com.ecomarket.envioservice.exception;

import com.ecomarket.envioservice.controller.EnvioController;
import com.ecomarket.envioservice.dto.ErrorResponseDTO;
import com.ecomarket.envioservice.service.EnvioDomainService;
import com.ecomarket.envioservice.service.EnvioService;
import com.ecomarket.envioservice.service.RutaTransporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Mock EnvioService envioService;
    @Mock EnvioDomainService envioDomainService;
    @Mock RutaTransporteService rutaTransporteService;

    MockMvc mvc;

    @BeforeEach
    void setup() {
        var controller = new EnvioController(envioService, envioDomainService, rutaTransporteService);
        mvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("404 NOT FOUND — NoExisteEnBdException")
    class NoExiste {

        @Test
        @DisplayName("envio no encontrado → 404 con status, error, message y path")
        void noExisteEnBdDevuelve404() throws Exception {
            when(envioService.obtenerEnvioPorId(99L))
                    .thenThrow(new NoExisteEnBdException("El envio con id 99 no existe en la DB."));

            mvc.perform(get("/api/v1/logistica-envios/envios/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("El envio con id 99 no existe en la DB."))
                    .andExpect(jsonPath("$.path").value("/api/v1/logistica-envios/envios/99"))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("409 CONFLICT — YaExisteEnBdException")
    class YaExiste {

        @Test
        @DisplayName("entidad duplicada → 409")
        void yaExisteDevuelve409() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/estado-envio");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleYaExisteEnDBException(
                            new YaExisteEnBdException("El estado envio con nombre PENDIENTE ya existe en BD."),
                            request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().getMessage()).contains("ya existe");
            assertThat(response.getBody().getStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("409 CONFLICT — PedidoClienteIncompatibleException")
    class PedidoClienteIncompatible {

        @Test
        @DisplayName("pedido no compatible con cliente → 409")
        void incompatibleDevuelve409() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/logistica-envios/envios");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handlePedidoClienteIncompatibleException(
                            new PedidoClienteIncompatibleException("No compatible"),
                            request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().getStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("409 CONFLICT — EnvioEstadoInvalidoException")
    class EstadoInvalido {

        @Test
        @DisplayName("estado invalido para operacion → 409")
        void estadoInvalidoDevuelve409() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/logistica-envios/envios/10/cancelar");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleEnvioEstadoInvalidoException(
                            new EnvioEstadoInvalidoException("No se puede cancelar"),
                            request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().getStatus()).isEqualTo(409);
        }
    }

    @Nested
    @DisplayName("400 BAD REQUEST — MethodArgumentNotValidException")
    class Validacion {

        @Test
        @DisplayName("campo invalido → 400 con details")
        void validationExceptionDevuelve400() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();

            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("crearEnvioRequest", "pedidoId", "no debe estar nulo");
            when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

            MethodArgumentNotValidException ex =
                    new MethodArgumentNotValidException(null, bindingResult);

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/logistica-envios/envios");

            ResponseEntity<ErrorResponseDTO> response = handler.handleValidationExceptions(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getMessage()).isEqualTo("La validacion de los datos ha fallado. Revisa los detalles.");
            assertThat(response.getBody().getDetails()).containsEntry("pedidoId", "no debe estar nulo");
        }
    }

    @Nested
    @DisplayName("409 CONFLICT — DataIntegrityViolationException")
    class DbIntegrity {

        @Test
        @DisplayName("violacion de integridad → 409 con mensaje generico")
        void dataIntegrityDevuelve409() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/logistica-envios/envios");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleDatabaseExceptions(new DataIntegrityViolationException("constraint"), request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody().getMessage()).contains("Error de integridad");
        }
    }

    @Nested
    @DisplayName("400 BAD REQUEST — HttpMessageNotReadableException")
    class JsonMalformado {

        @Test
        @DisplayName("JSON mal formado → 400")
        void jsonInvalidoDevuelve400() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            MockHttpServletRequest request = new MockHttpServletRequest();

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleHttpMessageNotReadableException(
                            new HttpMessageNotReadableException("JSON parse error", mock(HttpInputMessage.class)), request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getMessage()).contains("JSON mal formado");
        }
    }

    @Nested
    @DisplayName("400 BAD REQUEST — MissingServletRequestParameterException")
    class ParametroFaltante {

        @Test
        @DisplayName("parametro requerido faltante → 400")
        void missingParamDevuelve400() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/logistica-envios/envios");

            org.springframework.web.bind.MissingServletRequestParameterException ex =
                    new org.springframework.web.bind.MissingServletRequestParameterException("clienteId", "Long");

            ResponseEntity<ErrorResponseDTO> response = handler.handleMissingParams(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getMessage()).contains("clienteId");
        }
    }

    @Nested
    @DisplayName("400 BAD REQUEST — MethodArgumentTypeMismatchException")
    class TipoInvalido {

        @Test
        @DisplayName("tipo de argumento incorrecto → 400")
        
        void typeMismatchDevuelve400() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            MockHttpServletRequest request = new MockHttpServletRequest();

            MethodParameter param = mock(MethodParameter.class);
            when(param.getParameterName()).thenReturn("id");
            MethodArgumentTypeMismatchException ex =
                    new MethodArgumentTypeMismatchException("abc", Long.class, "id", param, null);

            ResponseEntity<ErrorResponseDTO> response = handler.handleTypeMismatch(ex, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getMessage()).contains("id");
        }
    }

    @Nested
    @DisplayName("500 INTERNAL SERVER ERROR — Exception generica")
    class ErrorInterno {

        @Test
        @DisplayName("error no controlado → 500 con mensaje generico")
        void exceptionGeneralDevuelve500() {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/logistica-envios/envios");

            ResponseEntity<ErrorResponseDTO> response =
                    handler.handleGeneralException(new Exception("Error inesperado"), request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody().getMessage()).contains("error inesperado");
            assertThat(response.getBody().getPath()).isEqualTo("/api/v1/logistica-envios/envios");
        }
    }
}
