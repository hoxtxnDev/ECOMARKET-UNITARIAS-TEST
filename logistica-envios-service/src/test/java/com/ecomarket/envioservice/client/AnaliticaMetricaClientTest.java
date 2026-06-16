package com.ecomarket.envioservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnaliticaMetricaClient")
class AnaliticaMetricaClientTest {

    @Mock RestTemplate restTemplate;
    @InjectMocks AnaliticaMetricaClient client;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(client, "analiticaUrl", "http://localhost:8086");
    }

    @Nested @DisplayName("registrarMetrica")
    class Registrar {
        @Test @DisplayName("registra metrica exitosamente")
        void exitoso() {
            client.registrarMetrica("envios.creados", 1.0, "test");
            verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
        }

        @Test @DisplayName("maneja ResourceAccessException")
        void resourceAccessException() {
            when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                    .thenThrow(new ResourceAccessException("timeout"));
            client.registrarMetrica("envios.creados", 1.0, "test");
        }

        @Test @DisplayName("maneja Exception generica")
        void genericException() {
            when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                    .thenThrow(new RuntimeException("error"));
            client.registrarMetrica("envios.creados", 1.0, "test");
        }
    }
}
