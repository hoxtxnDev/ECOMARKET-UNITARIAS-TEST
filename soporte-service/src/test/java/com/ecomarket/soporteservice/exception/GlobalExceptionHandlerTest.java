package com.ecomarket.soporteservice.exception;

import com.ecomarket.soporteservice.model.reference.CanalNotificacion;
import com.ecomarket.soporteservice.service.CanalNotificacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MissingServletRequestParameterException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ecomarket.soporteservice.controller.CanalNotificacionController;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Testea GlobalExceptionHandler disparando excepciones via
 * CanalNotificacionController
 * (el controller más simple, sin dependencias extra).
 */
@WebMvcTest(CanalNotificacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    @MockitoBean
    CanalNotificacionService service;

    private CanalNotificacion canal(Long id, String nombre) {
        return new CanalNotificacion(id, nombre);
    }

    // ─── YaExisteEnBdException → 409 ─────────────────────────────────────────
    @Nested
    @DisplayName("YaExisteEnBdException")
    class YaExiste {
        @Test
        @DisplayName("POST duplicado → 409 CONFLICT")
        void yaExiste() throws Exception {
            when(service.createCanalNotificacion(any()))
                    .thenThrow(new YaExisteEnBdException("EMAIL ya existe."));
            mvc.perform(post("/api/v1/canal-notificacion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(canal(null, "EMAIL"))))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"));
        }
    }

    // ─── NoExisteEnBdException → 404 ─────────────────────────────────────────
    @Nested
    @DisplayName("NoExisteEnBdException")
    class NoExiste {
        @Test
        @DisplayName("DELETE inexistente → 404 NOT FOUND")
        void noExiste() throws Exception {
            doThrow(new NoExisteEnBdException("99 no existe."))
                    .when(service).deleteCanalNotificacionById(99L);
            mvc.perform(delete("/api/v1/canal-notificacion/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    // ─── MethodArgumentNotValidException → 400 ───────────────────────────────
    @Nested
    @DisplayName("MethodArgumentNotValidException")
    class ValidationError {
        @Test
        @DisplayName("POST nombre vacío → 400 con detalles de campo")
        void validationFail() throws Exception {
            mvc.perform(post("/api/v1/canal-notificacion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(canal(null, ""))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.details").exists());
        }
    }

    // ─── MissingServletRequestParameterException → 400 ───────────────────────────
    @Nested
    @DisplayName("MissingServletRequestParameterException")
    class MissingParam {
        @Test
        @DisplayName("Parámetro requerido faltante → 400 con mensaje descriptivo")
        void missingParam() throws Exception {
            GlobalExceptionHandler handler = new GlobalExceptionHandler();
            MissingServletRequestParameterException ex = new MissingServletRequestParameterException("filtro",
                    "String");

            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/api/v1/canal-notificacion");

            var response = handler.handleMissingParams(ex, request);

            assertThat(response.getStatusCode().value()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).contains("filtro");
        }
    }

    // ─── DataIntegrityViolationException → 409 ───────────────────────────────
    @Nested
    @DisplayName("DataIntegrityViolationException")
    class DbIntegrity {
        @Test
        @DisplayName("POST con violación de integridad → 409 Database Conflict")
        void dbConflict() throws Exception {
            when(service.createCanalNotificacion(any()))
                    .thenThrow(new DataIntegrityViolationException("Duplicate entry"));
            mvc.perform(post("/api/v1/canal-notificacion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(canal(null, "EMAIL"))))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Database Conflict"));
        }
    }

    // ─── HttpMessageNotReadableException → 400 ───────────────────────────────
    @Nested
    @DisplayName("HttpMessageNotReadableException")
    class MalformedJson {
        @Test
        @DisplayName("POST con JSON mal formado → 400")
        void malformedJson() throws Exception {
            mvc.perform(post("/api/v1/canal-notificacion")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{nombre: INVALID JSON"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(
                            org.hamcrest.Matchers.containsString("JSON mal formado")));
        }
    }

    // ─── MethodArgumentTypeMismatchException → 400 ───────────────────────────
    @Nested
    @DisplayName("MethodArgumentTypeMismatchException")
    class TypeMismatch {
        @Test
        @DisplayName("DELETE con id no numérico → 400")
        void typeMismatch() throws Exception {
            mvc.perform(delete("/api/v1/canal-notificacion/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    // ─── Exception genérica → 500 ────────────────────────────────────────────
    @Nested
    @DisplayName("Exception genérica")
    class GenericError {
        @Test
        @DisplayName("Error inesperado → 500 INTERNAL SERVER ERROR")
        void genericException() throws Exception {
            when(service.readAllCanalNotificacion())
                    .thenThrow(new RuntimeException("Error inesperado"));
            mvc.perform(get("/api/v1/canal-notificacion"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500));
        }
    }
}