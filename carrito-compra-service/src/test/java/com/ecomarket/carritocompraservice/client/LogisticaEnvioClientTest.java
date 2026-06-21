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

import com.ecomarket.carritocompraservice.dto.MetodoEnvioDTO;
import com.ecomarket.carritocompraservice.exception.NoExisteEnBdException;

@ExtendWith(MockitoExtension.class)
class LogisticaEnvioClientTest {

    @Mock private RestTemplate restTemplate;

    private LogisticaEnvioClient client;

    @BeforeEach
    void setUp() {
        client = new LogisticaEnvioClient(restTemplate);
        ReflectionTestUtils.setField(client, "enviosUrl", "http://localhost:8083");
    }

    @Test
    void validarMetodoEnvioReturnsMetodoEnvio() {
        MetodoEnvioDTO expected = new MetodoEnvioDTO(1L, "Envio Express");
        when(restTemplate.getForObject("http://localhost:8083/api/v1/metodo-envio/1", MetodoEnvioDTO.class))
                .thenReturn(expected);

        MetodoEnvioDTO result = client.validarMetodoEnvio(1L);

        assertEquals("Envio Express", result.getNombre());
    }

    @Test
    void validarMetodoEnvioThrowsNoExisteEnBdWhen404() {
        HttpClientErrorException notFound = mock(HttpClientErrorException.class);
        when(notFound.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.NOT_FOUND);
        when(restTemplate.getForObject("http://localhost:8083/api/v1/metodo-envio/99", MetodoEnvioDTO.class))
                .thenThrow(notFound);

        assertThrows(NoExisteEnBdException.class, () -> client.validarMetodoEnvio(99L));
    }

    @Test
    void validarMetodoEnvioThrowsRuntimeOnOtherError() {
        HttpClientErrorException badRequest = mock(HttpClientErrorException.class);
        when(badRequest.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.BAD_REQUEST);
        when(restTemplate.getForObject("http://localhost:8083/api/v1/metodo-envio/99", MetodoEnvioDTO.class))
                .thenThrow(badRequest);

        assertThrows(RuntimeException.class, () -> client.validarMetodoEnvio(99L));
    }
}
