package com.ecomarket.pedidos.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.pedidos.dto.ProductoDTO;
import com.ecomarket.pedidos.exception.NoExisteEnBdException;

@ExtendWith(MockitoExtension.class)
@DisplayName("CatalogoInventarioClient")
class CatalogoInventarioClientTest {

    @Mock private RestTemplate restTemplate;

    private CatalogoInventarioClient client;

    @BeforeEach
    void setUp() {
        client = new CatalogoInventarioClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8087");
    }

    @Test
    @DisplayName("obtenerProducto retorna producto cuando existe")
    void obtenerProductoExitoso() {
        ProductoDTO expected = new ProductoDTO();
        when(restTemplate.getForEntity("http://localhost:8087/api/catalogo/1", ProductoDTO.class))
                .thenReturn(ResponseEntity.ok(expected));

        ProductoDTO result = client.obtenerProducto(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("obtenerProducto lanza NoExisteEnBdException cuando falla")
    void obtenerProductoNoExiste() {
        when(restTemplate.getForEntity("http://localhost:8087/api/catalogo/999", ProductoDTO.class))
                .thenThrow(mock(HttpClientErrorException.class));

        assertThrows(NoExisteEnBdException.class, () -> client.obtenerProducto(999L));
    }
}
