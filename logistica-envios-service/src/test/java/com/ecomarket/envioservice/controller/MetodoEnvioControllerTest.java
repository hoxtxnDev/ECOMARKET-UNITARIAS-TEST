package com.ecomarket.envioservice.controller;

import com.ecomarket.envioservice.exception.GlobalExceptionHandler;
import com.ecomarket.envioservice.model.reference.MetodoEnvio;
import com.ecomarket.envioservice.service.MetodoEnvioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
@DisplayName("MetodoEnvioController")
class MetodoEnvioControllerTest {

    @Mock MetodoEnvioService metodoEnvioService;

    MockMvc mvc;
    ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        var controller = new MetodoEnvioController(metodoEnvioService);
        mvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested @DisplayName("GET /api/v1/logistica-envios/metodo-envio")
    class GetAll {
        @Test @DisplayName("200 OK retorna lista")
        void exitoso() throws Exception {
            when(metodoEnvioService.readAll()).thenReturn(List.of(new MetodoEnvio(1L, "Domicilio", 0.0)));
            mvc.perform(get("/api/v1/logistica-envios/metodo-envio"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre").value("Domicilio"));
        }
    }

    @Nested @DisplayName("POST /api/v1/logistica-envios/metodo-envio")
    class Create {
        @Test @DisplayName("201 Created")
        void exitoso() throws Exception {
            when(metodoEnvioService.create(any())).thenReturn(new MetodoEnvio(1L, "Express", 0.0));
            mvc.perform(post("/api/v1/logistica-envios/metodo-envio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(new MetodoEnvio(null, "Express", 0.0))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested @DisplayName("DELETE /api/v1/logistica-envios/metodo-envio/{id}")
    class Delete {
        @Test @DisplayName("200 OK elimina metodo")
        void exitoso() throws Exception {
            doNothing().when(metodoEnvioService).delete(1L);
            mvc.perform(delete("/api/v1/logistica-envios/metodo-envio/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("El metodo envio con id 1 ha sido eliminado con exito."));
        }
    }

    @Nested @DisplayName("GET /api/v1/logistica-envios/metodo-envio/{id}")
    class GetById {
        @Test @DisplayName("200 OK retorna metodo envio por id")
        void exitoso() throws Exception {
            when(metodoEnvioService.findById(1L)).thenReturn(new MetodoEnvio(1L, "Domicilio", 5000.0));
            mvc.perform(get("/api/v1/logistica-envios/metodo-envio/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("Domicilio"));
        }
    }

    @Nested @DisplayName("PUT /api/v1/logistica-envios/metodo-envio/{id}/costo")
    class ActualizarCosto {
        @Test @DisplayName("200 OK actualiza costo")
        void exitoso() throws Exception {
            when(metodoEnvioService.actualizarCosto(1L, 6000.0)).thenReturn(new MetodoEnvio(1L, "Domicilio", 6000.0));
            mvc.perform(put("/api/v1/logistica-envios/metodo-envio/1/costo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(6000.0)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.costo").value(6000.0));
        }
    }
}
