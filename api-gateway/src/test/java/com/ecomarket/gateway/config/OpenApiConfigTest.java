package com.ecomarket.gateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    @Test
    void apiGatewayOpenAPIContainsCorrectInfo() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.apiGatewayOpenAPI();

        assertThat(api).isNotNull();
        Info info = api.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("EcoMarket API Gateway");
        assertThat(info.getDescription()).isEqualTo("API Gateway para EcoMarket - Microservicios de e-commerce");
        assertThat(info.getVersion()).isEqualTo("1.0.0");

        License license = info.getLicense();
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("Apache 2.0");
        assertThat(license.getUrl()).isEqualTo("http://springdoc.org");
    }
}
