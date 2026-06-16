package com.ecomarket.gestiontiendaservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.*;

class RestClientConfigTest {

    @Test
    @DisplayName("crea bean RestClient")
    void restClient() {
        RestClient client = new RestClientConfig().restClient();
        assertThat(client).isNotNull();
    }
}
