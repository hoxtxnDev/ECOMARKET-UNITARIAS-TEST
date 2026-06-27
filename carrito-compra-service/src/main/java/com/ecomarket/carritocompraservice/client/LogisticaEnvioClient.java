package com.ecomarket.carritocompraservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.carritocompraservice.dto.MetodoEnvioDTO;
import com.ecomarket.carritocompraservice.exception.NoExisteEnBdException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogisticaEnvioClient {

    private final RestTemplate restTemplate;

    @Value("${microservicio.envios.url}")
    private String enviosUrl;

    public MetodoEnvioDTO validarMetodoEnvio(Long metodoEnvioId) {
        try {
            String url = enviosUrl + "/api/v1/logistica-envios/metodo-envio/" + metodoEnvioId;
            return restTemplate.getForObject(url, MetodoEnvioDTO.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404) {
                throw new NoExisteEnBdException("El metodo de envio con id " + metodoEnvioId + " no es valido.");
            }
            throw new RuntimeException("Error al validar metodo de envio: " + e.getMessage());
        }
    }
}
