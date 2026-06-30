package com.ecomarket.envioservice.controller;

import com.ecomarket.envioservice.exception.GlobalExceptionHandler;
import com.ecomarket.envioservice.model.reference.EstadoEnvio;
import com.ecomarket.envioservice.service.EstadoEnvioService;
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
@DisplayName("EstadoEnvioController")
class EstadoEnvioControllerTest {

    @Mock EstadoEnvioService estadoEnvioService;

    MockMvc mvc;
    ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        var controller = new EstadoEnvioController(estadoEnvioService);
        mvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested @DisplayName("GET /api/v1/logistica-envios/estado-envio")
    class GetAll {
        @Test @DisplayName("200 OK retorna lista")
        void exitoso() throws Exception {
            when(estadoEnvioService.readAll()).thenReturn(List.of(new EstadoEnvio(1L, "PENDIENTE")));
            mvc.perform(get("/api/v1/logistica-envios/estado-envio"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre").value("PENDIENTE"));
        }
    }

    @Nested @DisplayName("POST /api/v1/logistica-envios/estado-envio")
    class Create {
        @Test @DisplayName("201 Created")
        void exitoso() throws Exception {
            when(estadoEnvioService.create(any())).thenReturn(new EstadoEnvio(1L, "NUEVO"));
            mvc.perform(post("/api/v1/logistica-envios/estado-envio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(new EstadoEnvio(null, "NUEVO"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested @DisplayName("DELETE /api/v1/logistica-envios/estado-envio/{id}")
    class Delete {
        @Test @DisplayName("200 OK elimina estado")
        void exitoso() throws Exception {
            doNothing().when(estadoEnvioService).delete(1L);
            mvc.perform(delete("/api/v1/logistica-envios/estado-envio/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("El estado envio con id 1 ha sido eliminado con exito."));
        }
    }
}
