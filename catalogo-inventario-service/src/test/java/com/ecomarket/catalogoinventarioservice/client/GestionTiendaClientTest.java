package com.ecomarket.catalogoinventarioservice.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.catalogoinventarioservice.dto.SucursalDTO;

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

        String expectedUrl = "http://localhost:8090/api/tienda/alertas/stock-bajo";
        Map<String, Object> expectedBody = Map.of(
                "sucursalId", 1L,
                "productoId", 2L,
                "stockActual", 5
        );

        verify(restTemplate).postForEntity(expectedUrl, expectedBody, Void.class);
    }

    @Test
    void obtenerSucursalReturnsBody() {
        SucursalDTO sucursal = new SucursalDTO();
        sucursal.setId(10L);
        sucursal.setNombre("Centro");
        ResponseEntity<SucursalDTO> responseEntity = ResponseEntity.ok(sucursal);

        when(restTemplate.getForEntity("http://localhost:8090/api/tienda/sucursal/10", SucursalDTO.class))
                .thenReturn(responseEntity);

        SucursalDTO result = client.obtenerSucursal(10L);

        assertEquals(10L, result.getId());
        assertEquals("Centro", result.getNombre());
    }

    @Test
    void obtenerSucursalReturnsNullWhenBodyIsNull() {
        ResponseEntity<SucursalDTO> responseEntity = ResponseEntity.ok(null);

        when(restTemplate.getForEntity("http://localhost:8090/api/tienda/sucursal/99", SucursalDTO.class))
                .thenReturn(responseEntity);

        assertNull(client.obtenerSucursal(99L));
    }

}
