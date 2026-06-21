package com.ecomarket.gestiontiendaservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProcesoPagoClient {

    private final RestTemplate restTemplate;

    @Value("${app.services.proceso-pago-url}")
    private String procesoPagoUrl;

    public List<TransaccionResumenDTO> obtenerTransaccionesPorCliente(Long clienteId) {
        String url = procesoPagoUrl + "/api/pagos/cliente/" + clienteId;
        try {
            log.info("Consultando transacciones del cliente {} en proceso-pago-service", clienteId);
            TransaccionResumenDTO[] resultado = restTemplate.getForObject(url, TransaccionResumenDTO[].class);
            return resultado != null ? List.of(resultado) : List.of();
        } catch (Exception e) {
            log.error("Error al obtener transacciones del cliente {}: {}", clienteId, e.getMessage());
            throw new RuntimeException("No se pudo obtener las transacciones del cliente: " + clienteId, e);
        }
    }

    public TransaccionResumenDTO obtenerTransaccion(Long transaccionId) {
        String url = procesoPagoUrl + "/api/pagos/" + transaccionId;
        try {
            log.info("Consultando transacción {} en proceso-pago-service", transaccionId);
            return restTemplate.getForObject(url, TransaccionResumenDTO.class);
        } catch (Exception e) {
            log.error("Error al obtener transacción {}: {}", transaccionId, e.getMessage());
            throw new RuntimeException("No se pudo obtener la transacción: " + transaccionId, e);
        }
    }
}
