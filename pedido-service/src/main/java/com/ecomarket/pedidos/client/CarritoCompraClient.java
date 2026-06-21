package com.ecomarket.pedidos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
            String url = baseUrl + "/api/carrito/" + clienteId;
            return restTemplate.getForEntity(url, CarritoDTO.class).getBody();
        } catch (Exception e) {
            throw new NoExisteEnBdException("El carrito del cliente " + clienteId + " no existe.");
        }
    }

    public void vaciarCarrito(Long clienteId) {
        String url = baseUrl + "/api/carrito/" + clienteId + "/vaciar";
        restTemplate.delete(url);
    }

    public void cerrarCarrito(Long clienteId) {
        try {
            String url = baseUrl + "/api/carrito/" + clienteId + "/cerrar";
            restTemplate.put(url, null);
        } catch (Exception e) {
            log.error("Error al cerrar carrito del cliente {}: {}", clienteId, e.getMessage());
        }
    }
}
