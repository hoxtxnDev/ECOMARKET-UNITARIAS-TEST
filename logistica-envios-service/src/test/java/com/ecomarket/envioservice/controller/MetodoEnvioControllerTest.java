package com.ecomarket.envioservice.controller;

import com.ecomarket.envioservice.model.reference.MetodoEnvio;
import com.ecomarket.envioservice.service.MetodoEnvioService;
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

@WebMvcTest(MetodoEnvioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("MetodoEnvioController")
class MetodoEnvioControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean MetodoEnvioService metodoEnvioService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Nested @DisplayName("GET /api/v1/metodo-envio")
    class GetAll {
        @Test @DisplayName("200 OK retorna lista")
        void exitoso() throws Exception {
            when(metodoEnvioService.readAll()).thenReturn(List.of(new MetodoEnvio(1L, "Domicilio")));
            mvc.perform(get("/api/v1/metodo-envio"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nombre").value("Domicilio"));
        }
    }

    @Nested @DisplayName("POST /api/v1/metodo-envio")
    class Create {
        @Test @DisplayName("201 Created")
        void exitoso() throws Exception {
            when(metodoEnvioService.create(any())).thenReturn(new MetodoEnvio(1L, "Express"));
            mvc.perform(post("/api/v1/metodo-envio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(new MetodoEnvio(null, "Express"))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested @DisplayName("DELETE /api/v1/metodo-envio/{id}")
    class Delete {
        @Test @DisplayName("200 OK elimina metodo")
        void exitoso() throws Exception {
            doNothing().when(metodoEnvioService).delete(1L);
            mvc.perform(delete("/api/v1/metodo-envio/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("El metodo envio con id 1 ha sido eliminado con exito."));
        }
    }
}
