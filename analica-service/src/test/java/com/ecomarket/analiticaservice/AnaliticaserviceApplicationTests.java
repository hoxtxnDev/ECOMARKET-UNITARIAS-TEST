package com.ecomarket.analiticaservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class AnaliticaserviceApplicationTests {

    @Test
    void hasImplicitConstructor() {
        assertNotNull(new AnaliticaserviceApplication());
    }

    @Test
    void mainCallsSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            AnaliticaserviceApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(AnaliticaserviceApplication.class, new String[]{}));
        }
    }
}
