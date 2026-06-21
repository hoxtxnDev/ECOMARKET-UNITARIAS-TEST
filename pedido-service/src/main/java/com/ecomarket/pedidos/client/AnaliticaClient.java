package com.ecomarket.pedidos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnaliticaClient {

    private final RestTemplate restTemplate;

    @Value("${app.services.analitica-url}")
    private String baseUrl;

    public void registrarLog(java.util.Map<String, Object> log) {
        try {
            restTemplate.postForEntity(baseUrl + "/api/analitica/logs", log, String.class);
        } catch (Exception e) {
            // Log error but don't break business flow
        }
    }
}
