package com.ecomarket.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final RestTemplate restTemplate = new RestTemplate();

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    @Override
    public String name() {
        return "JwtAuthentication";
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            HttpMethod method = exchange.getRequest().getMethod();

            // Permitir preflight CORS
            if (HttpMethod.OPTIONS.equals(method)) {
                return chain.filter(exchange);
            }

            // Endpoints públicos (sin autenticación)
            if (esPublico(path)) {
                return chain.filter(exchange);
            }

            // Extraer token
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            // Validar token llamando al iniciosesion-service
            String validationUrl = "http://localhost:8086/api/sesion/validar";
            Map<String, String> request = Map.of("token", token);

            Long usuarioId;
            List<String> roles;
            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(validationUrl, request, Map.class);
                Map body = response.getBody();
                Boolean isValid = (Boolean) body.get("valido");

                if (isValid == null || !isValid) {
                    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                Object uidObj = body.get("usuarioId");
                usuarioId = uidObj instanceof Number ? ((Number) uidObj).longValue() : null;
                roles = (List<String>) body.get("roles");
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            if (usuarioId == null || roles == null || roles.isEmpty()) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // ── Control de acceso basado en roles ──────────────────────────────

            String rol = roles.get(0);

            // Admin → acceso total
            if ("ROLE_ADMIN".equals(rol)) {
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", String.valueOf(usuarioId))
                        .header("X-User-Roles", String.join(",", roles))
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }

            // Soporte → acceso total (como admin)
            if ("ROLE_SOPORTE".equals(rol)) {
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", String.valueOf(usuarioId))
                        .header("X-User-Roles", String.join(",", roles))
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }

            // Repartidor → solo cambiar estado de pedidos
            if ("ROLE_REPARTIDOR".equals(rol)) {
                if (esAccesoRepartidorPermitido(method, path)) {
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(usuarioId))
                            .header("X-User-Roles", String.join(",", roles))
                            .build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                }
                return responderForbidden(exchange, "No tienes permiso para acceder a este recurso. Tu rol no tiene los privilegios necesarios.");
            }

            // Cliente → acceso restringido
            if ("ROLE_CLIENTE".equals(rol)) {
                // Bloquear endpoints prohibidos para cliente
                if (esEndpointProhibidoParaCliente(method, path)) {
                    return responderForbidden(exchange, "No tienes permiso para acceder a este recurso. Tu rol de cliente no tiene los privilegios necesarios.");
                }

                // Validar que el clienteId en la ruta coincida con su ID (cuando corresponda)
                if (!validarClienteIdEnPath(path, usuarioId)) {
                    return responderForbidden(exchange, "No tienes permiso para acceder a este recurso. El ID de cliente en la ruta no coincide con tu sesión.");
                }

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", String.valueOf(usuarioId))
                        .header("X-User-Roles", String.join(",", roles))
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }

            // Otros roles (ROLE_USER legacy) → permitir con headers
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(usuarioId))
                    .header("X-User-Roles", String.join(",", roles))
                    .build();
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    // ── Endpoints públicos ─────────────────────────────────────────────────

    private boolean esPublico(String path) {
        return path.equals("/api/sesion/login")
            || path.equals("/api/sesion/credencial")
            || path.equals("/api/usuarios/registro")
            || path.startsWith("/api/estado-pago")
            || path.startsWith("/api/metodo-pago")
            || path.startsWith("/api/direccion-envio")
            || path.startsWith("/api/estado-pedido")
            || path.startsWith("/api/v1/estado-envio")
            || path.startsWith("/api/v1/metodo-envio")
            || path.startsWith("/api/v1/puntos-retiro")
            || path.startsWith("/api/soporte/categorias")
            || path.startsWith("/api/soporte/estados")
            || path.startsWith("/api/sesion/recuperar")
            || path.startsWith("/api/sesion/restablecer")
            || path.startsWith("/api/sesion/validar")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/doc/swagger-ui")
            || path.startsWith("/webjars");
    }

    // ── Endpoints prohibidos para CLIENTE ──────────────────────────────────

    private boolean esEndpointProhibidoParaCliente(HttpMethod method, String path) {
        // Creación/edición/eliminación de productos
        if (path.startsWith("/api/catalogo") && method != HttpMethod.GET) {
            return true;
        }

        // Catálogo admin (categorías, estados, especificaciones)
        if (path.startsWith("/api/catalogo-admin")) {
            return true;
        }

        // Listado de usuarios
        if (path.equals("/api/usuarios") || (path.startsWith("/api/usuarios") && !path.contains("/direcciones") && !path.contains("/registro"))) {
            // Permitir GET /api/usuarios/{id} (consulta individual) y /api/usuarios/correo/{correo}
            if ("GET".equals(method.name())) {
                Pattern individual = Pattern.compile("^/api/usuarios/(\\d+)$");
                Pattern correo = Pattern.compile("^/api/usuarios/correo/.+$");
                if (individual.matcher(path).matches() || correo.matcher(path).matches()) {
                    return false;
                }
            }
            return true;
        }

        // Cambio de estado de pedidos (solo admin/repartidor)
        if (path.matches("^/api/pedidos/\\d+/estado/\\d+$")
            || path.matches("^/api/pedidos/\\d+/estado-nombre$")) {
            return true;
        }

        // Soporte: asignar empleado, solucionar, cambiar estado, eliminar tickets (solo admin/soporte)
        if (path.startsWith("/api/v1/soporte/tickets") && HttpMethod.PATCH.equals(method)) {
            if (path.contains("/asignar/") || path.contains("/solucionar") || path.contains("/estado/")) {
                return true;
            }
        }
        if (path.startsWith("/api/v1/soporte/tickets") && HttpMethod.DELETE.equals(method)) {
            return true;
        }
        // Soporte: enviar notificacion push (solo admin/soporte)
        if (path.equals("/api/v1/soporte/enviar-notificacion-push") && HttpMethod.POST.equals(method)) {
            return true;
        }
        // Soporte: aprobar/rechazar/eliminar resenas (solo admin/soporte)
        if (path.startsWith("/api/v1/soporte/resenas") && (HttpMethod.PATCH.equals(method) || HttpMethod.DELETE.equals(method))) {
            return true;
        }
        // Soporte: eliminar notificaciones (solo admin/soporte)
        if (path.startsWith("/api/v1/soporte/notificaciones") && HttpMethod.DELETE.equals(method)) {
            return true;
        }

        // Gestión de tiendas (solo admin)
        if (path.startsWith("/api/tiendas") || path.startsWith("/api/tienda")) {
            return true;
        }

        // Inventario (solo admin/repartidor)
        if (path.startsWith("/api/inventario")) {
            return true;
        }

        // Analítica
        if (path.startsWith("/api/analitica") || path.startsWith("/api/v1/reportes")
            || path.startsWith("/api/v1/metricas") || path.startsWith("/api/v1/alertas")
            || path.startsWith("/api/v1/respaldos")) {
            return true;
        }

        return false;
    }

    // ── Acceso permitido para REPARTIDOR ───────────────────────────────────

    private boolean esAccesoRepartidorPermitido(HttpMethod method, String path) {
        if (HttpMethod.PUT.equals(method)) {
            Pattern cambiarEstado = Pattern.compile("^/api/pedidos/\\d+/estado/\\d+$");
            Pattern cambiarEstadoNombre = Pattern.compile("^/api/pedidos/\\d+/estado-nombre$");
            if (cambiarEstado.matcher(path).matches() || cambiarEstadoNombre.matcher(path).matches()) {
                return true;
            }
        }
        if (HttpMethod.PATCH.equals(method)) {
            Pattern cambiarEstadoEnvio = Pattern.compile("^/api/v1/logistica-envios/envios/\\d+/estado/\\d+$");
            if (cambiarEstadoEnvio.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }

    // ── Validar clienteId en path contra JWT ──────────────────────────────

    private boolean validarClienteIdEnPath(String path, Long usuarioId) {
        String uidStr = String.valueOf(usuarioId);
        String[] parts = path.split("/");

        // /api/usuarios/direcciones/{usuarioId}  (GET: listar)
        if (matchesPattern(parts, "api", "usuarios", "direcciones", "*")) {
            String candidate = parts[4];
            return candidate.equals(uidStr) || !candidate.matches("\\d+");
        }

        // /api/carrito/{clienteId}/...
        if (matchesPattern(parts, "api", "carrito", "*")) {
            String candidate = parts[3];
            if (candidate.matches("\\d+")) {
                return candidate.equals(uidStr);
            }
            return true; // segmento no numérico (ej: no es un id)
        }

        // /api/pedidos/cliente/{clienteId}
        if (matchesPattern(parts, "api", "pedidos", "cliente", "*")) {
            return parts[4].equals(uidStr);
        }

        // /api/pedidos/generar — no tiene clienteId en path, se usa X-User-Id
        if (matchesPattern(parts, "api", "pedidos", "generar", "*")) {
            return true;
        }

        return true;
    }

    private boolean matchesPattern(String[] parts, String... expected) {
        if (parts.length < expected.length + 1) return false; // +1 for empty first element from leading /
        for (int i = 0; i < expected.length; i++) {
            if (!"*".equals(expected[i]) && !parts[i + 1].equals(expected[i])) {
                return false;
            }
        }
        return true;
    }

    private Mono<Void> responderForbidden(org.springframework.web.server.ServerWebExchange exchange, String mensaje) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\":\"Forbidden\",\"message\":\"" + mensaje + "\"}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
