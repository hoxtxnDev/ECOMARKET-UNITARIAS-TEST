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
class ProcesoPagoClientTest {

    @Mock private RestTemplate restTemplate;

    private ProcesoPagoClient client;

    @BeforeEach
    void setUp() {
        client = new ProcesoPagoClient();
        ReflectionTestUtils.setField(client, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(client, "pagosUrl", "http://localhost:8088");
    }

    @Test
    void iniciarPagoReturnsIdWhenSuccess() {
        Map<String, Object> responseBody = Map.of("id", 99);
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseBody);
        when(restTemplate.postForEntity(eq("http://localhost:8088/api/pagos/iniciar?pedidoId=1&clienteId=10&monto=500.0"), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        Long result = client.iniciarPago(1L, 10L, 500.0, 3L);

        assertEquals(99L, result);
    }

    @Test
    void iniciarPagoReturnsNullWhenBodyIsNull() {
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(null);
        when(restTemplate.postForEntity(eq("http://localhost:8088/api/pagos/iniciar?pedidoId=1&clienteId=10&monto=500.0"), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        assertNull(client.iniciarPago(1L, 10L, 500.0, 3L));
    }

    @Test
    void iniciarPagoReturnsNullWhenBodyHasNoId() {
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(Map.of());
        when(restTemplate.postForEntity(eq("http://localhost:8088/api/pagos/iniciar?pedidoId=1&clienteId=10&monto=500.0"), any(), eq(Map.class)))
                .thenReturn(responseEntity);

        assertNull(client.iniciarPago(1L, 10L, 500.0, 3L));
    }

    @Test
    void iniciarPagoReturnsNullOnResourceAccessException() {
        when(restTemplate.postForEntity(eq("http://localhost:8088/api/pagos/iniciar?pedidoId=1&clienteId=10&monto=500.0"), any(), eq(Map.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        assertNull(client.iniciarPago(1L, 10L, 500.0, 3L));
    }

    @Test
    void iniciarPagoReturnsNullOnGenericException() {
        when(restTemplate.postForEntity(eq("http://localhost:8088/api/pagos/iniciar?pedidoId=1&clienteId=10&monto=500.0"), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("error"));

        assertNull(client.iniciarPago(1L, 10L, 500.0, 3L));
    }
}
