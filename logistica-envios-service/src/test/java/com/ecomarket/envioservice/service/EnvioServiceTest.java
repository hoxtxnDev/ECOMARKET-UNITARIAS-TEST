package com.ecomarket.envioservice.service;

import com.ecomarket.envioservice.client.AnaliticaMetricaClient;
import com.ecomarket.envioservice.client.SoporteNotificacionClient;
import com.ecomarket.envioservice.dto.ClienteDTO;
import com.ecomarket.envioservice.dto.PedidoDTO;
import com.ecomarket.envioservice.dto.TransportistaDTO;
import com.ecomarket.envioservice.exception.EnvioEstadoInvalidoException;
import com.ecomarket.envioservice.exception.NoExisteEnBdException;
import com.ecomarket.envioservice.exception.PedidoClienteIncompatibleException;
import com.ecomarket.envioservice.model.entity.Envio;
import com.ecomarket.envioservice.model.entity.HistorialEnvio;
import com.ecomarket.envioservice.model.entity.PuntoRetiro;
import com.ecomarket.envioservice.model.entity.RutaTransporte;
import com.ecomarket.envioservice.model.reference.EstadoEnvio;
import com.ecomarket.envioservice.model.reference.MetodoEnvio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnvioService")
class EnvioServiceTest {

    @Mock EnvioDomainService envioDomainService;
    @Mock HistorialEnvioService historialEnvioService;
    @Mock RutaTransporteService rutaTransporteService;
    @Mock EstadoEnvioService estadoEnvioService;
    @Mock MetodoEnvioService metodoEnvioService;
    @Mock DireccionService direccionService;
    @Mock PuntoRetiroService puntoRetiroService;
    @Mock RestTemplate restTemplate;
    @Mock SoporteNotificacionClient soporteNotificacionClient;
    @Mock AnaliticaMetricaClient analiticaMetricaClient;
    @InjectMocks EnvioService envioService;

    @Captor ArgumentCaptor<Envio> envioCaptor;
    @Captor ArgumentCaptor<HistorialEnvio> historialCaptor;
    @Captor ArgumentCaptor<RutaTransporte> rutaCaptor;

    private EstadoEnvio estadoPendiente;
    private EstadoEnvio estadoEntregado;
    private EstadoEnvio estadoCancelado;
    private EstadoEnvio estadoPuntoRetiro;
    private MetodoEnvio metodoDomicilio;
    private MetodoEnvio metodoRetiro;
    private Envio envioPendiente;

    @BeforeEach
    void setup() {
        estadoPendiente = new EstadoEnvio(1L, "PENDIENTE");
        estadoEntregado = new EstadoEnvio(4L, "ENTREGADO");
        estadoCancelado = new EstadoEnvio(5L, "CANCELADO");
        estadoPuntoRetiro = new EstadoEnvio(3L, "EN_PUNTO_RETIRO");

        metodoDomicilio = new MetodoEnvio(1L, "Domicilio", 5000.0);
        metodoRetiro = new MetodoEnvio(2L, "PuntoRetiro", 0.0);

        envioPendiente = new Envio();
        envioPendiente.setId(10L);
        envioPendiente.setPedidoId(100L);
        envioPendiente.setClienteId(5L);
        envioPendiente.setMetodoEnvio(metodoDomicilio);
        envioPendiente.setEstadoActual(estadoPendiente);
        envioPendiente.setDireccionId(1L);
        envioPendiente.setCostoEnvio(5000.0);
        envioPendiente.setFechaCreacion(LocalDateTime.now());

        ReflectionTestUtils.setField(envioService, "usuariosUrl", "http://localhost:8085");
        ReflectionTestUtils.setField(envioService, "pedidosUrl", "http://localhost:8082");
    }

    @Nested
    @DisplayName("crearEnvio")
    class CrearEnvio {

