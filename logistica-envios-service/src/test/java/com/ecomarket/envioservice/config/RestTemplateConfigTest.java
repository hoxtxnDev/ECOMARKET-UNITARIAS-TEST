package com.ecomarket.envioservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RestTemplateConfig")
class RestTemplateConfigTest {

    private final RestTemplateConfig config = new RestTemplateConfig();

    @Test
    @DisplayName("crea RestTemplate bean")
    void restTemplate() {
        RestTemplate rt = config.restTemplate();
        assertThat(rt).isNotNull();
    }
}
