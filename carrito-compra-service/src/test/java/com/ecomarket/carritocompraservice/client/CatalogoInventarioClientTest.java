package com.ecomarket.carritocompraservice.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.carritocompraservice.dto.ProductoClienteDTO;

@ExtendWith(MockitoExtension.class)
class CatalogoInventarioClientTest {

    @Mock private RestTemplate restTemplate;

    private CatalogoInventarioClient client;

    @BeforeEach
    void setUp() {
        client = new CatalogoInventarioClient(restTemplate);
        ReflectionTestUtils.setField(client, "catalogoUrl", "http://localhost:8087");
    }

    @Test
    void obtenerProductoReturnsProduct() {
        ProductoClienteDTO expected = new ProductoClienteDTO(1L, "SKU-001", "Laptop", 1500.0, null, null);
        when(restTemplate.getForObject("http://localhost:8087/api/catalogo/1", ProductoClienteDTO.class))
                .thenReturn(expected);

        ProductoClienteDTO result = client.obtenerProducto(1L);

        assertEquals("Laptop", result.getNombre());
    }

    @Test
    void obtenerProductoThrowsWhenNotFound() {
        when(restTemplate.getForObject("http://localhost:8087/api/catalogo/999", ProductoClienteDTO.class))
                .thenThrow(mock(HttpClientErrorException.class));

        assertThrows(RuntimeException.class, () -> client.obtenerProducto(999L));
    }
}