        @Test
        @DisplayName("crea envio exitosamente con cliente y pedido validos")
        void crearExitoso() throws Exception {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);
            when(restTemplate.getForObject(anyString(), eq(ClienteDTO.class))).thenReturn(new ClienteDTO());
            PedidoDTO pedidoDto = new PedidoDTO();
            pedidoDto.setClienteId(5L);
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedidoDto);
            when(estadoEnvioService.findById(1L)).thenReturn(estadoPendiente);
            when(envioDomainService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Envio resultado = envioService.crearEnvio(100L, 5L, 1L, 1L);

            assertThat(resultado.getPedidoId()).isEqualTo(100L);
            assertThat(resultado.getClienteId()).isEqualTo(5L);
            assertThat(resultado.getEstadoActual().getNombre()).isEqualTo("PENDIENTE");
            assertThat(resultado.getCostoEnvio()).isEqualTo(5000.0);
            verify(historialEnvioService).save(any(HistorialEnvio.class));
            verify(soporteNotificacionClient).notificarCreacionEnvio(5L, 100L, resultado.getId());
            verify(analiticaMetricaClient).registrarMetrica("envios.creados", 1.0, "Envio #" + resultado.getId() + " creado para pedido #100");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando la direccion no existe")
        void direccionNoExiste() {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            when(restTemplate.getForObject(anyString(), eq(Object.class)))
                    .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

            assertThatThrownBy(() -> envioService.crearEnvio(100L, 5L, 1L, 1L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("direccion");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando servicio de usuarios no disponible para validar direccion")
        void servicioDireccionNoDisponible() {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            when(restTemplate.getForObject(anyString(), eq(Object.class)))
                    .thenThrow(new ResourceAccessException("Connection refused"));

            assertThatThrownBy(() -> envioService.crearEnvio(100L, 5L, 1L, 1L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("servicio de usuarios");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando el cliente no existe")
        void clienteNoExiste() {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);
            when(restTemplate.getForObject(anyString(), eq(ClienteDTO.class)))
                    .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

            assertThatThrownBy(() -> envioService.crearEnvio(100L, 99L, 1L, 1L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("cliente");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando servicio de usuarios no disponible")
        void servicioClientesNoDisponible() {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);
            when(restTemplate.getForObject(anyString(), eq(ClienteDTO.class)))
                    .thenThrow(new ResourceAccessException("Connection refused"));

            assertThatThrownBy(() -> envioService.crearEnvio(100L, 5L, 1L, 1L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("servicio de usuarios");
        }

        @Test
        @DisplayName("lanza PedidoClienteIncompatibleException cuando el pedido no pertenece al cliente")
        void pedidoNoPerteneceAlCliente() throws Exception {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);
            when(restTemplate.getForObject(anyString(), eq(ClienteDTO.class))).thenReturn(new ClienteDTO());
            PedidoDTO pedidoDto = new PedidoDTO();
            pedidoDto.setClienteId(99L);
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedidoDto);

            assertThatThrownBy(() -> envioService.crearEnvio(100L, 5L, 1L, 1L))
                    .isInstanceOf(PedidoClienteIncompatibleException.class)
                    .hasMessageContaining("compatible");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando el pedido no existe (404)")
        void pedidoNoExiste() {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            
            when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);
            when(restTemplate.getForObject(anyString(), eq(ClienteDTO.class))).thenReturn(new ClienteDTO());
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

            assertThatThrownBy(() -> envioService.crearEnvio(100L, 5L, 1L, 1L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("pedido");
        }

        @Test
        @DisplayName("lanza Exception generica cuando el servicio de pedidos devuelve error HTTP inesperado")
        void pedidoErrorHttpInesperado() {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            
            when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);
            when(restTemplate.getForObject(anyString(), eq(ClienteDTO.class))).thenReturn(new ClienteDTO());
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class)))
                    .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

            assertThatThrownBy(() -> envioService.crearEnvio(100L, 5L, 1L, 1L))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando servicio de pedidos no disponible")
        void servicioPedidosNoDisponible() {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            
            when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);
            when(restTemplate.getForObject(anyString(), eq(ClienteDTO.class))).thenReturn(new ClienteDTO());
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class)))
                    .thenThrow(new ResourceAccessException("Connection refused"));

            assertThatThrownBy(() -> envioService.crearEnvio(100L, 5L, 1L, 1L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("servicio de pedidos");
        }

        @Test
        @DisplayName("crea envio cuando el servicio de pedidos retorna null (pedido null, no incompatible)")
        void pedidoNullNoLanzaIncompatibilidad() throws Exception {
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            
            when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);
            when(restTemplate.getForObject(anyString(), eq(ClienteDTO.class))).thenReturn(new ClienteDTO());
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(null);
            when(estadoEnvioService.findById(1L)).thenReturn(estadoPendiente);
            when(envioDomainService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Envio resultado = envioService.crearEnvio(100L, 5L, 1L, 1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getPedidoId()).isEqualTo(100L);
            verify(historialEnvioService).save(any(HistorialEnvio.class));
        }

        @Test
        @DisplayName("usa costo 0 para metodo PuntoRetiro")
        void costoGratisParaPuntoRetiro() throws Exception {
            when(metodoEnvioService.findById(2L)).thenReturn(metodoRetiro);
            
            when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);
            when(restTemplate.getForObject(anyString(), eq(ClienteDTO.class))).thenReturn(new ClienteDTO());
            PedidoDTO pedidoDto = new PedidoDTO();
            pedidoDto.setClienteId(5L);
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedidoDto);
            when(estadoEnvioService.findById(1L)).thenReturn(estadoPendiente);
            when(envioDomainService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Envio resultado = envioService.crearEnvio(100L, 5L, 2L, 1L);

            assertThat(resultado.getCostoEnvio()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("crearEnvioAutomatico")
    class CrearEnvioAutomatico {

        private PedidoDTO pedidoDto;

        @BeforeEach
        void setup() {
            pedidoDto = new PedidoDTO();
            pedidoDto.setId(200L);
            pedidoDto.setClienteId(5L);
            pedidoDto.setDireccionEnvioId(1L);
        }

        @Test
        @DisplayName("crea envio automaticamente a partir de pedido")
        void exito() throws Exception {
            when(restTemplate.getForObject("http://localhost:8082/api/pedidos/200", PedidoDTO.class)).thenReturn(pedidoDto);
            when(metodoEnvioService.findById(1L)).thenReturn(metodoDomicilio);
            when(estadoEnvioService.findById(1L)).thenReturn(estadoPendiente);
            when(envioDomainService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Envio resultado = envioService.crearEnvioAutomatico(200L);

            assertThat(resultado.getPedidoId()).isEqualTo(200L);
            assertThat(resultado.getCostoEnvio()).isEqualTo(5000.0);
        }

        @Test
        @DisplayName("lanza excepcion si pedido no existe")
        void pedidoNoExiste() {
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(null);

            assertThatThrownBy(() -> envioService.crearEnvioAutomatico(200L))
                .isInstanceOf(NoExisteEnBdException.class)
                .hasMessageContaining("Pedido no encontrado");
        }

        @Test
        @DisplayName("lanza excepcion si pedido no tiene direccion asignada")
        void pedidoSinDireccion() {
            pedidoDto.setDireccionEnvioId(null);
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedidoDto);

            assertThatThrownBy(() -> envioService.crearEnvioAutomatico(200L))
                .isInstanceOf(NoExisteEnBdException.class)
                .hasMessageContaining("dirección de envío");
        }

        @Test
        @DisplayName("lanza excepcion si el metodo de envio por defecto no existe")
        void metodoEnvioNoExiste() {
            when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedidoDto);
            when(metodoEnvioService.findById(1L)).thenThrow(new NoExisteEnBdException("MetodoEnvio no encontrado"));

            assertThatThrownBy(() -> envioService.crearEnvioAutomatico(200L))
                .isInstanceOf(NoExisteEnBdException.class)
                .hasMessageContaining("MetodoEnvio");
        }
    }

    @Nested
    @DisplayName("consultarEstadoEnvio")
    class ConsultarEstado {

        @Test
        @DisplayName("retorna el estado actual del envio")
        void retornaEstado() {
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);

            EstadoEnvio resultado = envioService.consultarEstadoEnvio(10L);

            assertThat(resultado.getNombre()).isEqualTo("PENDIENTE");
        }
    }

    @Nested
    @DisplayName("actualizarEstado")
    class ActualizarEstado {

        @Test
        @DisplayName("actualiza el estado y registra historial")
        void actualizaExitoso() {
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            when(estadoEnvioService.findById(4L)).thenReturn(estadoEntregado);
            when(envioDomainService.save(any())).thenReturn(envioPendiente);
            when(historialEnvioService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            HistorialEnvio resultado = envioService.actualizarEstado(10L, 4L, "Entregado correctamente");

            assertThat(resultado.getEstado().getNombre()).isEqualTo("ENTREGADO");
            assertThat(resultado.getObservacion()).isEqualTo("Entregado correctamente");
            verify(analiticaMetricaClient).registrarMetrica(eq("envios.estado.cambiado"), eq(1.0), anyString());
        }

        @Test
        @DisplayName("establece fechaEntregaReal cuando el estado es final")
        void estadoFinalAsignaFechaReal() {
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            when(estadoEnvioService.findById(4L)).thenReturn(estadoEntregado);
            when(envioDomainService.save(envioCaptor.capture())).thenReturn(envioPendiente);
            when(historialEnvioService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            envioService.actualizarEstado(10L, 4L, null);

            assertThat(envioCaptor.getValue().getFechaEntregaReal()).isNotNull();
        }

        @Test
        @DisplayName("usa mensaje por defecto cuando observacion es nula")
        void observacionNulaUsaMensajePorDefecto() {
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            when(estadoEnvioService.findById(4L)).thenReturn(estadoEntregado);
            when(envioDomainService.save(any())).thenReturn(envioPendiente);
            when(historialEnvioService.save(historialCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

            envioService.actualizarEstado(10L, 4L, null);

            assertThat(historialCaptor.getValue().getObservacion()).contains("ENTREGADO");
        }

        @Test
        @DisplayName("no establece fechaEntregaReal cuando el estado no es final")
        void estadoNoFinalNoAsignaFechaReal() {
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            EstadoEnvio estadoTransito = new EstadoEnvio(2L, "EN_TRANSITO");
            when(estadoEnvioService.findById(2L)).thenReturn(estadoTransito);
            when(envioDomainService.save(any())).thenReturn(envioPendiente);
            when(historialEnvioService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            envioService.actualizarEstado(10L, 2L, null);

            assertThat(envioPendiente.getFechaEntregaReal()).isNull();
        }

        @Test
        @DisplayName("establece fechaEntregaReal cuando el nuevo estado es CANCELADO (esEstadoFinal 5L)")
        void estadoCanceladoAsignaFechaReal() {
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            when(estadoEnvioService.findById(5L)).thenReturn(estadoCancelado);
            when(envioDomainService.save(envioCaptor.capture())).thenReturn(envioPendiente);
            when(historialEnvioService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            envioService.actualizarEstado(10L, 5L, null);

            assertThat(envioCaptor.getValue().getFechaEntregaReal()).isNotNull();
        }
    }

    @Nested
    @DisplayName("cancelarEnvio")
    class CancelarEnvio {

        @Test
        @DisplayName("cancela un envio pendiente exitosamente")
        void cancelarExitoso() {
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            when(estadoEnvioService.findById(5L)).thenReturn(estadoCancelado);
            when(envioDomainService.save(any())).thenReturn(envioPendiente);
            when(historialEnvioService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            Boolean resultado = envioService.cancelarEnvio(10L);

            assertThat(resultado).isTrue();
            verify(historialEnvioService).save(historialCaptor.capture());
            assertThat(historialCaptor.getValue().getObservacion()).isEqualTo("Envio cancelado.");
        }

        @Test
        @DisplayName("lanza EnvioEstadoInvalidoException si el envio ya esta en estado final")
        void envioEnEstadoFinalNoSePuedeCancelar() {
            envioPendiente.setEstadoActual(estadoEntregado);
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);

            assertThatThrownBy(() -> envioService.cancelarEnvio(10L))
                    .isInstanceOf(EnvioEstadoInvalidoException.class)
                    .hasMessageContaining("cancelar");
        }
    }

    @Nested
    @DisplayName("registrarRecepcion")
    class RegistrarRecepcion {

        @Test
        @DisplayName("registra recepcion con firma y cambia estado a ENTREGADO")
        void registrarExitoso() {
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            when(estadoEnvioService.findById(4L)).thenReturn(estadoEntregado);
            when(envioDomainService.save(any())).thenReturn(envioPendiente);

            Envio resultado = envioService.registrarRecepcion(10L, "Firma Juan");

            assertThat(resultado.getEstadoActual().getNombre()).isEqualTo("ENTREGADO");
            assertThat(resultado.getFechaEntregaReal()).isNotNull();
            verify(historialEnvioService).save(historialCaptor.capture());
            assertThat(historialCaptor.getValue().getObservacion()).contains("Firma Juan");
        }
    }

    @Nested
    @DisplayName("seleccionarPuntoRetiro")
    class SeleccionarPuntoRetiro {

        @Test
        @DisplayName("asigna punto de retiro y cambia estado")
        void seleccionExitoso() {
            PuntoRetiro punto = new PuntoRetiro();
            punto.setId(1L);
            punto.setNombre("Retiro Centro");
            doNothing().when(puntoRetiroService).verificarDisponibilidad(punto);
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            when(puntoRetiroService.findById(1L)).thenReturn(punto);
            when(envioDomainService.save(any())).thenReturn(envioPendiente);
            when(estadoEnvioService.findById(3L)).thenReturn(estadoPuntoRetiro);

            Envio resultado = envioService.seleccionarPuntoRetiro(10L, 1L, "Firma Maria");

            assertThat(resultado.getPuntoRetiro()).isEqualTo(punto);
            verify(historialEnvioService).save(historialCaptor.capture());
            assertThat(historialCaptor.getValue().getObservacion()).contains("Retiro Centro");
        }
    }

    @Nested
    @DisplayName("planificarRuta")
    class PlanificarRuta {

        @Test
        @DisplayName("planifica ruta con transportista y envios validos")
        void planificarExitoso() throws Exception {
            when(restTemplate.getForObject(anyString(), eq(TransportistaDTO.class))).thenReturn(new TransportistaDTO());
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            when(rutaTransporteService.save(any())).thenAnswer(inv -> inv.getArgument(0));

            RutaTransporte resultado = envioService.planificarRuta(1L, List.of(10L));

            assertThat(resultado.getTransportistaId()).isEqualTo(1L);
            assertThat(resultado.getCompletada()).isFalse();
            assertThat(resultado.getEnviosIds()).containsExactly(10L);
            verify(rutaTransporteService).save(rutaCaptor.capture());
            assertThat(rutaCaptor.getValue().getTransportistaId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException si transportista no existe")
        void transportistaNoExiste() {
            when(restTemplate.getForObject(anyString(), eq(TransportistaDTO.class)))
                    .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

            assertThatThrownBy(() -> envioService.planificarRuta(99L, List.of(10L)))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("transportista");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando servicio de transportistas no disponible")
        void transportistaServicioNoDisponible() {
            when(restTemplate.getForObject(anyString(), eq(TransportistaDTO.class)))
                    .thenThrow(new ResourceAccessException("Connection refused"));

            assertThatThrownBy(() -> envioService.planificarRuta(1L, List.of(10L)))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("servicio no esta disponible");
        }
    }

    @Nested
    @DisplayName("listarEnvios")
    class ListarEnvios {

        @Test
        @DisplayName("filtra por clienteId cuando se proporciona")
        void filtraPorCliente() {
            when(envioDomainService.readByClienteId(5L)).thenReturn(List.of(envioPendiente));

            List<Envio> resultado = envioService.listarEnvios(5L, null);

            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("filtra por estadoId cuando se proporciona")
        void filtraPorEstado() {
            when(envioDomainService.readByEstadoId(1L)).thenReturn(List.of(envioPendiente));

            List<Envio> resultado = envioService.listarEnvios(null, 1L);

            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("retorna todos cuando no hay filtros")
        void retornaTodos() {
            when(envioDomainService.readAll()).thenReturn(List.of(envioPendiente));

            List<Envio> resultado = envioService.listarEnvios(null, null);

            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("clienteId tiene prioridad sobre estadoId")
        void clienteTienePrioridad() {
            when(envioDomainService.readByClienteId(5L)).thenReturn(List.of(envioPendiente));

            List<Envio> resultado = envioService.listarEnvios(5L, 1L);

            assertThat(resultado).hasSize(1);
            verify(envioDomainService, never()).readByEstadoId(any());
        }
    }

    @Nested
    @DisplayName("obtenerEnvioPorId")
    class ObtenerEnvio {

        @Test
        @DisplayName("retorna envio cuando existe")
        void retornaEnvio() {
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);

            Envio resultado = envioService.obtenerEnvioPorId(10L);

            assertThat(resultado.getId()).isEqualTo(10L);
        }
    }

    @Nested
    @DisplayName("obtenerHistorialEnvio")
    class ObtenerHistorial {

        @Test
        @DisplayName("retorna historial del envio")
        void retornaHistorial() {
            HistorialEnvio h = new HistorialEnvio();
            h.setId(1L);
            h.setEnvioId(10L);
            h.setEstado(estadoPendiente);
            when(envioDomainService.findById(10L)).thenReturn(envioPendiente);
            when(historialEnvioService.findHistorialByEnvioId(10L)).thenReturn(List.of(h));

            List<HistorialEnvio> resultado = envioService.obtenerHistorialEnvio(10L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getEnvioId()).isEqualTo(10L);
        }
    }
}
