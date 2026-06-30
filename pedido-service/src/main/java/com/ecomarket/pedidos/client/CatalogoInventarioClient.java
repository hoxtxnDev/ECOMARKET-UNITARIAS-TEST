package com.ecomarket.pedidos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.pedidos.dto.ProductoDTO;
import com.ecomarket.pedidos.exception.NoExisteEnBdException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatalogoInventarioClient {

    private final RestTemplate restTemplate;

    @Value("${app.services.catalogo-inventario-url}")
    private String baseUrl;

    public ProductoDTO obtenerProducto(Long productoId) {
        try {
            String url = baseUrl + "/api/catalogo/" + productoId;
            return restTemplate.getForEntity(url, ProductoDTO.class).getBody();
        } catch (Exception e) {
            throw new NoExisteEnBdException("El producto con ID " + productoId + " no existe.");
        }
    }
}

