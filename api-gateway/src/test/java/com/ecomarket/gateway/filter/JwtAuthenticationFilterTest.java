package com.ecomarket.gateway.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;
    private RestTemplate restTemplate;
    private ServerWebExchange exchange;
    private ServerHttpRequest request;
    private ServerHttpResponse response;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        restTemplate = mock(RestTemplate.class);
        ReflectionTestUtils.setField(filter, "restTemplate", restTemplate);

        request = mock(ServerHttpRequest.class);
        response = mock(ServerHttpResponse.class);
        exchange = mock(ServerWebExchange.class);
        chain = mock(GatewayFilterChain.class);

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(chain.filter(exchange)).thenReturn(Mono.empty());
    }

    private void mockMutateRequest() {
        ServerHttpRequest.Builder builder = mock(ServerHttpRequest.Builder.class);
        when(request.mutate()).thenReturn(builder);
        when(builder.header(anyString(), anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(request);
        ServerWebExchange.Builder exchangeBuilder = mock(ServerWebExchange.Builder.class);
        when(exchange.mutate()).thenReturn(exchangeBuilder);
        when(exchangeBuilder.request(request)).thenReturn(exchangeBuilder);
        when(exchangeBuilder.build()).thenReturn(exchange);
    }

    private void mockAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        when(request.getHeaders()).thenReturn(headers);
    }

    private void mockForbiddenResponse() {
        DataBufferFactory bufferFactory = mock(DataBufferFactory.class);
        DataBuffer buffer = mock(DataBuffer.class);
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        when(response.bufferFactory()).thenReturn(bufferFactory);
        when(bufferFactory.wrap(any(byte[].class))).thenReturn(buffer);
        when(response.writeWith(any())).thenReturn(Mono.empty());
    }

    private void mockValidationResponse(boolean valido, Long usuarioId, List<String> roles) {
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "valido", valido,
                        "usuarioId", usuarioId != null ? usuarioId : 0,
                        "roles", roles != null ? roles : List.of()
                )));
    }

    @Test
    void allowsOptionsRequestWithoutAuth() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.OPTIONS);

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/api/sesion/login",
        "/api/sesion/credencial",
        "/api/sesion/recuperar",
        "/api/sesion/restablecer",
        "/api/usuarios/registro",
        "/api/estado-pago",
        "/api/metodo-pago",
        "/api/direccion-envio",
        "/api/estado-pedido",
        "/api/v1/estado-envio",
        "/api/v1/metodo-envio",
        "/api/v1/puntos-retiro",
        "/api/soporte/categorias",
        "/api/soporte/estados",
        "/api/sesion/validar",
        "/v3/api-docs",
        "/v3/api-docs/swagger-config",
        "/doc/swagger-ui.html",
        "/webjars/swagger-ui/index.html"
    })
    void allowsAllPublicPathsWithoutAuth(String path) {
        when(request.getURI()).thenReturn(URI.create("http://localhost" + path));
        when(request.getMethod()).thenReturn(HttpMethod.GET);

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void rejectsRequestWithoutAuthHeader() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        HttpHeaders headers = new HttpHeaders();
        when(request.getHeaders()).thenReturn(headers);
        when(response.setComplete()).thenReturn(Mono.empty());

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsRequestWithInvalidAuthHeader() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Invalid format");
        when(request.getHeaders()).thenReturn(headers);
        when(response.setComplete()).thenReturn(Mono.empty());

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void allowsAdminOnAnyEndpoint() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/catalogo-admin/categorias"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("admin-token");
        mockValidationResponse(true, 1L, List.of("ROLE_ADMIN"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void rejectsClienteOnProductCreation() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/catalogo"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnAdminCatalog() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/catalogo-admin/categorias"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnUserList() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void allowsClienteOnCartEndpoint() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/carrito/envio"));
        when(request.getMethod()).thenReturn(HttpMethod.PUT);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void rejectsClienteOnOtherUsersAddress() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/direcciones/99"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void allowsClienteOnOwnAddress() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/direcciones/2"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsClienteOnOwnOrders() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/pedidos/cliente/2"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void rejectsClienteOnOtherUsersOrders() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/pedidos/cliente/99"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void allowsClienteOnOwnOrderGeneration() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/pedidos/generar/10"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsRepartidorOnOrderStatusChange() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/pedidos/1/estado/2"));
        when(request.getMethod()).thenReturn(HttpMethod.PUT);
        mockAuthHeaders("repartidor-token");
        mockValidationResponse(true, 3L, List.of("ROLE_REPARTIDOR"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsRepartidorOnOrderStatusChangeNombre() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/pedidos/1/estado-nombre"));
        when(request.getMethod()).thenReturn(HttpMethod.PUT);
        mockAuthHeaders("repartidor-token");
        mockValidationResponse(true, 3L, List.of("ROLE_REPARTIDOR"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void rejectsRepartidorOnPutWithWrongPath() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/pedidos/1/otro"));
        when(request.getMethod()).thenReturn(HttpMethod.PUT);
        mockAuthHeaders("repartidor-token");
        mockValidationResponse(true, 3L, List.of("ROLE_REPARTIDOR"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsRepartidorOnNonStatusEndpoint() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/pedidos/cliente/3"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("repartidor-token");
        mockValidationResponse(true, 3L, List.of("ROLE_REPARTIDOR"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsRequestWithInvalidTokenResponse() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer invalid-token");
        when(request.getHeaders()).thenReturn(headers);
        when(response.setComplete()).thenReturn(Mono.empty());

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of("valido", false)));

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsRequestWhenValidationServiceThrowsException() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer some-token");
        when(request.getHeaders()).thenReturn(headers);
        when(response.setComplete()).thenReturn(Mono.empty());

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsRequestWhenValidoIsNullInResponse() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer token-with-null-valido");
        when(request.getHeaders()).thenReturn(headers);
        when(response.setComplete()).thenReturn(Mono.empty());

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of()));

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsRequestWithNullUsuarioId() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("token-null-uid");
        when(response.setComplete()).thenReturn(Mono.empty());
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of("valido", true, "roles", List.of("ROLE_CLIENTE"))));

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsRequestWithNullRoles() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("token-null-roles");
        when(response.setComplete()).thenReturn(Mono.empty());
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("valido", true);
        body.put("usuarioId", 1L);
        body.put("roles", null);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(body));

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsRequestWithEmptyRoles() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("token-empty-roles");
        when(response.setComplete()).thenReturn(Mono.empty());
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of("valido", true, "usuarioId", 1L, "roles", Collections.emptyList())));

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    // ── esEndpointProhibidoParaCliente: block paths ─────────────────────

    @Test
    void rejectsClienteOnRegistroContainingPath() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/registro-confirmacion"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsClienteOnDireccionNonNumeric() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/direcciones/nombre-direccion"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void rejectsClienteOnUserUpdateNonGet() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/2"));
        when(request.getMethod()).thenReturn(HttpMethod.PUT);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnPedidoEstado() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/pedidos/1/estado/2"));
        when(request.getMethod()).thenReturn(HttpMethod.PUT);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnPedidoEstadoNombre() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/pedidos/1/estado-nombre"));
        when(request.getMethod()).thenReturn(HttpMethod.PUT);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnTiendas() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/tiendas"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnInventario() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/inventario/ingresar"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnAnalitica() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/analitica/logs"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    // ── esEndpointProhibidoParaCliente: allow paths ─────────────────────

    @Test
    void allowsClienteOnSelfByCorreo() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/correo/test@test.com"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsClienteOnSelfById() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/2"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    // ── validarClienteIdEnPath ──────────────────────────────────────────

    @Test
    void allowsClienteOnFallthroughPath() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/alguna/otra/ruta"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    // ── Default role ────────────────────────────────────────────────────

    @Test
    void allowsDefaultRoleUser() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/direcciones"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("user-token");
        mockValidationResponse(true, 5L, List.of("ROLE_USER"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    // ── ResponderForbidden response body ────────────────────────────────

    @Test
    void forbiddenResponseContainsJsonBody() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/catalogo/productos"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        DataBufferFactory bufferFactory = mock(DataBufferFactory.class);
        DataBuffer buffer = mock(DataBuffer.class);
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        when(response.bufferFactory()).thenReturn(bufferFactory);
        when(bufferFactory.wrap(any(byte[].class))).thenReturn(buffer);
        when(response.writeWith(any())).thenReturn(Mono.empty());

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(bufferFactory).wrap(any(byte[].class));
        verify(response).writeWith(any());
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnReportes() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/reportes/ventas"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnMetricas() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/metricas"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnAlertas() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/alertas"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnRespaldos() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/respaldos"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnTiendaSingular() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/tienda/mi-tienda"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    // ── ROLE_SOPORTE: full access ──────────────────────────────────────

    @Test
    void allowsSoporteOnAnyEndpoint() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/catalogo/productos"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        mockAuthHeaders("soporte-token");
        mockValidationResponse(true, 4L, List.of("ROLE_SOPORTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsSoporteOnAdminCatalog() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/catalogo-admin/categorias"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("soporte-token");
        mockValidationResponse(true, 4L, List.of("ROLE_SOPORTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    // ── Repartidor PATCH for logistica-envios ──────────────────────────

    @Test
    void allowsRepartidorOnShipmentStatusChange() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/logistica-envios/envios/5/estado/3"));
        when(request.getMethod()).thenReturn(HttpMethod.PATCH);
        mockAuthHeaders("repartidor-token");
        mockValidationResponse(true, 3L, List.of("ROLE_REPARTIDOR"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    // ── Cliente blocked on Soporte endpoints ───────────────────────────

    @Test
    void rejectsClienteOnSoporteTicketsAsignar() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/tickets/1/asignar/5"));
        when(request.getMethod()).thenReturn(HttpMethod.PATCH);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnSoporteTicketsSolucionar() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/tickets/1/solucionar"));
        when(request.getMethod()).thenReturn(HttpMethod.PATCH);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnSoporteTicketsEstado() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/tickets/1/estado/2"));
        when(request.getMethod()).thenReturn(HttpMethod.PATCH);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnSoporteTicketsDelete() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/tickets/1"));
        when(request.getMethod()).thenReturn(HttpMethod.DELETE);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnEnviarNotificacionPush() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/enviar-notificacion-push"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnResenasAprobacion() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/resenas/1/aprobar"));
        when(request.getMethod()).thenReturn(HttpMethod.PATCH);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnResenasDelete() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/resenas/1"));
        when(request.getMethod()).thenReturn(HttpMethod.DELETE);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void rejectsClienteOnNotificacionesDelete() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/notificaciones/1"));
        when(request.getMethod()).thenReturn(HttpMethod.DELETE);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    // ── validarClienteIdEnPath: carrito with numeric ID ────────────────

    @Test
    void rejectsClienteOnCarritoNumericIdMismatch() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/carrito/99/items"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void allowsClienteOnCarritoOwnNumericId() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/carrito/2/items"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    // ── Missing branch coverage for Soporte endpoints ───────────────────

    @Test
    void rejectsClienteOnSoporteTicketsPatchNonSpecificPath() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/tickets/1/actualizar"));
        when(request.getMethod()).thenReturn(HttpMethod.PATCH);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void rejectsRepartidorOnPatchWithInvalidPath() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/logistica-envios/envios/abc/estado/3"));
        when(request.getMethod()).thenReturn(HttpMethod.PATCH);
        mockAuthHeaders("repartidor-token");
        mockValidationResponse(true, 3L, List.of("ROLE_REPARTIDOR"));
        mockForbiddenResponse();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void allowsClienteOnSoporteTicketsGetMethod() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/tickets/1/asignar/5"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsClienteOnNotificacionesGet() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/notificaciones/1"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsClienteOnResenasPatchNonSpecificPath() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/resenas/listar"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsClienteOnNotificacionPushGet() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/v1/soporte/enviar-notificacion-push"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        mockAuthHeaders("cliente-token");
        mockValidationResponse(true, 2L, List.of("ROLE_CLIENTE"));
        mockMutateRequest();

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }
}