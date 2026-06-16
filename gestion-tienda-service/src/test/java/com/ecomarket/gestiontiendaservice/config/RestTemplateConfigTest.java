package com.ecomarket.gestiontiendaservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;

class RestTemplateConfigTest {

    @Test
    @DisplayName("crea bean RestTemplate con timeout")
    void restTemplate() {
        RestTemplate template = new RestTemplateConfig().restTemplate();
        assertThat(template).isNotNull();
    }
}
