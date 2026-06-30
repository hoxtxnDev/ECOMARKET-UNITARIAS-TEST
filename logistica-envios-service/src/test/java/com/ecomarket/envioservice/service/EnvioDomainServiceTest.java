package com.ecomarket.envioservice.service;

import com.ecomarket.envioservice.exception.NoExisteEnBdException;
import com.ecomarket.envioservice.model.entity.Envio;
import com.ecomarket.envioservice.repository.EnvioRepository;
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
@DisplayName("EnvioDomainService")
class EnvioDomainServiceTest {

    @Mock EnvioRepository envioRepository;
    @InjectMocks EnvioDomainService service;

    private Envio envio() {
        Envio e = new Envio();
        e.setId(1L);
        return e;
    }

    @Nested
    @DisplayName("readAll")
    class ReadAll {
        @Test
        void retornaTodos() {
            when(envioRepository.findAll()).thenReturn(List.of(envio()));
            assertThat(service.readAll()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("readByClienteId")
    class ReadByCliente {
        @Test
        void retornaEnviosDelCliente() {
            when(envioRepository.findByClienteId(5L)).thenReturn(List.of(envio()));
            assertThat(service.readByClienteId(5L)).hasSize(1);
        }
    }

    @Nested
    @DisplayName("readByPedidoId")
    class ReadByPedido {
        @Test
        void retornaEnviosDelPedido() {
            when(envioRepository.findByPedidoId(100L)).thenReturn(List.of(envio()));
            assertThat(service.readByPedidoId(100L)).hasSize(1);
        }
    }

    @Nested
    @DisplayName("readByEstadoId")
    class ReadByEstado {
        @Test
        void retornaEnviosPorEstado() {
            when(envioRepository.findByEstadoActualId(1L)).thenReturn(List.of(envio()));
            assertThat(service.readByEstadoId(1L)).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        void retornaEnvio() {
            when(envioRepository.findById(1L)).thenReturn(Optional.of(envio()));
            assertThat(service.findById(1L).getId()).isEqualTo(1L);
        }

        @Test
        void lanzaExcepcionSiNoExiste() {
            when(envioRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }

    @Nested
    @DisplayName("save")
    class Save {
        @Test
        void guardaEnvio() {
            Envio e = envio();
            when(envioRepository.save(e)).thenReturn(e);
            assertThat(service.save(e)).isEqualTo(e);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        void eliminaExistente() {
            when(envioRepository.findById(1L)).thenReturn(Optional.of(envio()));
            service.delete(1L);
            verify(envioRepository).deleteById(1L);
        }

        @Test
        void lanzaExcepcionSiNoExiste() {
            when(envioRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }
}
