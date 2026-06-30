package com.ecomarket.gateway.config;

import com.ecomarket.gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

    private final JwtAuthenticationFilter jwtFilter;

    public RouteConfiguration(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("registro-service", r -> r.path("/api/usuarios/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8081"))
                .route("login-service", r -> r.path("/api/sesion/**").uri("http://localhost:8086"))
                .route("carrito-service", r -> r.path("/api/carrito/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8082"))
                .route("catalogo-service", r -> r.path("/api/catalogo/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8087"))
                .route("envio-service", r -> r.path("/api/v1/logistica-envios/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8083"))
                .route("tienda-service", r -> r.path("/api/tienda/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8090"))
                .route("pago-service", r -> r.path("/api/pagos/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8085"))
                .route("soporte-service", r -> r.path("/api/v1/soporte/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8088"))
                .route("analitica-service", r -> r.path("/api/analitica/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8084"))
                .route("pedido-service", r -> r.path("/api/pedidos/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8089"))
                .route("inventario-service", r -> r.path("/api/inventario/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8087"))
                .route("catalogo-admin-service", r -> r.path("/api/catalogo-admin/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8087"))
                .route("tiendas-service", r -> r.path("/api/tiendas/**").filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))).uri("http://localhost:8090"))
                .build();
    }
}
