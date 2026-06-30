package com.ecomarket.envioservice.service;

import com.ecomarket.envioservice.model.entity.HistorialEnvio;
import com.ecomarket.envioservice.model.reference.EstadoEnvio;
import com.ecomarket.envioservice.repository.HistorialEnvioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistorialEnvioService")
class HistorialEnvioServiceTest {

    @Mock HistorialEnvioRepository historialEnvioRepository;
    @InjectMocks HistorialEnvioService service;

    private HistorialEnvio historial() {
        HistorialEnvio h = new HistorialEnvio();
        h.setId(1L);
        h.setEnvioId(10L);
        h.setEstado(new EstadoEnvio(1L, "PENDIENTE"));
        return h;
    }

    @Nested @DisplayName("findHistorialByEnvioId")
    class FindHistorial {
        @Test void retornaHistorialOrdenado() {
            when(historialEnvioRepository.findByEnvioIdOrderByFechaActualizacionAsc(10L))
                    .thenReturn(List.of(historial()));
            assertThat(service.findHistorialByEnvioId(10L)).hasSize(1);
        }
    }

    @Nested @DisplayName("save")
    class Save {
        @Test void guardaHistorial() {
            HistorialEnvio h = historial();
            when(historialEnvioRepository.save(h)).thenReturn(h);
            assertThat(service.save(h).getId()).isEqualTo(1L);
        }
    }
}
