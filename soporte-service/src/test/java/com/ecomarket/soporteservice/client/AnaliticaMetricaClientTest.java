package com.ecomarket.soporteservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AnaliticaMetricaClientTest {

    private AnaliticaMetricaClient client;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        client = new AnaliticaMetricaClient(restTemplate);
        ReflectionTestUtils.setField(client, "analiticaUrl", "http://localhost:9090");
    }

    @Test
    @DisplayName("registrarMetrica exitoso → llama al restTemplate")
    void registrarMetricaExitoso() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok("ok"));

        client.registrarMetrica("TICKETS_CREADOS", 1.0, "ticket");

        verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("registrarMetrica → ResourceAccessException se loguea como warn")
    void registrarMetricaServicioNoDisponible() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new ResourceAccessException("Connection refused"));

        client.registrarMetrica("TICKETS_CREADOS", 1.0, "ticket");

        verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("registrarMetrica → Exception genérica se loguea como error")
    void registrarMetricaErrorGenerico() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Error inesperado"));

        client.registrarMetrica("TICKETS_CREADOS", 1.0, "ticket");

        verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
    }
}
