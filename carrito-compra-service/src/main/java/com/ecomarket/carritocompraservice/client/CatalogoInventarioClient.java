package com.ecomarket.carritocompraservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.carritocompraservice.dto.ProductoClienteDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CatalogoInventarioClient {
    private final RestTemplate restTemplate;

    @Value("${microservicio.catalogo.url}")
    private String catalogoUrl;

    public ProductoClienteDTO obtenerProducto(Long productoId) {
        try {
            String url = catalogoUrl + "/api/catalogo/" + productoId;
            return restTemplate.getForObject(url, ProductoClienteDTO.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Producto no encontrado en catalogo: " + productoId);
        }
    }
}
