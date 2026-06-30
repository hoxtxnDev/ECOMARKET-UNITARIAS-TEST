package com.ecomarket.envioservice.service;

import com.ecomarket.envioservice.exception.NoExisteEnBdException;
import com.ecomarket.envioservice.model.entity.RutaTransporte;
import com.ecomarket.envioservice.repository.RutaTransporteRepository;
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
@DisplayName("RutaTransporteService")
class RutaTransporteServiceTest {

    @Mock RutaTransporteRepository rutaTransporteRepository;
    @InjectMocks RutaTransporteService service;

    private RutaTransporte ruta(Long id) {
        RutaTransporte r = new RutaTransporte();
        r.setId(id);
        r.setTransportistaId(10L);
        return r;
    }

    @Nested @DisplayName("readAll")
    class ReadAll {
        @Test void retornaTodas() {
            when(rutaTransporteRepository.findAll()).thenReturn(List.of(ruta(1L)));
            assertThat(service.readAll()).hasSize(1);
        }
    }

    @Nested @DisplayName("findById")
    class FindById {
        @Test void retornaRuta() {
            when(rutaTransporteRepository.findById(1L)).thenReturn(Optional.of(ruta(1L)));
            assertThat(service.findById(1L).getTransportistaId()).isEqualTo(10L);
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(rutaTransporteRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }

    @Nested @DisplayName("readByTransportistaId")
    class ReadByTransportista {
        @Test void retornaRutasDelTransportista() {
            when(rutaTransporteRepository.findByTransportistaId(10L)).thenReturn(List.of(ruta(1L)));
            assertThat(service.readByTransportistaId(10L)).hasSize(1);
        }
    }

    @Nested @DisplayName("save")
    class Save {
        @Test void guardaRuta() {
            RutaTransporte r = ruta(null);
            when(rutaTransporteRepository.save(r)).thenReturn(ruta(1L));
            assertThat(service.save(r).getId()).isEqualTo(1L);
        }
    }

    @Nested @DisplayName("delete")
    class Delete {
        @Test void eliminaExistente() {
            when(rutaTransporteRepository.findById(1L)).thenReturn(Optional.of(ruta(1L)));
            service.delete(1L);
            verify(rutaTransporteRepository).deleteById(1L);
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(rutaTransporteRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }
}
