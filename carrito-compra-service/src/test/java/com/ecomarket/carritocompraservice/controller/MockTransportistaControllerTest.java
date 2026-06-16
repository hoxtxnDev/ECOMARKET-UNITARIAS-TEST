package com.ecomarket.carritocompraservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MockTransportistaController.class)
class MockTransportistaControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void obtenerTransportistaReturnsMockData() throws Exception {
        mockMvc.perform(get("/api/mock/transportistas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.nombre").value("Transportista"))
                .andExpect(jsonPath("$.correo").value("transportista1@ecomarket.cl"));
    }
}
