package com.ecomarket.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final RestTemplate restTemplate = new RestTemplate();

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    @SuppressWarnings("rawtypes")
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            @SuppressWarnings("unused")
            HttpMethod method = exchange.getRequest().getMethod();

            if (path.contains("/api/usuarios/login") || path.contains("/api/usuarios/registro")) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            String validationUrl = "http://localhost:8081/api/usuarios/validar";
            Map<String, String> request = Map.of("token", token);

            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(validationUrl, request, Map.class);
                Boolean isValid = (Boolean) response.getBody().get("valido");

                if (isValid != null && isValid) {
                    return chain.filter(exchange);
                }
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }
}
