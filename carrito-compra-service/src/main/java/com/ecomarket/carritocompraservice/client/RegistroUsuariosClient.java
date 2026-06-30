package com.ecomarket.carritocompraservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.carritocompraservice.exception.NoExisteEnBdException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegistroUsuariosClient {

    private final RestTemplate restTemplate;

    @Value("${app.services.registro-usuarios-url}")
    private String baseUrl;

    public void validarCliente(Long clienteId) {
        try {
            String url = baseUrl + "/api/usuarios/" + clienteId;
            restTemplate.getForEntity(url, Object.class);
        } catch (Exception e) {
            throw new NoExisteEnBdException("El usuario con ID " + clienteId + " no existe.");
        }
    }
}
