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
@DisplayName("SoporteNotificacionClient")
class SoporteNotificacionClientTest {

    @Mock RestTemplate restTemplate;
    @InjectMocks SoporteNotificacionClient client;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(client, "soporteUrl", "http://localhost:8085");
    }

    @Nested @DisplayName("notificarCreacionEnvio")
    class Notificar {
        @Test @DisplayName("envia notificacion exitosamente")
        void exitoso() {
            client.notificarCreacionEnvio(1L, 100L, 10L);
            verify(restTemplate).postForEntity(anyString(), any(), eq(String.class));
        }

        @Test @DisplayName("maneja ResourceAccessException")
        void resourceAccessException() {
            when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                    .thenThrow(new ResourceAccessException("timeout"));
            client.notificarCreacionEnvio(1L, 100L, 10L);
        }

        @Test @DisplayName("maneja Exception generica")
        void genericException() {
            when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                    .thenThrow(new RuntimeException("error"));
            client.notificarCreacionEnvio(1L, 100L, 10L);
        }
    }
}
