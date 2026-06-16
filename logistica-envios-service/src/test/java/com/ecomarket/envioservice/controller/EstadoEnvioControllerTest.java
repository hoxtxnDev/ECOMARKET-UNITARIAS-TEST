package com.ecomarket.envioservice.controller;

import com.ecomarket.envioservice.model.reference.EstadoEnvio;
import com.ecomarket.envioservice.service.EstadoEnvioService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstadoEnvioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("EstadoEnvioController")
class EstadoEnvioControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean EstadoEnvioService estadoEnvioService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Nested @DisplayName("GET /api/v1/estado-envio")
    class GetAll {
        @Test @DisplayName("200 OK retorna lista")
        void exitoso() throws Exception {
            when(estadoEnvioService.readAll()).thenReturn(List.of(new EstadoEnvio(1L, "PENDIENTE")));
            mvc.perform(get("/api/v1/estado-envio"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre").value("PENDIENTE"));
        }
    }

    @Nested @DisplayName("POST /api/v1/estado-envio")
    class Create {
        @Test @DisplayName("201 Created")
        void exitoso() throws Exception {
            when(estadoEnvioService.create(any())).thenReturn(new EstadoEnvio(1L, "NUEVO"));
            mvc.perform(post("/api/v1/estado-envio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(new EstadoEnvio(null, "NUEVO"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested @DisplayName("DELETE /api/v1/estado-envio/{id}")
    class Delete {
        @Test @DisplayName("200 OK elimina estado")
        void exitoso() throws Exception {
            doNothing().when(estadoEnvioService).delete(1L);
            mvc.perform(delete("/api/v1/estado-envio/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("El estado envio con id 1 ha sido eliminado con exito."));
        }
    }
}
