package com.ecomarket.gestiontiendaservice.service;

import com.ecomarket.gestiontiendaservice.client.RegistroUsuariosClient;
import com.ecomarket.gestiontiendaservice.dto.SucursalRequestDTO;
import com.ecomarket.gestiontiendaservice.exception.NoExisteEnBdException;
import com.ecomarket.gestiontiendaservice.exception.YaExisteEnBdException;
import com.ecomarket.gestiontiendaservice.model.*;
import com.ecomarket.gestiontiendaservice.repository.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionTiendaServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;
    @Mock
    private PermisoPOSRepository permisoPOSRepository;
    @Mock
    private ReglamentoInternoRepository reglamentoInternoRepository;
    @Mock
    private HorarioAtencionRepository horarioAtencionRepository;
    @Mock
    private TareaPersonalRepository tareaPersonalRepository;
    @Mock
    private EstadoTareaPersonalRepository estadoTareaPersonalRepository;
    @Mock
    private RegistroUsuariosClient registroUsuariosClient;

    @InjectMocks
    private GestionTiendaService service;

    private Sucursal sucursalActiva(Long id, String nombre) {
        Sucursal s = new Sucursal();
        s.setId(id);
        s.setNombre(nombre);
        s.setDireccion("Av. Principal 123");
        s.setTelefono("412345678");
        s.setGerenteCargoId(10L);
        s.setActiva(true);
        return s;
    }

    private EstadoTareaPersonal estadoPendiente() {
        EstadoTareaPersonal e = new EstadoTareaPersonal();
        e.setId(1L);
        e.setNombre("PENDIENTE");
        return e;
    }

    private PermisoPOS permisoPos(Long id, Long rolEmpleado,
            boolean anulaciones, boolean apertura, boolean descuento) {
        PermisoPOS p = new PermisoPOS();
        p.setId(id);
        p.setRolEmpleado(rolEmpleado);
        p.setPermiteAnulaciones(anulaciones);
        p.setPermiteAperturaCaja(apertura);
        p.setPermiteAplicarDescuentoManual(descuento);
        return p;
    }

    @Nested
    @DisplayName("registrarSucursal")
    class RegistrarSucursal {

        @Test
        @DisplayName("persiste la sucursal con activa=true y gerenteCargoId")
        void registraSucursalExitosamente() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(10L)).thenReturn("GERENTE");
            when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(i -> {
                Sucursal s = i.getArgument(0);
                s.setId(1L);
                return s;
            });

            SucursalRequestDTO dto = new SucursalRequestDTO();
            dto.setNombre("Sucursal Centro");
            dto.setDireccion("Barros Arana 500");
            dto.setTelefono("412345678");
            dto.setGerenteCargoId(10L);

            Sucursal resultado = service.registrarSucursal(dto);

            assertThat(resultado.getNombre()).isEqualTo("Sucursal Centro");
            assertThat(resultado.getActiva()).isTrue();
            assertThat(resultado.getGerenteCargoId()).isEqualTo(10L);
            verify(sucursalRepository).save(argThat(s -> Boolean.TRUE.equals(s.getActiva())));
        }

        @Test
        @DisplayName("registra sucursal sin gerente cuando gerenteCargoId es null")
        void registraSucursalSinGerente() {
            when(sucursalRepository.save(any(Sucursal.class))).thenAnswer(i -> {
                Sucursal s = i.getArgument(0);
                s.setId(2L);
                return s;
            });

            SucursalRequestDTO dto = new SucursalRequestDTO();
            dto.setNombre("Sucursal Norte");
            dto.setDireccion("Av. Norte 123");
            dto.setTelefono("411111111");

            Sucursal resultado = service.registrarSucursal(dto);

            assertThat(resultado.getGerenteCargoId()).isNull();
            verify(registroUsuariosClient, never()).obtenerRolNombrePorUsuarioId(any());
        }

        @Test
        @DisplayName("lanza excepcion si el gerente no tiene rol GERENTE")
        void registraSucursalConRolInvalido() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(10L)).thenReturn("EMPLEADO");

            SucursalRequestDTO dto = new SucursalRequestDTO();
            dto.setNombre("Sucursal Centro");
            dto.setDireccion("Barros Arana 500");
            dto.setGerenteCargoId(10L);

            assertThatThrownBy(() -> service.registrarSucursal(dto))
                    .isInstanceOf(YaExisteEnBdException.class)
                    .hasMessageContaining("GERENTE");
        }

        @Test
        @DisplayName("lanza excepcion si el gerente no existe")
        void registraSucursalConGerenteInexistente() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(99L)).thenReturn(null);

            SucursalRequestDTO dto = new SucursalRequestDTO();
            dto.setNombre("Sucursal Centro");
            dto.setDireccion("Barros Arana 500");
            dto.setGerenteCargoId(99L);

            assertThatThrownBy(() -> service.registrarSucursal(dto))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("el repositorio save es invocado exactamente una vez")
        void saveInvocadoUnaVez() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(1L)).thenReturn("GERENTE");
            when(sucursalRepository.save(any())).thenReturn(sucursalActiva(1L, "S1"));

            SucursalRequestDTO dto = new SucursalRequestDTO();
            dto.setNombre("S1");
            dto.setDireccion("Dir");
            dto.setTelefono("Tel");
            dto.setGerenteCargoId(1L);

            service.registrarSucursal(dto);

            verify(sucursalRepository, times(1)).save(any());
        }
    }

    @Nested
    @DisplayName("obtenerDatosSucursal")
    class ObtenerDatosSucursal {

        @Test
        @DisplayName("retorna la sucursal cuando el ID existe")
        void retornaSucursalExistente() {
            Sucursal sucursal = sucursalActiva(1L, "Centro");
            when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));

            Sucursal resultado = service.obtenerDatosSucursal(1L);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNombre()).isEqualTo("Centro");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException si la sucursal no existe")
        void sucursalInexistenteLanzaExcepcion() {
            when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.obtenerDatosSucursal(99L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("listarSucursalesActivas")
    class ListarSucursalesActivas {

        @Test
        @DisplayName("delega en findByActivaTrue y retorna la lista del repositorio")
        void delegaEnRepository() {
            List<Sucursal> activas = List.of(
                    sucursalActiva(1L, "Norte"),
                    sucursalActiva(2L, "Sur"));
            when(sucursalRepository.findByActivaTrue()).thenReturn(activas);

            List<Sucursal> resultado = service.listarSucursalesActivas();

            assertThat(resultado).hasSize(2);
            verify(sucursalRepository).findByActivaTrue();
        }
    }

    @Nested
    @DisplayName("asignarGerente")
    class AsignarGerente {

        @Test
        @DisplayName("asigna gerente a sucursal existente")
        void asignaGerenteExitosamente() {
            Sucursal sucursal = sucursalActiva(1L, "Centro");
            sucursal.setGerenteCargoId(null);
            when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(20L)).thenReturn("GERENTE");
            when(sucursalRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Sucursal resultado = service.asignarGerente(1L, 20L);

            assertThat(resultado.getGerenteCargoId()).isEqualTo(20L);
        }

        @Test
        @DisplayName("lanza excepcion si la sucursal no existe")
        void sucursalNoExiste() {
            when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.asignarGerente(99L, 20L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza excepcion si el usuario no es GERENTE")
        void usuarioNoEsGerente() {
            Sucursal sucursal = sucursalActiva(1L, "Centro");
            when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal));
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(20L)).thenReturn("EMPLEADO");

            assertThatThrownBy(() -> service.asignarGerente(1L, 20L))
                    .isInstanceOf(YaExisteEnBdException.class)
                    .hasMessageContaining("EMPLEADO");
        }
    }

    @Nested
    @DisplayName("asignarTareaPersonal")
    class AsignarTareaPersonal {

        @Test
        @DisplayName("crea tarea en estado PENDIENTE con todos los campos correctos")
        void asignaTareaExitosamente() {
            when(estadoTareaPersonalRepository.findByNombre("PENDIENTE"))
                    .thenReturn(Optional.of(estadoPendiente()));
            when(tareaPersonalRepository.save(any(TareaPersonal.class))).thenAnswer(i -> {
                TareaPersonal t = i.getArgument(0);
                t.setId(1L);
                return t;
            });
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(5L)).thenReturn("EMPLEADO");

            LocalDateTime limite = LocalDateTime.now().plusDays(7);
            TareaPersonal tarea = new TareaPersonal();
            tarea.setEmpleadoId(5L);
            tarea.setSucursalId(1L);
            tarea.setTitulo("Inventariar bodega");
            tarea.setDescripcion("Contar ítems de bodega");
            tarea.setFechaLimite(limite);

            TareaPersonal resultado = service.asignarTareaPersonal(tarea);

            assertThat(resultado.getEmpleadoId()).isEqualTo(5L);
            assertThat(resultado.getSucursalId()).isEqualTo(1L);
            assertThat(resultado.getTitulo()).isEqualTo("Inventariar bodega");
            assertThat(resultado.getEstado().getNombre()).isEqualTo("PENDIENTE");
            assertThat(resultado.getFechaAsignacion()).isNotNull();
            assertThat(resultado.getFechaLimite()).isEqualTo(limite);
        }

        @Test
        @DisplayName("asigna tarea con gerenteAsignadoId cuando se provee")
        void asignaTareaConGerente() {
            when(estadoTareaPersonalRepository.findByNombre("PENDIENTE"))
                    .thenReturn(Optional.of(estadoPendiente()));
            when(tareaPersonalRepository.save(any())).thenAnswer(i -> i.getArgument(0));
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(5L)).thenReturn("EMPLEADO");
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(10L)).thenReturn("GERENTE");

            TareaPersonal tarea = new TareaPersonal();
            tarea.setEmpleadoId(5L);
            tarea.setGerenteAsignadoId(10L);
            tarea.setSucursalId(1L);
            tarea.setTitulo("Tarea con gerente");
            tarea.setDescripcion("Desc");

            TareaPersonal resultado = service.asignarTareaPersonal(tarea);

            assertThat(resultado.getGerenteAsignadoId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException si el estado PENDIENTE no existe en BD")
        void estadoPendienteNoEncontradoLanzaExcepcion() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(1L)).thenReturn("EMPLEADO");
            when(estadoTareaPersonalRepository.findByNombre("PENDIENTE"))
                    .thenReturn(Optional.empty());

            TareaPersonal tarea = new TareaPersonal();
            tarea.setEmpleadoId(1L);
            tarea.setSucursalId(1L);
            tarea.setTitulo("Tarea");

            assertThatThrownBy(() -> service.asignarTareaPersonal(tarea))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("PENDIENTE");
        }

        @Test
        @DisplayName("lanza excepcion si el empleado no tiene rol EMPLEADO")
        void empleadoRolInvalido() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(1L)).thenReturn("GERENTE");

            TareaPersonal tarea = new TareaPersonal();
            tarea.setEmpleadoId(1L);
            tarea.setSucursalId(1L);
            tarea.setTitulo("Tarea");

            assertThatThrownBy(() -> service.asignarTareaPersonal(tarea))
                    .isInstanceOf(YaExisteEnBdException.class)
                    .hasMessageContaining("EMPLEADO");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException si el empleado no existe")
        void empleadoInexistente() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(99L)).thenReturn(null);

            TareaPersonal tarea = new TareaPersonal();
            tarea.setEmpleadoId(99L);
            tarea.setSucursalId(1L);
            tarea.setTitulo("Tarea");

            assertThatThrownBy(() -> service.asignarTareaPersonal(tarea))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("actualizarEstadoTarea")
    class ActualizarEstadoTarea {

        @Test
        @DisplayName("actualiza el estado de la tarea correctamente")
        void actualizaEstadoExitosamente() {
            TareaPersonal tarea = new TareaPersonal();
            tarea.setId(1L);
            tarea.setEstado(estadoPendiente());

            EstadoTareaPersonal completado = new EstadoTareaPersonal();
            completado.setId(2L);
            completado.setNombre("COMPLETADO");

            when(tareaPersonalRepository.findById(1L)).thenReturn(Optional.of(tarea));
            when(estadoTareaPersonalRepository.findById(2L)).thenReturn(Optional.of(completado));
            when(tareaPersonalRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            TareaPersonal resultado = service.actualizarEstadoTarea(1L, 2L);

            assertThat(resultado.getEstado().getNombre()).isEqualTo("COMPLETADO");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException si la tarea no existe")
        void tareaInexistenteLanzaExcepcion() {
            when(tareaPersonalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarEstadoTarea(99L, 1L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException si el estado no existe")
        void estadoInexistenteLanzaExcepcion() {
            TareaPersonal tarea = new TareaPersonal();
            tarea.setId(1L);
            when(tareaPersonalRepository.findById(1L)).thenReturn(Optional.of(tarea));
            when(estadoTareaPersonalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarEstadoTarea(1L, 99L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("administrarHorario")
    class AdministrarHorario {

        @Test
        @DisplayName("elimina horarios existentes y guarda los nuevos para la sucursal")
        void reemplazaHorariosExitosamente() {
            HorarioAtencion horarioExistente = new HorarioAtencion();
            horarioExistente.setId(1L);
            horarioExistente.setSucursalId(1L);

            when(horarioAtencionRepository.findBySucursalId(1L))
                    .thenReturn(List.of(horarioExistente));

            HorarioAtencion nuevoHorario = new HorarioAtencion();
            nuevoHorario.setDiaSemana(1);
            nuevoHorario.setHoraApertura("09:00");
            nuevoHorario.setHoraCierre("20:00");
            nuevoHorario.setEsFeriado(false);

            Boolean resultado = service.administrarHorario(1L, List.of(nuevoHorario));

            assertThat(resultado).isTrue();
            verify(horarioAtencionRepository).saveAll(argThat(iterable -> {
                List<HorarioAtencion> h = (List<HorarioAtencion>) iterable;
                return h.size() == 1 &&
                        h.get(0).getSucursalId().equals(1L) &&
                        h.get(0).getDiaSemana() == 1 &&
                        "09:00".equals(h.get(0).getHoraApertura()) &&
                        "20:00".equals(h.get(0).getHoraCierre()) &&
                        Boolean.FALSE.equals(h.get(0).getEsFeriado());
            }));
        }

        @Test
        @DisplayName("si no hay horarios previos, guarda los nuevos igualmente")
        void guardaHorariosNuevosSinPrevios() {
            when(horarioAtencionRepository.findBySucursalId(2L)).thenReturn(List.of());

            HorarioAtencion nuevo = new HorarioAtencion();
            nuevo.setDiaSemana(2);
            nuevo.setHoraApertura("08:00");
            nuevo.setHoraCierre("18:00");
            nuevo.setEsFeriado(false);

            Boolean resultado = service.administrarHorario(2L, List.of(nuevo));

            assertThat(resultado).isTrue();
            verify(horarioAtencionRepository).saveAll(any());
        }
    }

    @Nested
    @DisplayName("configurarPermisoPOS")
    class ConfigurarPermisoPOS {

        @Test
        @DisplayName("persiste el permiso POS validando rol EMPLEADO")
        void guardarPermisoExitosamente() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(1L)).thenReturn("EMPLEADO");

            PermisoPOS permiso = permisoPos(null, 1L, true, false, true);
            PermisoPOS guardado = permisoPos(1L, 1L, true, false, true);

            when(permisoPOSRepository.save(permiso)).thenReturn(guardado);

            PermisoPOS resultado = service.configurarPermisoPOS(permiso);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getPermiteAnulaciones()).isTrue();
            assertThat(resultado.getPermiteAperturaCaja()).isFalse();
            assertThat(resultado.getPermiteAplicarDescuentoManual()).isTrue();
        }

        @Test
        @DisplayName("lanza excepcion si el rol no es EMPLEADO")
        void permisoConRolInvalido() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(1L)).thenReturn("GERENTE");

            PermisoPOS permiso = permisoPos(null, 1L, true, false, true);

            assertThatThrownBy(() -> service.configurarPermisoPOS(permiso))
                    .isInstanceOf(YaExisteEnBdException.class)
                    .hasMessageContaining("EMPLEADO");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException si el empleado no existe")
        void permisoConEmpleadoInexistente() {
            when(registroUsuariosClient.obtenerRolNombrePorUsuarioId(99L)).thenReturn(null);

            PermisoPOS permiso = permisoPos(null, 99L, true, false, true);

            assertThatThrownBy(() -> service.configurarPermisoPOS(permiso))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("establecerReglamento")
    class EstablecerReglamento {

        @Test
        @DisplayName("persiste el reglamento y retorna el objeto guardado")
        void guardaReglamentoExitosamente() {
            ReglamentoInterno reglamento = new ReglamentoInterno();
            reglamento.setId(1L);
            reglamento.setSucursalId(1L);
            reglamento.setVersion(1);
            reglamento.setTituloSeccion("Normas de convivencia");
            reglamento.setContenidoLegal("Respetar horarios y uniforme");
            reglamento.setFechaVigencia(LocalDateTime.now().plusDays(30));

            when(reglamentoInternoRepository.save(reglamento)).thenReturn(reglamento);

            ReglamentoInterno resultado = service.establecerReglamento(reglamento);

            assertThat(resultado.getTituloSeccion()).isEqualTo("Normas de convivencia");
            assertThat(resultado.getContenidoLegal()).contains("uniforme");
            verify(reglamentoInternoRepository).save(reglamento);
        }
    }

    @Nested
    @DisplayName("consultarHorariosTienda")
    class ConsultarHorariosTienda {

        @Test
        @DisplayName("retorna horarios de la sucursal")
        void retornaHorarios() {
            HorarioAtencion h = new HorarioAtencion();
            h.setId(1L);
            h.setSucursalId(1L);
            h.setDiaSemana(1);
            h.setHoraApertura("09:00");
            h.setHoraCierre("20:00");

            when(horarioAtencionRepository.findBySucursalId(1L)).thenReturn(List.of(h));

            List<HorarioAtencion> resultado = service.consultarHorariosTienda(1L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getDiaSemana()).isEqualTo(1);
            verify(horarioAtencionRepository).findBySucursalId(1L);
        }

        @Test
        @DisplayName("retorna lista vacia si no hay horarios")
        void retornaVaciaSinHorarios() {
            when(horarioAtencionRepository.findBySucursalId(99L)).thenReturn(List.of());

            List<HorarioAtencion> resultado = service.consultarHorariosTienda(99L);

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("listarEstadosTarea")
    class ListarEstadosTarea {

        @Test
        @DisplayName("retorna todos los estados")
        void listarEstados() {
            when(estadoTareaPersonalRepository.findAll())
                    .thenReturn(List.of(estadoPendiente(), new EstadoTareaPersonal()));

            List<EstadoTareaPersonal> resultado = service.listarEstadosTarea();

            assertThat(resultado).hasSize(2);
        }
    }

    @Nested
    @DisplayName("obtenerEstadoTarea")
    class ObtenerEstadoTarea {

        @Test
        @DisplayName("retorna estado cuando existe")
        void existe() {
            when(estadoTareaPersonalRepository.findById(1L))
                    .thenReturn(Optional.of(estadoPendiente()));

            EstadoTareaPersonal resultado = service.obtenerEstadoTarea(1L);

            assertThat(resultado.getNombre()).isEqualTo("PENDIENTE");
        }

        @Test
        @DisplayName("lanza excepcion cuando no existe")
        void noExiste() {
            when(estadoTareaPersonalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.obtenerEstadoTarea(99L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("crearEstadoTarea")
    class CrearEstadoTarea {

        @Test
        @DisplayName("persiste y retorna el nuevo estado")
        void crear() {
            EstadoTareaPersonal nuevo = new EstadoTareaPersonal();
            nuevo.setNombre("EN_PROCESO");
            EstadoTareaPersonal guardado = new EstadoTareaPersonal();
            guardado.setId(1L);
            guardado.setNombre("EN_PROCESO");

            when(estadoTareaPersonalRepository.save(nuevo)).thenReturn(guardado);

            EstadoTareaPersonal resultado = service.crearEstadoTarea(nuevo);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNombre()).isEqualTo("EN_PROCESO");
        }
    }

    @Nested
    @DisplayName("editarEstadoTarea")
    class EditarEstadoTarea {

        @Test
        @DisplayName("actualiza nombre del estado existente")
        void editar() {
            EstadoTareaPersonal existente = new EstadoTareaPersonal();
            existente.setId(1L);
            existente.setNombre("PENDIENTE");

            EstadoTareaPersonal datos = new EstadoTareaPersonal();
            datos.setNombre("EN_PROCESO");

            when(estadoTareaPersonalRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(estadoTareaPersonalRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            EstadoTareaPersonal resultado = service.editarEstadoTarea(1L, datos);

            assertThat(resultado.getNombre()).isEqualTo("EN_PROCESO");
        }

        @Test
        @DisplayName("lanza excepcion si el estado no existe")
        void noExiste() {
            when(estadoTareaPersonalRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.editarEstadoTarea(99L, new EstadoTareaPersonal()))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("eliminarEstadoTarea")
    class EliminarEstadoTarea {

        @Test
        @DisplayName("elimina estado existente y retorna true")
        void elimina() {
            when(estadoTareaPersonalRepository.existsById(1L)).thenReturn(true);

            boolean resultado = service.eliminarEstadoTarea(1L);

            assertThat(resultado).isTrue();
            verify(estadoTareaPersonalRepository).deleteById(1L);
        }

        @Test
        @DisplayName("retorna false si el estado no existe")
        void noExiste() {
            when(estadoTareaPersonalRepository.existsById(99L)).thenReturn(false);

            boolean resultado = service.eliminarEstadoTarea(99L);

            assertThat(resultado).isFalse();
            verify(estadoTareaPersonalRepository, never()).deleteById(any());
        }
    }
}
