package com.horacio.ecomarket.usuarios.service;

import com.horacio.ecomarket.usuarios.exception.RecursoNoEncontradoException;
import com.horacio.ecomarket.usuarios.model.Direccion;
import com.horacio.ecomarket.usuarios.repository.DireccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DireccionService")
class DireccionServiceTest {

    @Mock
    private DireccionRepository direccionRepository;

    @InjectMocks
    private DireccionService service;

    private Direccion direccionBase;
    private Long usuarioId = 1L;

    @BeforeEach
    void setUp() {
        direccionBase = Direccion.builder()
                .id(10L)
                .usuarioId(usuarioId)
                .calle("Calle Falsa 123")
                .numero("123")
                .ciudad("Santiago")
                .region("RM")
                .destinatario("Horacio")
                .esPredeterminada(false)
                .build();
    }

    @Nested
    @DisplayName("agregarDireccion")
    class AgregarDireccion {
        @Test
        @DisplayName("agrega direccion correctamente cuando no es predeterminada")
        void agregaDireccionNormal() {
            when(direccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Direccion resultado = service.agregarDireccion(usuarioId, direccionBase);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getUsuarioId()).isEqualTo(usuarioId);
            verify(direccionRepository).save(direccionBase);
            verify(direccionRepository, never()).findByUsuarioId(anyLong());
        }

        @Test
        @DisplayName("marca otras direcciones como no predeterminadas al agregar una predeterminada")
        void agregaDireccionPredeterminada() {
            Direccion predeterminada = Direccion.builder()
                    .calle("Calle Nueva")
                    .esPredeterminada(true)
                    .build();
            
            List<Direccion> existentes = new ArrayList<>();
            existentes.add(direccionBase);
            
            when(direccionRepository.findByUsuarioId(usuarioId)).thenReturn(existentes);
            when(direccionRepository.saveAll(any())).thenReturn(existentes);
            when(direccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Direccion resultado = service.agregarDireccion(usuarioId, predeterminada);

            assertThat(resultado).isNotNull();
            verify(direccionRepository).findByUsuarioId(usuarioId);
            verify(direccionRepository).saveAll(any());
            verify(direccionRepository).save(predeterminada);
        }
    }

    @Nested
    @DisplayName("listarDirecciones")
    class ListarDirecciones {
        @Test
        @DisplayName("retorna lista de direcciones del usuario")
        void listaDireccionesExitosamente() {
            List<Direccion> lista = List.of(direccionBase);
            when(direccionRepository.findByUsuarioId(usuarioId)).thenReturn(lista);

            List<Direccion> resultado = service.listarDirecciones(usuarioId);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0)).isEqualTo(direccionBase);
        }
    }

    @Nested
    @DisplayName("obtenerPorId")
    class ObtenerPorId {
        @Test
        @DisplayName("retorna la direccion cuando el id existe")
        void obtieneDireccionExistente() {
            when(direccionRepository.findById(10L)).thenReturn(Optional.of(direccionBase));

            Direccion resultado = service.obtenerPorId(10L);

            assertThat(resultado).isEqualTo(direccionBase);
        }

        @Test
        @DisplayName("lanza RuntimeException cuando la direccion no existe")
        void lanzaExcepcionCuandoNoExiste() {
            when(direccionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.obtenerPorId(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no existe");
        }
    }

    @Nested
    @DisplayName("obtenerPredeterminada")
    class ObtenerPredeterminada {
        @Test
        @DisplayName("retorna la direccion predeterminada")
        void obtienePredeterminadaExitosamente() {
            direccionBase.setEsPredeterminada(true);
            when(direccionRepository.findByUsuarioIdAndEsPredeterminadaTrue(usuarioId)).thenReturn(Optional.of(direccionBase));

            Direccion resultado = service.obtenerPredeterminada(usuarioId);

            assertThat(resultado).isEqualTo(direccionBase);
        }

        @Test
        @DisplayName("lanza RuntimeException cuando no hay direccion predeterminada")
        void lanzaExcepcionCuandoNoHayPredeterminada() {
            when(direccionRepository.findByUsuarioIdAndEsPredeterminadaTrue(usuarioId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.obtenerPredeterminada(usuarioId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no tiene una direcci");
        }
    }

    @Nested
    @DisplayName("editarDireccion")
    class EditarDireccion {
        @Test
        @DisplayName("edita los datos de la direccion correctamente")
        void editaDireccionExitosamente() {
            Direccion datosNuevos = Direccion.builder()
                    .calle("Calle Modificada")
                    .numero("456")
                    .ciudad("Valparaíso")
                    .region("V")
                    .destinatario("Horacio Modificado")
                    .esPredeterminada(false)
                    .build();

            when(direccionRepository.findById(10L)).thenReturn(Optional.of(direccionBase));
            when(direccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Direccion resultado = service.editarDireccion(10L, datosNuevos);

            assertThat(resultado.getCalle()).isEqualTo("Calle Modificada");
            assertThat(resultado.getNumero()).isEqualTo("456");
            assertThat(resultado.getCiudad()).isEqualTo("Valparaíso");
            assertThat(resultado.getRegion()).isEqualTo("V");
            assertThat(resultado.getDestinatario()).isEqualTo("Horacio Modificado");
        }

        @Test
        @DisplayName("lanza RecursoNoEncontradoException cuando la direccion no existe")
        void editaDireccionNoExistenteLanzaExcepcion() {
            when(direccionRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.editarDireccion(999L, direccionBase))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessage("Dirección no encontrada.");
        }

        @Test
        @DisplayName("marca otras direcciones como no predeterminadas al editar una como predeterminada")
        void editaDireccionComoPredeterminada() {
            Direccion datosNuevos = Direccion.builder()
                    .calle("Calle Modificada")
                    .esPredeterminada(true)
                    .build();

            List<Direccion> existentes = new ArrayList<>();
            existentes.add(direccionBase);

            when(direccionRepository.findById(10L)).thenReturn(Optional.of(direccionBase));
            when(direccionRepository.findByUsuarioId(usuarioId)).thenReturn(existentes);
            when(direccionRepository.saveAll(any())).thenReturn(existentes);
            when(direccionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.editarDireccion(10L, datosNuevos);

            verify(direccionRepository).findByUsuarioId(usuarioId);
            verify(direccionRepository).saveAll(any());
        }
    }

    @Nested
    @DisplayName("eliminarDireccion")
    class EliminarDireccion {
        @Test
        @DisplayName("elimina la direccion correctamente")
        void eliminaDireccionExitosamente() {
            doNothing().when(direccionRepository).deleteById(10L);

            service.eliminarDireccion(10L);

            verify(direccionRepository).deleteById(10L);
        }
    }
}
