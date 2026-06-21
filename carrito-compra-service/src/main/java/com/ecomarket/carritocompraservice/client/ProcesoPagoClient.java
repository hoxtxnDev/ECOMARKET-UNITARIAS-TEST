package com.ecomarket.carritocompraservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.carritocompraservice.dto.MetodoPagoDTO;
import com.ecomarket.carritocompraservice.exception.NoExisteEnBdException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProcesoPagoClient {

    private final RestTemplate restTemplate;

    @Value("${microservicio.pagos.url}")
    private String pagosUrl;

    public MetodoPagoDTO validarMetodoPago(Long metodoPagoId) {
        try {
            String url = pagosUrl + "/api/metodo-pago/" + metodoPagoId;
            return restTemplate.getForObject(url, MetodoPagoDTO.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new NoExisteEnBdException("El metodo de pago con id " + metodoPagoId + " no es valido.");
            }
            throw new RuntimeException("Error al validar metodo de pago: " + e.getMessage());
        }
    }
}
