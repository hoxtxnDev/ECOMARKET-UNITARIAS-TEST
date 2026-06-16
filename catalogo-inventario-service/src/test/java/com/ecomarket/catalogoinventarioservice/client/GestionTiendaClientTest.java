package com.ecomarket.catalogoinventarioservice.client;

import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GestionTiendaClientTest {

    @Mock private RestTemplate restTemplate;

    private GestionTiendaClient client;

    @BeforeEach
    void setUp() {
        client = new GestionTiendaClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8090");
    }

    @Test
    void notificarStockBajoSendsPostRequest() {
        client.notificarStockBajo(1L, 2L, 5);

        String expectedUrl = "http://localhost:8090/api/gestion-tienda/alertas/stock-bajo";
        Map<String, Object> expectedBody = Map.of(
                "sucursalId", 1L,
                "productoId", 2L,
                "stockActual", 5
        );

        verify(restTemplate).postForEntity(expectedUrl, expectedBody, Void.class);
    }

}
