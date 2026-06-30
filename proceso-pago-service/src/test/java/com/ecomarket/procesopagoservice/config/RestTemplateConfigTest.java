package com.ecomarket.procesopagoservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RestTemplateConfig")
class RestTemplateConfigTest {

    @Test
    @DisplayName("restTemplate bean no es nulo")
    void restTemplateBeanNoEsNulo() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate rt = config.restTemplate();
        assertThat(rt).isNotNull();
    }
}
