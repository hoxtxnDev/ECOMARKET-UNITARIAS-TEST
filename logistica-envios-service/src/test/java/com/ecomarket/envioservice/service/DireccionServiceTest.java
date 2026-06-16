package com.ecomarket.envioservice.service;

import com.ecomarket.envioservice.exception.NoExisteEnBdException;
import com.ecomarket.envioservice.model.entity.Direccion;
import com.ecomarket.envioservice.repository.DireccionRepository;
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
@DisplayName("DireccionService")
class DireccionServiceTest {

    @Mock DireccionRepository direccionRepository;
    @InjectMocks DireccionService service;

    private Direccion direccion(Long id) {
        Direccion d = new Direccion();
        d.setId(id);
        d.setCalle("Av Siempre Viva");
        d.setNumero("123");
        d.setCiudad("Springfield");
        return d;
    }

    @Nested @DisplayName("readAll")
    class ReadAll {
        @Test void retornaTodas() {
            when(direccionRepository.findAll()).thenReturn(List.of(direccion(1L)));
            assertThat(service.readAll()).hasSize(1);
        }
    }

    @Nested @DisplayName("findById")
    class FindById {
        @Test void retornaDireccion() {
            when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion(1L)));
            assertThat(service.findById(1L).getCalle()).isEqualTo("Av Siempre Viva");
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(direccionRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }

    @Nested @DisplayName("create")
    class Create {
        @Test void creaDireccion() {
            Direccion d = direccion(null);
            when(direccionRepository.save(d)).thenReturn(direccion(1L));
            assertThat(service.create(d).getId()).isEqualTo(1L);
        }
    }

    @Nested @DisplayName("update")
    class Update {
        @Test void actualizaDireccion() {
            Direccion existente = direccion(1L);
            Direccion datos = new Direccion();
            datos.setCalle("Otra Calle");
            datos.setNumero("456");
            datos.setDepartamento("2B");
            datos.setCiudad("Gotham");
            datos.setCodigoPostal("1000");
            datos.setLatitud(-34.0);
            datos.setLongitud(-56.0);

            when(direccionRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(direccionRepository.save(existente)).thenReturn(existente);

            Direccion resultado = service.update(1L, datos);
            assertThat(resultado.getCalle()).isEqualTo("Otra Calle");
            assertThat(resultado.getCiudad()).isEqualTo("Gotham");
            assertThat(resultado.getLatitud()).isEqualTo(-34.0);
        }
    }

    @Nested @DisplayName("delete")
    class Delete {
        @Test void eliminaExistente() {
            when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion(1L)));
            service.delete(1L);
            verify(direccionRepository).deleteById(1L);
        }

        @Test void lanzaExcepcionSiNoExiste() {
            when(direccionRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.delete(99L)).isInstanceOf(NoExisteEnBdException.class);
        }
    }
}
