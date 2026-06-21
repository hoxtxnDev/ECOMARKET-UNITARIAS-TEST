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
            HttpMethod method = exchange.getRequest().getMethod();
            
            System.out.println("PATH RECIBIDO EN JWT FILTER: " + path);

            // Permitir preflight CORS
            if (HttpMethod.OPTIONS.equals(method)) {
                return chain.filter(exchange);
            }

            // Endpoints públicos: login, registro, creación de credenciales y seeders iniciales
            if (path.startsWith("/api/sesion/login") || 
                path.startsWith("/api/sesion/credencial") || 
                path.startsWith("/api/usuarios/registro") || 
                path.startsWith("/api/usuarios/roles") || 
                path.startsWith("/api/usuarios/estados-perfil") || 
                path.startsWith("/api/usuarios/permisos") || 
                path.startsWith("/api/estado-pago") || 
                path.startsWith("/api/metodo-pago") || 
                path.startsWith("/api/direccion-envio") || 
                path.startsWith("/api/estado-pedido") || 
                path.startsWith("/api/v1/estado-envio") || 
                path.startsWith("/api/v1/metodo-envio") || 
                path.startsWith("/api/v1/puntos-retiro") || 
                path.startsWith("/api/soporte/categorias") || 
                path.startsWith("/api/soporte/estados")) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            
            // Validar token llamando al iniciosesion-service
            String validationUrl = "http://localhost:8086/api/sesion/validar";
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
