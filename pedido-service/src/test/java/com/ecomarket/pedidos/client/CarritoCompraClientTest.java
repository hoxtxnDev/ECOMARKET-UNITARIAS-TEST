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
            when(restTemplate.getForEntity("http://localhost:8082/api/carrito/5", CarritoDTO.class))
                    .thenReturn(ResponseEntity.ok(expected));

            CarritoDTO result = client.obtenerCarrito(5L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando falla la llamada")
        void cuandoFalla() {
            when(restTemplate.getForEntity("http://localhost:8082/api/carrito/999", CarritoDTO.class))
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

            verify(restTemplate).delete("http://localhost:8082/api/carrito/5/vaciar");
        }
    }

    @Nested
    @DisplayName("cerrarCarrito")
    class CerrarCarrito {

        @Test
        @DisplayName("llama a put sin errores")
        void exitoso() {
            client.cerrarCarrito(5L);

            verify(restTemplate).put("http://localhost:8082/api/carrito/5/cerrar", null);
        }

        @Test
        @DisplayName("tolera error y no lanza excepción")
        void toleraError() {
            doThrow(new RuntimeException("Connection refused")).when(restTemplate).put(anyString(), any());

            assertDoesNotThrow(() -> client.cerrarCarrito(5L));
        }
    }
}
