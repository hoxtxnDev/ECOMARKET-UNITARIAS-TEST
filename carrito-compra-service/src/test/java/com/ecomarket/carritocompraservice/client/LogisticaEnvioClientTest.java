package com.ecomarket.carritocompraservice.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class LogisticaEnvioClientTest {

    @Mock private RestTemplate restTemplate;

    private LogisticaEnvioClient client;

    @BeforeEach
    void setUp() {
        client = new LogisticaEnvioClient();
        ReflectionTestUtils.setField(client, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(client, "enviosUrl", "http://localhost:8083");
    }

    @Test
    void crearEnvioReturnsIdWhenSuccess() {
        Map<String, Object> responseBody = Map.of("id", 42);
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseBody);
        when(restTemplate.postForEntity(eq("http://localhost:8083/api/v1/logistica-envios/envios"), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        Long result = client.crearEnvio(1L, 10L, 2L, 3L);

        assertEquals(42L, result);
    }

    @Test
    void crearEnvioReturnsNullWhenBodyIsNull() {
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(null);
        when(restTemplate.postForEntity(eq("http://localhost:8083/api/v1/logistica-envios/envios"), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        assertNull(client.crearEnvio(1L, 10L, 2L, 3L));
    }

    @Test
    void crearEnvioReturnsNullWhenBodyHasNoId() {
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(Map.of());
        when(restTemplate.postForEntity(eq("http://localhost:8083/api/v1/logistica-envios/envios"), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        assertNull(client.crearEnvio(1L, 10L, 2L, 3L));
    }

    @Test
    void crearEnvioReturnsNullOnResourceAccessException() {
        when(restTemplate.postForEntity(eq("http://localhost:8083/api/v1/logistica-envios/envios"), any(), eq(Map.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        assertNull(client.crearEnvio(1L, 10L, 2L, 3L));
    }

    @Test
    void crearEnvioReturnsNullOnGenericException() {
        when(restTemplate.postForEntity(eq("http://localhost:8083/api/v1/logistica-envios/envios"), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("error"));

        assertNull(client.crearEnvio(1L, 10L, 2L, 3L));
    }
}
