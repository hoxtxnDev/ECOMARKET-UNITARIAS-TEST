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

import com.ecomarket.carritocompraservice.dto.MetodoPagoDTO;
import com.ecomarket.carritocompraservice.exception.NoExisteEnBdException;

@ExtendWith(MockitoExtension.class)
class ProcesoPagoClientTest {

    @Mock private RestTemplate restTemplate;

    private ProcesoPagoClient client;

    @BeforeEach
    void setUp() {
        client = new ProcesoPagoClient(restTemplate);
        ReflectionTestUtils.setField(client, "pagosUrl", "http://localhost:8088");
    }

    @Test
    void validarMetodoPagoReturnsMetodoPago() {
        MetodoPagoDTO expected = new MetodoPagoDTO(1L, "Tarjeta Credito");
        when(restTemplate.getForObject("http://localhost:8088/api/metodo-pago/1", MetodoPagoDTO.class))
                .thenReturn(expected);

        MetodoPagoDTO result = client.validarMetodoPago(1L);

        assertEquals("Tarjeta Credito", result.getNombre());
    }

    @Test
    void validarMetodoPagoThrowsNoExisteEnBdWhen404() {
        HttpClientErrorException notFound = mock(HttpClientErrorException.class);
        when(notFound.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.NOT_FOUND);
        when(restTemplate.getForObject("http://localhost:8088/api/metodo-pago/99", MetodoPagoDTO.class))
                .thenThrow(notFound);

        assertThrows(NoExisteEnBdException.class, () -> client.validarMetodoPago(99L));
    }

    @Test
    void validarMetodoPagoThrowsRuntimeOnOtherError() {
        HttpClientErrorException badRequest = mock(HttpClientErrorException.class);
        when(badRequest.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.BAD_REQUEST);
        when(restTemplate.getForObject("http://localhost:8088/api/metodo-pago/99", MetodoPagoDTO.class))
                .thenThrow(badRequest);

        assertThrows(RuntimeException.class, () -> client.validarMetodoPago(99L));
    }
}
