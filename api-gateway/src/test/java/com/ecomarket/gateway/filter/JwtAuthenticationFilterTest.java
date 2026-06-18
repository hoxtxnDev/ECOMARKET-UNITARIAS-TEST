package com.ecomarket.gateway.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
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

    @Test
    void allowsLoginPathWithoutAuth() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/login"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
    }

    @Test
    void allowsRegistroPathWithoutAuth() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios/registro"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
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
    void allowsRequestWithValidToken() {
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/usuarios"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer valid-token");
        when(request.getHeaders()).thenReturn(headers);

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of("valido", true)));

        GatewayFilter gatewayFilter = filter.apply(new JwtAuthenticationFilter.Config());
        assertNotNull(gatewayFilter.filter(exchange, chain));

        verify(chain).filter(exchange);
        verify(response, never()).setStatusCode(any());
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
}
