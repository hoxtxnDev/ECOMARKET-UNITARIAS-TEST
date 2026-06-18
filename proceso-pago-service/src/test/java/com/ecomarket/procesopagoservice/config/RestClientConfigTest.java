package com.ecomarket.procesopagoservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RestClientConfig")
class RestClientConfigTest {

    @Test
    @DisplayName("restClient bean no es nulo")
    void restClientBeanNoEsNulo() {
        RestClientConfig config = new RestClientConfig();
        RestClient rc = config.restClient();
        assertThat(rc).isNotNull();
    }
}
