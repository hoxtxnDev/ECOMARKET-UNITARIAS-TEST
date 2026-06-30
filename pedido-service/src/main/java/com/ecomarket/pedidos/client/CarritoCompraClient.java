package com.ecomarket.pedidos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.pedidos.dto.CarritoDTO;
import com.ecomarket.pedidos.exception.NoExisteEnBdException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarritoCompraClient {

    private static final Logger log = LoggerFactory.getLogger(CarritoCompraClient.class);

    private final RestTemplate restTemplate;

    @Value("${app.services.carrito-compras-url}")
    private String baseUrl;

    public CarritoDTO obtenerCarrito(Long clienteId) {
        try {
            String url = baseUrl + "/api/carrito/activo";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", String.valueOf(clienteId));
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(url, HttpMethod.GET, entity, CarritoDTO.class).getBody();
        } catch (Exception e) {
            throw new NoExisteEnBdException("El carrito del cliente " + clienteId + " no existe.");
        }
    }

    public void vaciarCarrito(Long clienteId) {
        String url = baseUrl + "/api/carrito/vaciar";
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", String.valueOf(clienteId));
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public void cerrarCarrito(Long clienteId) {
        try {
            String url = baseUrl + "/api/carrito/cerrar";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", String.valueOf(clienteId));
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
        } catch (Exception e) {
            log.error("Error al cerrar carrito del cliente {}: {}", clienteId, e.getMessage());
        }
    }
}
