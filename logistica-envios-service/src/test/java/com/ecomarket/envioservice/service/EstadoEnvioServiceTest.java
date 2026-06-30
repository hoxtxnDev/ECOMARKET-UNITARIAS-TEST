package com.ecomarket.envioservice.service;

import com.ecomarket.envioservice.exception.NoExisteEnBdException;
import com.ecomarket.envioservice.exception.YaExisteEnBdException;
import com.ecomarket.envioservice.model.reference.EstadoEnvio;
import com.ecomarket.envioservice.repository.EstadoEnvioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EstadoEnvioService")
class EstadoEnvioServiceTest {

    @Mock EstadoEnvioRepository estadoEnvioRepository;
    @InjectMocks EstadoEnvioService service;

    private EstadoEnvio estado(Long id, String nombre) {
        return new EstadoEnvio(id, nombre);
    }

    @Nested @DisplayName("readAll")
    class ReadAll {
        @Test void retornaTodos() {
            when(estadoEnvioRepository.findAll()).thenReturn(List.of(estado(1L, "PENDIENTE")));
            assertThat(service.readAll()).hasSize(1);
        }
    }

    @Nested @DisplayName("findById")
    class FindById {
        @Test void retornaEstado() {
            when(estadoEnvioRepository.findById(1L)).thenReturn(Optional.of(estado(1L, "PENDIENTE")));
            assertThat(service.findById(1L).getNombre()).isEqualTo("PENDIENTE");
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(estadoEnvioRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }

    @Nested @DisplayName("create")
    class Create {
        @Test void creaEstado() {
            EstadoEnvio e = estado(null, "NUEVO");
            when(estadoEnvioRepository.findByNombre("NUEVO")).thenReturn(Optional.empty());
            when(estadoEnvioRepository.save(e)).thenReturn(estado(1L, "NUEVO"));
            assertThat(service.create(e).getId()).isEqualTo(1L);
        }

        @Test void lanzaExcepcionSiYaExiste() {
            EstadoEnvio e = estado(null, "PENDIENTE");
            when(estadoEnvioRepository.findByNombre("PENDIENTE")).thenReturn(Optional.of(estado(1L, "PENDIENTE")));
            assertThatThrownBy(() -> service.create(e)).isInstanceOf(YaExisteEnBdException.class);
        }
    }

    @Nested @DisplayName("delete")
    class Delete {
        @Test void eliminaExistente() {
            when(estadoEnvioRepository.findById(1L)).thenReturn(Optional.of(estado(1L, "PENDIENTE")));
            service.delete(1L);
            verify(estadoEnvioRepository).deleteById(1L);
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(estadoEnvioRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }
}
