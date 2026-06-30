package com.ecomarket.catalogoinventarioservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.catalogoinventarioservice.dto.SucursalDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GestionTiendaClient {

    private final RestTemplate restTemplate;

    @Value("${microservicio.gestiontienda.url}")
    private String baseUrl;

    public SucursalDTO obtenerSucursal(Long sucursalId) {
        String url = baseUrl + "/api/tienda/sucursal/" + sucursalId;
        return restTemplate.getForEntity(url, SucursalDTO.class).getBody();
    }

    public void notificarStockBajo(Long sucursalId, Long productoId, Integer stockActual) {
        String url = baseUrl + "/api/tienda/alertas/stock-bajo";
        restTemplate.postForEntity(url,
            java.util.Map.of("sucursalId", sucursalId, "productoId", productoId, "stockActual", stockActual),
            Void.class);
    }
}