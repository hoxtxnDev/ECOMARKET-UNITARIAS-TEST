package com.ecomarket.envioservice.controller;

import com.ecomarket.envioservice.model.entity.Direccion;
import com.ecomarket.envioservice.service.DireccionService;
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

@WebMvcTest(DireccionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("DireccionController")
class DireccionControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean DireccionService direccionService;

    private final ObjectMapper mapper = new ObjectMapper();

    private Direccion direccion(Long id) {
        Direccion d = new Direccion();
        d.setId(id);
        d.setCalle("Av Siempre Viva");
        d.setNumero("123");
        d.setCiudad("Springfield");
        return d;
    }

    @Nested @DisplayName("GET /api/v1/direcciones")
    class GetAll {
        @Test @DisplayName("200 OK retorna lista")
        void exitoso() throws Exception {
            when(direccionService.readAll()).thenReturn(List.of(direccion(1L)));
            mvc.perform(get("/api/v1/direcciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }

    @Nested @DisplayName("GET /api/v1/direcciones/{id}")
    class GetById {
        @Test @DisplayName("200 OK retorna direccion")
        void exitoso() throws Exception {
            when(direccionService.findById(1L)).thenReturn(direccion(1L));
            mvc.perform(get("/api/v1/direcciones/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.calle").value("Av Siempre Viva"));
        }
    }

    @Nested @DisplayName("POST /api/v1/direcciones")
    class Create {
        @Test @DisplayName("201 Created")
        void exitoso() throws Exception {
            Direccion d = direccion(1L);
            when(direccionService.create(any())).thenReturn(d);
            mvc.perform(post("/api/v1/direcciones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(d)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested @DisplayName("PUT /api/v1/direcciones/{id}")
    class Update {
        @Test @DisplayName("200 OK actualiza direccion")
        void exitoso() throws Exception {
            Direccion d = direccion(1L);
            when(direccionService.update(eq(1L), any())).thenReturn(d);
            mvc.perform(put("/api/v1/direcciones/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(d)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested @DisplayName("DELETE /api/v1/direcciones/{id}")
    class Delete {
        @Test @DisplayName("200 OK elimina direccion")
        void exitoso() throws Exception {
            doNothing().when(direccionService).delete(1L);
            mvc.perform(delete("/api/v1/direcciones/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("La direccion con id 1 ha sido eliminada con exito."));
        }
    }
}
