package com.ecomarket.pedidos.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.pedidos.dto.CarritoDTO;
import com.ecomarket.pedidos.dto.ItemCarritoDTO;
import com.ecomarket.pedidos.exception.NoExisteEnBdException;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarritoCompraClient")
class CarritoCompraClientTest {

    @Mock private RestTemplate restTemplate;

    private CarritoCompraClient client;

    @BeforeEach
    void setUp() {
        client = new CarritoCompraClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8082");
    }

    @Nested
    @DisplayName("obtenerCarrito")
    class ObtenerCarrito {

        @Test
        @DisplayName("retorna CarritoDTO cuando la llamada es exitosa")
        void exitoso() {
            CarritoDTO expected = CarritoDTO.builder()
                    .id(1L).clienteId(5L).subtotal(50000.0)
                    .items(List.of(ItemCarritoDTO.builder().productoId(100L).cantidad(2).precioUnitarioAgregado(25000.0).build()))
                    .build();
            when(restTemplate.exchange(
                    eq("http://localhost:8082/api/carrito/activo"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(CarritoDTO.class)))
                    .thenReturn(ResponseEntity.ok(expected));

            CarritoDTO result = client.obtenerCarrito(5L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando falla la llamada")
        void cuandoFalla() {
            when(restTemplate.exchange(
                    eq("http://localhost:8082/api/carrito/activo"),
                    eq(HttpMethod.GET),
                    any(HttpEntity.class),
                    eq(CarritoDTO.class)))
                    .thenThrow(mock(HttpClientErrorException.class));

            assertThrows(NoExisteEnBdException.class, () -> client.obtenerCarrito(999L));
        }
    }

    @Nested
    @DisplayName("vaciarCarrito")
    class VaciarCarrito {

        @Test
        @DisplayName("llama a delete sin errores")
        void exitoso() {
            client.vaciarCarrito(5L);

            verify(restTemplate).exchange(
                    eq("http://localhost:8082/api/carrito/vaciar"),
                    eq(HttpMethod.DELETE),
                    any(HttpEntity.class),
                    eq(Void.class));
        }
    }

    @Nested
    @DisplayName("cerrarCarrito")
    class CerrarCarrito {

        @Test
        @DisplayName("llama a put sin errores")
        void exitoso() {
            client.cerrarCarrito(5L);

            verify(restTemplate).exchange(
                    eq("http://localhost:8082/api/carrito/cerrar"),
                    eq(HttpMethod.PUT),
                    any(HttpEntity.class),
                    eq(Void.class));
        }

        @Test
        @DisplayName("tolera error y no lanza excepción")
        void toleraError() {
            doThrow(new RuntimeException("Connection refused"))
                    .when(restTemplate)
                    .exchange(
                            eq("http://localhost:8082/api/carrito/cerrar"),
                            eq(HttpMethod.PUT),
                            any(HttpEntity.class),
                            eq(Void.class));

            assertDoesNotThrow(() -> client.cerrarCarrito(5L));
        }
    }
}
