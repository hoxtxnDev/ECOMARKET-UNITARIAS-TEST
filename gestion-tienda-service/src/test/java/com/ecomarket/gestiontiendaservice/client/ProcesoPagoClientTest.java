package com.ecomarket.gestiontiendaservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcesoPagoClientTest {

    @Mock
    RestTemplate restTemplate;

    ProcesoPagoClient client;

    @BeforeEach
    void setup() {
        client = new ProcesoPagoClient(restTemplate);
        ReflectionTestUtils.setField(client, "procesoPagoUrl", "http://localhost:8087");
    }

    @Nested
    @DisplayName("obtenerTransaccionesPorCliente")
    class ObtenerTransaccionesPorCliente {

        @Test
        @DisplayName("retorna lista de transacciones del cliente")
        void exito() {
            TransaccionResumenDTO t = new TransaccionResumenDTO();
            t.setId(1L);
            when(restTemplate.getForObject(
                    "http://localhost:8087/api/pagos/cliente/10",
                    TransaccionResumenDTO[].class))
                    .thenReturn(new TransaccionResumenDTO[]{t});

            var resultado = client.obtenerTransaccionesPorCliente(10L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("retorna lista vacia cuando el servicio retorna null")
        void retornaNull() {
            when(restTemplate.getForObject(
                    "http://localhost:8087/api/pagos/cliente/10",
                    TransaccionResumenDTO[].class))
                    .thenReturn(null);

            var resultado = client.obtenerTransaccionesPorCliente(10L);

            assertThat(resultado).isEmpty();
        }

        @Test
        @DisplayName("lanza RuntimeException cuando falla la llamada")
        void error() {
            when(restTemplate.getForObject(
                    "http://localhost:8087/api/pagos/cliente/10",
                    TransaccionResumenDTO[].class))
                    .thenThrow(new RuntimeException("Error externo"));

            assertThatThrownBy(() -> client.obtenerTransaccionesPorCliente(10L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No se pudo obtener las transacciones");
        }
    }

    @Nested
    @DisplayName("obtenerTransaccion")
    class ObtenerTransaccion {

        @Test
        @DisplayName("retorna transaccion por id")
        void exito() {
            TransaccionResumenDTO t = new TransaccionResumenDTO();
            t.setId(5L);
            when(restTemplate.getForObject(
                    "http://localhost:8087/api/pagos/5",
                    TransaccionResumenDTO.class))
                    .thenReturn(t);

            TransaccionResumenDTO resultado = client.obtenerTransaccion(5L);

            assertThat(resultado.getId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("lanza RuntimeException cuando falla la llamada")
        void error() {
            when(restTemplate.getForObject(
                    "http://localhost:8087/api/pagos/5",
                    TransaccionResumenDTO.class))
                    .thenThrow(new RuntimeException("Error externo"));

            assertThatThrownBy(() -> client.obtenerTransaccion(5L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No se pudo obtener la transacción");
        }
    }
}
