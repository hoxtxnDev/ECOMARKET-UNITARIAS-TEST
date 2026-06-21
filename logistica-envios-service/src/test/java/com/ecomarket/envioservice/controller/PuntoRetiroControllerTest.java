package com.ecomarket.envioservice.controller;

import com.ecomarket.envioservice.exception.GlobalExceptionHandler;
import com.ecomarket.envioservice.model.entity.PuntoRetiro;
import com.ecomarket.envioservice.service.PuntoRetiroService;
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
@DisplayName("PuntoRetiroController")
class PuntoRetiroControllerTest {

    @Mock PuntoRetiroService puntoRetiroService;

    MockMvc mvc;
    ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        var controller = new PuntoRetiroController(puntoRetiroService);
        mvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private PuntoRetiro punto(Long id) {
        PuntoRetiro p = new PuntoRetiro();
        p.setId(id);
        p.setNombre("Retiro " + id);
        p.setActivo(true);
        p.setCapacidadActual(5);
        p.setCapacidadMaxima(10);
        return p;
    }

    @Nested @DisplayName("GET /api/v1/logistica-envios/puntos-retiro")
    class GetAll {
        @Test @DisplayName("200 OK retorna lista")
        void exitoso() throws Exception {
            when(puntoRetiroService.readAll()).thenReturn(List.of(punto(1L)));
            mvc.perform(get("/api/v1/logistica-envios/puntos-retiro"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }

    @Nested @DisplayName("GET /api/v1/logistica-envios/puntos-retiro/activos")
    class GetActivos {
        @Test @DisplayName("200 OK retorna activos")
        void exitoso() throws Exception {
            when(puntoRetiroService.readActivos()).thenReturn(List.of(punto(1L)));
            mvc.perform(get("/api/v1/logistica-envios/puntos-retiro/activos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1));
        }
    }

    @Nested @DisplayName("GET /api/v1/logistica-envios/puntos-retiro/{id}")
    class GetById {
        @Test @DisplayName("200 OK retorna punto")
        void exitoso() throws Exception {
            when(puntoRetiroService.findById(1L)).thenReturn(punto(1L));
            mvc.perform(get("/api/v1/logistica-envios/puntos-retiro/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("Retiro 1"));
        }
    }

    @Nested @DisplayName("POST /api/v1/logistica-envios/puntos-retiro")
    class Create {
        @Test @DisplayName("201 Created")
        void exitoso() throws Exception {
            when(puntoRetiroService.create(any())).thenReturn(punto(1L));
            mvc.perform(post("/api/v1/logistica-envios/puntos-retiro")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(punto(null))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested @DisplayName("PUT /api/v1/logistica-envios/puntos-retiro/{id}")
    class Update {
        @Test @DisplayName("200 OK actualiza punto")
        void exitoso() throws Exception {
            when(puntoRetiroService.update(eq(1L), any())).thenReturn(punto(1L));
            mvc.perform(put("/api/v1/logistica-envios/puntos-retiro/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(punto(1L))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested @DisplayName("DELETE /api/v1/logistica-envios/puntos-retiro/{id}")
    class Delete {
        @Test @DisplayName("200 OK elimina punto")
        void exitoso() throws Exception {
            doNothing().when(puntoRetiroService).delete(1L);
            mvc.perform(delete("/api/v1/logistica-envios/puntos-retiro/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("El punto de retiro con id 1 ha sido eliminado con exito."));
        }
    }
}
