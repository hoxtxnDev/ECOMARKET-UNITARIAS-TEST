package com.ecomarket.envioservice.service;

import com.ecomarket.envioservice.exception.NoExisteEnBdException;
import com.ecomarket.envioservice.exception.YaExisteEnBdException;
import com.ecomarket.envioservice.model.reference.MetodoEnvio;
import com.ecomarket.envioservice.repository.MetodoEnvioRepository;
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
@DisplayName("MetodoEnvioService")
class MetodoEnvioServiceTest {

    @Mock MetodoEnvioRepository metodoEnvioRepository;
    @InjectMocks MetodoEnvioService service;

    private MetodoEnvio metodo(Long id, String nombre) {
        return new MetodoEnvio(id, nombre, 0.0);
    }

    @Nested @DisplayName("readAll")
    class ReadAll {
        @Test void retornaTodos() {
            when(metodoEnvioRepository.findAll()).thenReturn(List.of(metodo(1L, "Domicilio")));
            assertThat(service.readAll()).hasSize(1);
        }
    }

    @Nested @DisplayName("findById")
    class FindById {
        @Test void retornaMetodo() {
            when(metodoEnvioRepository.findById(1L)).thenReturn(Optional.of(metodo(1L, "Domicilio")));
            assertThat(service.findById(1L).getNombre()).isEqualTo("Domicilio");
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(metodoEnvioRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }

    @Nested @DisplayName("create")
    class Create {
        @Test void creaMetodo() {
            MetodoEnvio m = metodo(null, "Express");
            when(metodoEnvioRepository.findByNombre("Express")).thenReturn(Optional.empty());
            when(metodoEnvioRepository.save(m)).thenReturn(metodo(1L, "Express"));
            assertThat(service.create(m).getId()).isEqualTo(1L);
        }

        @Test void lanzaExcepcionSiYaExiste() {
            MetodoEnvio m = metodo(null, "Domicilio");
            when(metodoEnvioRepository.findByNombre("Domicilio")).thenReturn(Optional.of(metodo(1L, "Domicilio")));
            assertThatThrownBy(() -> service.create(m)).isInstanceOf(YaExisteEnBdException.class);
        }
    }

    @Nested @DisplayName("delete")
    class Delete {
        @Test void eliminaExistente() {
            when(metodoEnvioRepository.findById(1L)).thenReturn(Optional.of(metodo(1L, "Domicilio")));
            service.delete(1L);
            verify(metodoEnvioRepository).deleteById(1L);
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(metodoEnvioRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }

    @Nested @DisplayName("actualizarCosto")
    class ActualizarCosto {
        @Test void actualizaCosto() {
            MetodoEnvio existente = metodo(1L, "Domicilio");
            when(metodoEnvioRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(metodoEnvioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            MetodoEnvio resultado = service.actualizarCosto(1L, 9999.0);
            assertThat(resultado.getCosto()).isEqualTo(9999.0);
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(metodoEnvioRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.actualizarCosto(99L, 100.0)).isInstanceOf(NoExisteEnBdException.class);
        }
    }
}
