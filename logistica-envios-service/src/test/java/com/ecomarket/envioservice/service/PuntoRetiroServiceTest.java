package com.ecomarket.envioservice.service;

import com.ecomarket.envioservice.exception.EnvioEstadoInvalidoException;
import com.ecomarket.envioservice.exception.NoExisteEnBdException;
import com.ecomarket.envioservice.model.entity.PuntoRetiro;
import com.ecomarket.envioservice.repository.PuntoRetiroRepository;
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
@DisplayName("PuntoRetiroService")
class PuntoRetiroServiceTest {

    @Mock PuntoRetiroRepository puntoRetiroRepository;
    @InjectMocks PuntoRetiroService service;

    private PuntoRetiro punto(Long id, boolean activo, int actual, int max) {
        PuntoRetiro p = new PuntoRetiro();
        p.setId(id);
        p.setNombre("Retiro " + id);
        p.setActivo(activo);
        p.setCapacidadActual(actual);
        p.setCapacidadMaxima(max);
        return p;
    }

    @Nested @DisplayName("readAll")
    class ReadAll {
        @Test void retornaTodos() {
            when(puntoRetiroRepository.findAll()).thenReturn(List.of(punto(1L, true, 5, 10)));
            assertThat(service.readAll()).hasSize(1);
        }
    }

    @Nested @DisplayName("readActivos")
    class ReadActivos {
        @Test void retornaSoloActivos() {
            when(puntoRetiroRepository.findByActivoTrue()).thenReturn(List.of(punto(1L, true, 5, 10)));
            assertThat(service.readActivos()).hasSize(1);
        }
    }

    @Nested @DisplayName("findById")
    class FindById {
        @Test void retornaPunto() {
            when(puntoRetiroRepository.findById(1L)).thenReturn(Optional.of(punto(1L, true, 5, 10)));
            assertThat(service.findById(1L).getNombre()).contains("Retiro");
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(puntoRetiroRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }

    @Nested @DisplayName("create")
    class Create {
        @Test void creaPunto() {
            PuntoRetiro p = punto(null, true, 0, 10);
            when(puntoRetiroRepository.save(p)).thenReturn(punto(1L, true, 0, 10));
            assertThat(service.create(p).getId()).isEqualTo(1L);
        }
    }

    @Nested @DisplayName("update")
    class Update {
        @Test void actualizaPunto() {
            PuntoRetiro existente = punto(1L, true, 5, 10);
            PuntoRetiro datos = new PuntoRetiro();
            datos.setNombre("Nuevo Nombre");
            datos.setCapacidadActual(3);
            datos.setCapacidadMaxima(20);
            datos.setActivo(false);

            when(puntoRetiroRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(puntoRetiroRepository.save(existente)).thenReturn(existente);

            PuntoRetiro resultado = service.update(1L, datos);
            assertThat(resultado.getNombre()).isEqualTo("Nuevo Nombre");
            assertThat(resultado.getCapacidadActual()).isEqualTo(3);
            assertThat(resultado.getActivo()).isFalse();
        }
    }

    @Nested @DisplayName("delete")
    class Delete {
        @Test void eliminaExistente() {
            when(puntoRetiroRepository.findById(1L)).thenReturn(Optional.of(punto(1L, true, 5, 10)));
            service.delete(1L);
            verify(puntoRetiroRepository).deleteById(1L);
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(puntoRetiroRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }

    @Nested @DisplayName("verificarDisponibilidad")
    class VerificarDisponibilidad {
        @Test void pasaSiDisponible() {
            PuntoRetiro p = punto(1L, true, 5, 10);
            assertThatNoException().isThrownBy(() -> service.verificarDisponibilidad(p));
        }

        @Test void lanzaExcepcionSiNoActivo() {
            PuntoRetiro p = punto(1L, false, 5, 10);
            assertThatThrownBy(() -> service.verificarDisponibilidad(p))
                    .isInstanceOf(EnvioEstadoInvalidoException.class);
        }

        @Test void lanzaExcepcionSiCapacidadLlena() {
            PuntoRetiro p = punto(1L, true, 10, 10);
            assertThatThrownBy(() -> service.verificarDisponibilidad(p))
                    .isInstanceOf(EnvioEstadoInvalidoException.class);
        }
    }
}
