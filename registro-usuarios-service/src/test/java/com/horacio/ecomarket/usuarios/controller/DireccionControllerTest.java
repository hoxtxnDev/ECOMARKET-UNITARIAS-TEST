package com.horacio.ecomarket.usuarios.controller;

import tools.jackson.databind.ObjectMapper;
import com.horacio.ecomarket.usuarios.dto.CrearDireccionRequest;
import com.horacio.ecomarket.usuarios.exception.RecursoNoEncontradoException;
import com.horacio.ecomarket.usuarios.model.Direccion;
import com.horacio.ecomarket.usuarios.service.DireccionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DireccionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("DireccionController")
class DireccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DireccionService direccionService;

    private Direccion direccionBase;

    @BeforeEach
    void setUp() {
        direccionBase = Direccion.builder()
                .id(1L)
                .usuarioId(10L)
                .calle("Av. Principal")
                .numero("123")
                .ciudad("Santiago")
                .region("Metropolitana")
                .destinatario("Juan Pérez")
                .esPredeterminada(false)
                .build();
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Nested
    @DisplayName("agregar")
    class Agregar {

        private CrearDireccionRequest crearRequest() {
            CrearDireccionRequest req = new CrearDireccionRequest();
            req.setCalle("Av. Principal");
            req.setNumero("123");
            req.setCiudad("Santiago");
            req.setRegion("Metropolitana");
            req.setDestinatario("Juan Pérez");
            req.setEsPredeterminada(false);
            return req;
        }

        @Test
        @DisplayName("200 OK al agregar dirección desde X-User-Id header")
        void agregarExitoso() throws Exception {
            when(direccionService.agregarDireccion(eq(10L), any(Direccion.class))).thenReturn(direccionBase);

            mockMvc.perform(post("/api/usuarios/direcciones")
                    .header("X-User-Id", "10")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(crearRequest())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.calle").value("Av. Principal"));
        }

        @Test
        @DisplayName("200 OK al agregar dirección como admin con path userId")
        void agregarAdminExitoso() throws Exception {
            when(direccionService.agregarDireccion(eq(10L), any(Direccion.class))).thenReturn(direccionBase);

            mockMvc.perform(post("/api/usuarios/direcciones/10")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(crearRequest())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.calle").value("Av. Principal"));
        }
    }

    @Nested
    @DisplayName("listar")
    class Listar {

        @Test
        @DisplayName("200 OK al listar direcciones de un usuario")
        void listarExitoso() throws Exception {
            when(direccionService.listarDirecciones(10L)).thenReturn(List.of(direccionBase));

            mockMvc.perform(get("/api/usuarios/direcciones/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].calle").value("Av. Principal"));
        }

        @Test
        @DisplayName("200 OK con lista vacía cuando el usuario no tiene direcciones")
        void listarSinDirecciones() throws Exception {
            when(direccionService.listarDirecciones(10L)).thenReturn(List.of());

            mockMvc.perform(get("/api/usuarios/direcciones/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {

        @Test
        @DisplayName("200 OK al obtener dirección por id")
        void obtenerPorIdExitoso() throws Exception {
            when(direccionService.obtenerPorId(1L)).thenReturn(direccionBase);

            mockMvc.perform(get("/api/usuarios/direcciones/id/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.destinatario").value("Juan Pérez"));
        }

        @Test
        @DisplayName("404 NOT FOUND cuando la dirección no existe")
        void obtenerPorIdNoExiste() throws Exception {
            when(direccionService.obtenerPorId(99L))
                    .thenThrow(new RecursoNoEncontradoException("La dirección con id 99 no existe."));

            mockMvc.perform(get("/api/usuarios/direcciones/id/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("obtenerPredeterminada")
    class ObtenerPredeterminada {

        @Test
        @DisplayName("200 OK al obtener dirección predeterminada")
        void obtenerPredeterminadaExitoso() throws Exception {
            Direccion predeterminada = Direccion.builder()
                    .id(2L)
                    .usuarioId(10L)
                    .calle("Casa Principal")
                    .numero("456")
                    .ciudad("Santiago")
                    .region("Metropolitana")
                    .destinatario("Juan Pérez")
                    .esPredeterminada(true)
                    .build();

            when(direccionService.obtenerPredeterminada(10L)).thenReturn(predeterminada);

            mockMvc.perform(get("/api/usuarios/direcciones/predeterminada/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.esPredeterminada").value(true));
        }

        @Test
        @DisplayName("404 NOT FOUND cuando el usuario no tiene dirección predeterminada")
        void obtenerPredeterminadaNoExiste() throws Exception {
            when(direccionService.obtenerPredeterminada(10L))
                    .thenThrow(new RecursoNoEncontradoException("El usuario no tiene una dirección predeterminada configurada."));

            mockMvc.perform(get("/api/usuarios/direcciones/predeterminada/10"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("editar")
    class Editar {

        @Test
        @DisplayName("200 OK al editar dirección con datos válidos")
        void editarExitoso() throws Exception {
            Direccion editada = Direccion.builder()
                    .id(1L)
                    .usuarioId(10L)
                    .calle("Av. Modificada")
                    .numero("789")
                    .ciudad("Santiago")
                    .region("Metropolitana")
                    .destinatario("Juan Pérez")
                    .esPredeterminada(true)
                    .build();

            when(direccionService.editarDireccion(eq(1L), any(Direccion.class))).thenReturn(editada);

            mockMvc.perform(put("/api/usuarios/direcciones/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(editada)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.calle").value("Av. Modificada"))
                    .andExpect(jsonPath("$.esPredeterminada").value(true));
        }

        @Test
        @DisplayName("404 NOT FOUND al editar dirección que no existe")
        void editarNoExiste() throws Exception {
            when(direccionService.editarDireccion(eq(99L), any(Direccion.class)))
                    .thenThrow(new RecursoNoEncontradoException("Dirección no encontrada."));

            mockMvc.perform(put("/api/usuarios/direcciones/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(direccionBase)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("eliminar")
    class Eliminar {

        @Test
        @DisplayName("204 NO CONTENT al eliminar dirección existente")
        void eliminarExitoso() throws Exception {
            doNothing().when(direccionService).eliminarDireccion(1L);

            mockMvc.perform(delete("/api/usuarios/direcciones/1"))
                    .andExpect(status().isNoContent());

            verify(direccionService).eliminarDireccion(1L);
        }
    }
}
