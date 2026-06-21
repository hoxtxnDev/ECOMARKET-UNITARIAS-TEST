package com.ecomarket.gestiontiendaservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;

class GestiontiendaserviceApplicationTests {

    @Test
    void instantiate() {
        assertThat(new GestiontiendaserviceApplication()).isNotNull();
    }

    @Test
    void main() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                  .thenReturn(null);

            GestiontiendaserviceApplication.main(new String[]{});

            mocked.verify(() -> SpringApplication.run(
                    GestiontiendaserviceApplication.class, new String[]{}));
        }
    }

    private <T> T any(Class<T> type) {
        return Mockito.any(type);
    }
}
