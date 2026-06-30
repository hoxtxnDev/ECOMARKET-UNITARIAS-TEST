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

import com.ecomarket.carritocompraservice.exception.NoExisteEnBdException;

@ExtendWith(MockitoExtension.class)
class RegistroUsuariosClientTest {

    @Mock private RestTemplate restTemplate;

    private RegistroUsuariosClient client;

    @BeforeEach
    void setUp() {
        client = new RegistroUsuariosClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8081");
    }

    @Test
    void validarClienteExitosamente() {
        when(restTemplate.getForEntity("http://localhost:8081/api/usuarios/1", Object.class))
                .thenReturn(null);

        assertDoesNotThrow(() -> client.validarCliente(1L));
    }

    @Test
    void validarClienteLanzaExcepcionCuandoNoExiste() {
        when(restTemplate.getForEntity("http://localhost:8081/api/usuarios/999", Object.class))
                .thenThrow(mock(HttpClientErrorException.class));

        assertThrows(NoExisteEnBdException.class, () -> client.validarCliente(999L));
    }
}
