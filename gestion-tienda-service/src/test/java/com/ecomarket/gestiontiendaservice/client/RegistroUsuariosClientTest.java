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
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroUsuariosClientTest {

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

    RegistroUsuariosClient client;

    @BeforeEach
    void setup() {
        client = new RegistroUsuariosClient(restClient);
        ReflectionTestUtils.setField(client, "registroUsuariosUrl", "http://localhost:8085");
        lenient().when(restClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    }

    @Nested
    @DisplayName("obtenerEmpleado")
    class ObtenerEmpleado {

        @Test
        @DisplayName("retorna empleado cuando existe")
        void exito() {
            EmpleadoDTO emp = new EmpleadoDTO();
            emp.setId(1L);
            emp.setNombre("Juan");
            when(responseSpec.body(eq(EmpleadoDTO.class))).thenReturn(emp);

            EmpleadoDTO resultado = client.obtenerEmpleado(1L);

            assertThat(resultado.getNombre()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("lanza RuntimeException cuando falla la llamada")
        void error() {
            when(responseSpec.body(eq(EmpleadoDTO.class)))
                    .thenThrow(new RuntimeException("Error externo"));

            assertThatThrownBy(() -> client.obtenerEmpleado(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No se pudo obtener el empleado");
        }

        @Test
        @DisplayName("el error handler lanza RuntimeException con el id del empleado")
        void errorHandlerLanzaExcepcion() throws IOException {
            when(responseSpec.body(eq(EmpleadoDTO.class))).thenReturn(new EmpleadoDTO());
            client.obtenerEmpleado(1L);
            verify(responseSpec).onStatus(any(), errorHandlerCaptor.capture());

            assertThatThrownBy(() -> errorHandlerCaptor.getValue().handle(
                    mock(HttpRequest.class), mock(ClientHttpResponse.class)))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("1");
        }
    }

    @Nested
    @DisplayName("empleadoExiste")
    class EmpleadoExiste {

        @Test
        @DisplayName("retorna true cuando empleado existe")
        void existe() {
            EmpleadoDTO emp = new EmpleadoDTO();
            emp.setId(1L);
            when(responseSpec.body(eq(EmpleadoDTO.class))).thenReturn(emp);

            boolean resultado = client.empleadoExiste(1L);

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("retorna false cuando obtenerEmpleado lanza excepcion")
        void noExistePorError() {
            when(responseSpec.body(eq(EmpleadoDTO.class)))
                    .thenThrow(new RuntimeException("Error"));

            boolean resultado = client.empleadoExiste(1L);

            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("retorna false cuando obtenerEmpleado retorna null")
        void noExistePorNull() {
            when(responseSpec.body(eq(EmpleadoDTO.class))).thenReturn(null);

            boolean resultado = client.empleadoExiste(1L);

            assertThat(resultado).isFalse();
        }
    }
}
