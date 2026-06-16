package com.ecomarket.soporteservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;

class RestTemplateConfigTest {

    @Test
    @org.junit.jupiter.api.DisplayName("restTemplate bean no es nulo")
    void restTemplateBeanNoEsNulo() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate rt = config.restTemplate();
        assertThat(rt).isNotNull();
    }
}