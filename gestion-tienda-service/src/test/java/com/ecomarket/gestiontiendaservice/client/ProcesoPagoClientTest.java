package com.ecomarket.gestiontiendaservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcesoPagoClientTest {

    @Mock
    RestClient restClient;
    @Mock
    RestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    RestClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    RestClient.ResponseSpec responseSpec;

    @Captor
    ArgumentCaptor<RestClient.ResponseSpec.ErrorHandler> errorHandlerCaptor;

    ProcesoPagoClient client;

    @BeforeEach
    void setup() {
        client = new ProcesoPagoClient(restClient);
        ReflectionTestUtils.setField(client, "procesoPagoUrl", "http://localhost:8087");
        lenient().when(restClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    }

    @Nested
    @DisplayName("obtenerTransaccionesPorCliente")
    class ObtenerTransaccionesPorCliente {

        @Test
        @DisplayName("retorna lista de transacciones del cliente")
        void exito() {
            TransaccionResumenDTO t = new TransaccionResumenDTO();
            t.setId(1L);
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of(t));

            List<TransaccionResumenDTO> resultado = client.obtenerTransaccionesPorCliente(10L);

            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("lanza RuntimeException cuando falla la llamada")
        void error() {
            when(responseSpec.body(any(ParameterizedTypeReference.class)))
                    .thenThrow(new RuntimeException("Error externo"));

            assertThatThrownBy(() -> client.obtenerTransaccionesPorCliente(10L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No se pudo obtener las transacciones");
        }

        @Test
        @DisplayName("el error handler lanza RuntimeException con el id del cliente")
        void errorHandlerLanzaExcepcion() throws IOException {
            when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(List.of());
            client.obtenerTransaccionesPorCliente(10L);
            verify(responseSpec).onStatus(any(), errorHandlerCaptor.capture());

            assertThatThrownBy(() -> errorHandlerCaptor.getValue().handle(
                    mock(HttpRequest.class), mock(ClientHttpResponse.class)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("10");
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
            when(responseSpec.body(eq(TransaccionResumenDTO.class))).thenReturn(t);

            TransaccionResumenDTO resultado = client.obtenerTransaccion(5L);

            assertThat(resultado.getId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("lanza RuntimeException cuando falla la llamada")
        void error() {
            when(responseSpec.body(eq(TransaccionResumenDTO.class)))
                    .thenThrow(new RuntimeException("Error externo"));

            assertThatThrownBy(() -> client.obtenerTransaccion(5L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No se pudo obtener la transacción");
        }

        @Test
        @DisplayName("el error handler lanza RuntimeException con el id de transaccion")
        void errorHandlerLanzaExcepcion() throws IOException {
            when(responseSpec.body(eq(TransaccionResumenDTO.class))).thenReturn(new TransaccionResumenDTO());
            client.obtenerTransaccion(5L);
            verify(responseSpec).onStatus(any(), errorHandlerCaptor.capture());

            assertThatThrownBy(() -> errorHandlerCaptor.getValue().handle(
                    mock(HttpRequest.class), mock(ClientHttpResponse.class)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("5");
        }
    }
}
