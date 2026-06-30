package com.ecomarket.pedidos.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnaliticaClient")
class AnaliticaClientTest {

    @Mock private RestTemplate restTemplate;

    private AnaliticaClient client;

    @BeforeEach
    void setUp() {
        client = new AnaliticaClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8084");
    }

    @Test
    @DisplayName("registrarLog envia log sin errores")
    void registrarLogExitoso() {
        Map<String, Object> log = Map.of("accion", "TEST");

        client.registrarLog(log);

        verify(restTemplate).postForEntity("http://localhost:8084/api/analitica/logs", log, String.class);
    }

    @Test
    @DisplayName("registrarLog tolera error sin lanzar excepción")
    void registrarLogToleraError() {
        doThrow(new RuntimeException("Analytics down"))
                .when(restTemplate).postForEntity(anyString(), any(), eq(String.class));

        assertDoesNotThrow(() -> client.registrarLog(Map.of("accion", "TEST")));
    }
}
