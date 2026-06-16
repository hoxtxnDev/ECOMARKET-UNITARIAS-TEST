package com.ecomarket.envioservice.controller;

import com.ecomarket.envioservice.dto.*;
import com.ecomarket.envioservice.model.entity.Envio;
import com.ecomarket.envioservice.model.entity.HistorialEnvio;
import com.ecomarket.envioservice.model.entity.RutaTransporte;
import com.ecomarket.envioservice.model.reference.EstadoEnvio;
import com.ecomarket.envioservice.model.reference.MetodoEnvio;
import com.ecomarket.envioservice.service.EnvioDomainService;
import com.ecomarket.envioservice.service.EnvioService;
import com.ecomarket.envioservice.service.RutaTransporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnvioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("EnvioController")
class EnvioControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean EnvioService envioService;
    @MockitoBean EnvioDomainService envioDomainService;
    @MockitoBean RutaTransporteService rutaTransporteService;

    private ObjectMapper mapper;
    private Envio envio;
    private EstadoEnvio estado;
    private MetodoEnvio metodo;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();

        estado = new EstadoEnvio(1L, "PENDIENTE");
        metodo = new MetodoEnvio(1L, "Domicilio");

        envio = new Envio();
        envio.setId(10L);
        envio.setPedidoId(100L);
        envio.setClienteId(5L);
        envio.setMetodoEnvio(metodo);
        envio.setEstadoActual(estado);
        envio.setDireccionId(1L);
        envio.setCostoEnvio(5000.0);
        envio.setFechaCreacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("POST /api/v1/logistica-envios/envios")
    class CrearEnvio {

        @Test
        @DisplayName("201 Created al crear envio exitosamente")
        void exitoso() throws Exception {
            CrearEnvioRequestDTO dto = new CrearEnvioRequestDTO();
            dto.setPedidoId(100L);
            dto.setClienteId(5L);
            dto.setMetodoEnvioId(1L);
            dto.setDireccionId(1L);

            when(envioService.crearEnvio(100L, 5L, 1L, 1L)).thenReturn(envio);

            mvc.perform(post("/api/v1/logistica-envios/envios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.clienteId").value(5));
        }

        @Test
        @DisplayName("400 si el body es invalido")
        void bodyInvalido() throws Exception {
            CrearEnvioRequestDTO dto = new CrearEnvioRequestDTO();

            mvc.perform(post("/api/v1/logistica-envios/envios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/logistica-envios/envios")
    class ListarEnvios {

        @Test
        @DisplayName("200 OK retorna lista de envios")
        void exitoso() throws Exception {
            when(envioService.listarEnvios(null, null)).thenReturn(List.of(envio));

            mvc.perform(get("/api/v1/logistica-envios/envios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(10));
        }

        @Test
        @DisplayName("filtra por clienteId cuando se pasa el parametro")
        void filtraPorCliente() throws Exception {
            when(envioService.listarEnvios(5L, null)).thenReturn(List.of(envio));

            mvc.perform(get("/api/v1/logistica-envios/envios")
                            .param("clienteId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].clienteId").value(5));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/logistica-envios/envios/{id}")
    class ObtenerEnvio {

        @Test
        @DisplayName("200 OK retorna el envio")
        void exitoso() throws Exception {
            when(envioService.obtenerEnvioPorId(10L)).thenReturn(envio);

            mvc.perform(get("/api/v1/logistica-envios/envios/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/logistica-envios/envios/{id}/estado")
    class ConsultarEstado {

        @Test
        @DisplayName("200 OK retorna el estado del envio")
        void exitoso() throws Exception {
            when(envioService.consultarEstadoEnvio(10L)).thenReturn(estado);

            mvc.perform(get("/api/v1/logistica-envios/envios/10/estado"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("PENDIENTE"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/logistica-envios/envios/{id}/estado")
    class ActualizarEstado {

        @Test
        @DisplayName("200 OK al actualizar estado")
        void exitoso() throws Exception {
            HistorialEnvio historial = new HistorialEnvio();
            historial.setId(1L);
            historial.setEnvioId(10L);
            historial.setObservacion("Cambio a CONFIRMADO");

            ActualizarEstadoRequestDTO dto = new ActualizarEstadoRequestDTO();
            dto.setNuevoEstadoId(2L);
            dto.setObservacion("Cambio a CONFIRMADO");

            when(envioService.actualizarEstado(10L, 2L, "Cambio a CONFIRMADO")).thenReturn(historial);

            mvc.perform(patch("/api/v1/logistica-envios/envios/10/estado")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/logistica-envios/envios/{id}/cancelar")
    class CancelarEnvio {

        @Test
        @DisplayName("200 OK al cancelar envio")
        void exitoso() throws Exception {
            when(envioService.cancelarEnvio(10L)).thenReturn(true);

            mvc.perform(post("/api/v1/logistica-envios/envios/10/cancelar"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value(true));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/logistica-envios/envios/{id}/recepcion")
    class RegistrarRecepcion {

        @Test
        @DisplayName("200 OK al registrar recepcion")
        void exitoso() throws Exception {
            RegistrarRecepcionRequestDTO dto = new RegistrarRecepcionRequestDTO();
            dto.setFirmaRecibe("Firma Juan");

            when(envioService.registrarRecepcion(10L, "Firma Juan")).thenReturn(envio);

            mvc.perform(post("/api/v1/logistica-envios/envios/10/recepcion")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10));
        }

        @Test
        @DisplayName("400 si la firma esta vacia")
        void firmaVacia() throws Exception {
            RegistrarRecepcionRequestDTO dto = new RegistrarRecepcionRequestDTO();

            mvc.perform(post("/api/v1/logistica-envios/envios/10/recepcion")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/logistica-envios/envios/{id}/seleccionar-punto-retiro")
    class SeleccionarPuntoRetiro {

        @Test
        @DisplayName("200 OK al seleccionar punto de retiro")
        void exitoso() throws Exception {
            SeleccionarPuntoRetiroRequestDTO dto = new SeleccionarPuntoRetiroRequestDTO();
            dto.setPuntoRetiroId(1L);
            dto.setFirmaRecibe("Firma Maria");

            when(envioService.seleccionarPuntoRetiro(10L, 1L, "Firma Maria")).thenReturn(envio);

            mvc.perform(post("/api/v1/logistica-envios/envios/10/seleccionar-punto-retiro")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/logistica-envios/envios/{id}/historial")
    class ObtenerHistorial {

        @Test
        @DisplayName("200 OK retorna historial del envio")
        void exitoso() throws Exception {
            HistorialEnvio h = new HistorialEnvio();
            h.setId(1L);
            h.setEnvioId(10L);
            when(envioService.obtenerHistorialEnvio(10L)).thenReturn(List.of(h));

            mvc.perform(get("/api/v1/logistica-envios/envios/10/historial"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/logistica-envios/rutas")
    class PlanificarRuta {

        @Test
        @DisplayName("201 Created al planificar ruta")
        void exitoso() throws Exception {
            PlanificarRutaRequestDTO dto = new PlanificarRutaRequestDTO();
            dto.setTransportistaId(1L);
            dto.setEnviosIds(List.of(10L));

            RutaTransporte ruta = new RutaTransporte();
            ruta.setId(1L);
            ruta.setTransportistaId(1L);

            when(envioService.planificarRuta(1L, List.of(10L))).thenReturn(ruta);

            mvc.perform(post("/api/v1/logistica-envios/rutas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.transportistaId").value(1));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/logistica-envios/rutas")
    class ListarRutas {

        @Test
        @DisplayName("200 OK retorna lista de rutas")
        void exitoso() throws Exception {
            RutaTransporte ruta = new RutaTransporte();
            ruta.setId(1L);
            when(rutaTransporteService.readAll()).thenReturn(List.of(ruta));

            mvc.perform(get("/api/v1/logistica-envios/rutas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/logistica-envios/envios/{id}")
    class EliminarEnvio {

        @Test
        @DisplayName("200 OK al eliminar envio")
        void exitoso() throws Exception {
            doNothing().when(envioDomainService).delete(10L);

            mvc.perform(delete("/api/v1/logistica-envios/envios/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").value("El envio con id 10 ha sido eliminado con exito."));
        }
    }
}
