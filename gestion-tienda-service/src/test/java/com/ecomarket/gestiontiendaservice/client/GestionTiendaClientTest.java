package com.ecomarket.gestiontiendaservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GestionTiendaClientTest {

    @Mock
    RestTemplate restTemplate;

    GestionTiendaClient client;

    @BeforeEach
    void setup() {
        client = new GestionTiendaClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8090");
    }

    @Test
    @DisplayName("notifica stock bajo via POST")
    void notificarStockBajo() {
        client.notificarStockBajo(1L, 100L, 5);

        verify(restTemplate).postForEntity(
                eq("http://localhost:8090/api/gestion-tienda/alertas/stock-bajo"),
                any(Map.class),
                eq(Void.class));
    }
}
