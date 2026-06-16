package com.ecomarket.carritocompraservice.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        client = new CatalogoInventarioClient();
        ReflectionTestUtils.setField(client, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(client, "catalogoUrl", "http://localhost:8087");
        ReflectionTestUtils.setField(client, "inventarioUrl", "http://localhost:8087");
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

    @Test
    void verificarDisponibilidadReturnsTrueWhenAvailable() {
        when(restTemplate.getForObject("http://localhost:8087/api/inventario/disponibilidad/1?cantidad=2", Boolean.class))
                .thenReturn(true);

        assertTrue(client.verificarDisponibilidad(1L, 2));
    }

    @Test
    void verificarDisponibilidadReturnsFalseWhenNotAvailable() {
        when(restTemplate.getForObject("http://localhost:8087/api/inventario/disponibilidad/1?cantidad=2", Boolean.class))
                .thenReturn(false);

        assertFalse(client.verificarDisponibilidad(1L, 2));
    }

    @Test
    void verificarDisponibilidadReturnsFalseOnError() {
        when(restTemplate.getForObject("http://localhost:8087/api/inventario/disponibilidad/1?cantidad=2", Boolean.class))
                .thenThrow(mock(HttpClientErrorException.class));

        assertFalse(client.verificarDisponibilidad(1L, 2));
    }

    @Test
    void reservarStockReturnsTrueWhenSuccess() {
        when(restTemplate.postForObject("http://localhost:8087/api/inventario/reservar/1?cantidad=2", null, Boolean.class))
                .thenReturn(true);

        assertTrue(client.reservarStock(1L, 2));
    }

    @Test
    void reservarStockReturnsFalseOnError() {
        when(restTemplate.postForObject("http://localhost:8087/api/inventario/reservar/1?cantidad=2", null, Boolean.class))
                .thenThrow(mock(HttpClientErrorException.class));

        assertFalse(client.reservarStock(1L, 2));
    }

    @Test
    void liberarStockReturnsTrueWhenSuccess() {
        when(restTemplate.postForObject("http://localhost:8087/api/inventario/liberar/1?cantidad=2", null, Boolean.class))
                .thenReturn(true);

        assertTrue(client.liberarStock(1L, 2));
    }

    @Test
    void liberarStockReturnsFalseOnError() {
        when(restTemplate.postForObject("http://localhost:8087/api/inventario/liberar/1?cantidad=2", null, Boolean.class))
                .thenThrow(mock(HttpClientErrorException.class));

        assertFalse(client.liberarStock(1L, 2));
    }
}
