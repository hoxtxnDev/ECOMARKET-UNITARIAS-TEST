package com.ecomarket.analiticaservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.analiticaservice.dto.*;
import com.ecomarket.analiticaservice.exception.NoExisteEnBdException;
import com.ecomarket.analiticaservice.model.entity.*;
import com.ecomarket.analiticaservice.model.reference.*;
import com.ecomarket.analiticaservice.repository.*;

@ExtendWith(MockitoExtension.class)
class AnaliticaServiceTest {

    @Mock private ReporteDomainService reporteDomainService;
    @Mock private AlertaDomainService alertaDomainService;
    @Mock private RespaldoDomainService respaldoDomainService;
    @Mock private MetricaDomainService metricaDomainService;
    @Mock private TipoReporteRepository tipoReporteRepository;
    @Mock private EstadoReporteRepository estadoReporteRepository;
    @Mock private NivelAlertaRepository nivelAlertaRepository;
    @Mock private EstadoRespaldoRepository estadoRespaldoRepository;
    @Mock private RestTemplate restTemplate;

    @InjectMocks private AnaliticaService service;

    private final TipoReporte tipo = new TipoReporte(1L, "Usuarios");
    private final EstadoReporte estado = new EstadoReporte(1L, "Pendiente");
    private final Reporte reporteBase = new Reporte(1L, 10L, tipo, estado, LocalDateTime.now(), null, null);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "usuariosUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(service, "pedidosUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(service, "productosUrl", "http://localhost:8087");
        ReflectionTestUtils.setField(service, "inventarioUrl", "http://localhost:8087");
        ReflectionTestUtils.setField(service, "pagosUrl", "http://localhost:8088");
        ReflectionTestUtils.setField(service, "carritoUrl", "http://localhost:8082");
        ReflectionTestUtils.setField(service, "soporteUrl", "http://localhost:8089");
        ReflectionTestUtils.setField(service, "enviosUrl", "http://localhost:8083");
    }

    // ====== REPORTES ======

    @Test
    void obtenerReporteDelegatesToDomain() {
        when(reporteDomainService.buscarPorId(1L)).thenReturn(reporteBase);

        assertEquals(1L, service.obtenerReporte(1L).getId());
    }

    @Test
    void listarReportesDelegatesToDomain() {
        when(reporteDomainService.listarTodos()).thenReturn(List.of(reporteBase));

        assertEquals(1, service.listarReportes().size());
    }

    @Test
    void listarReportesPorSolicitanteDelegates() {
        when(reporteDomainService.buscarPorSolicitante(10L)).thenReturn(List.of(reporteBase));

        assertEquals(1, service.listarReportesPorSolicitante(10L).size());
    }

    @Test
    void listarReportesPorRangoFechasDelegates() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = LocalDateTime.now();
        when(reporteDomainService.buscarPorRangoFechas(inicio, fin)).thenReturn(List.of(reporteBase));

        assertEquals(1, service.listarReportesPorRangoFechas(inicio, fin).size());
    }

    @Test
    void generarReporteCreatesBaseReport() {
        when(tipoReporteRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporte(new ReporteRequestDTO(10L, 1L));

        assertEquals(1L, result.getId());
    }

    @Test
    void generarReportePorRangoCreatesBaseReport() {
        when(tipoReporteRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReportePorRango(
                new ReporteFechaRequestDTO(10L, 1L, LocalDateTime.now(), LocalDateTime.now()));

        assertEquals(1L, result.getId());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteUsuariosFetchesDataAndCreatesReport() {
        List<ClienteDTO> clientes = List.of(new ClienteDTO(1L, "Juan", "juan@test.com", "123"));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(clientes));
        when(tipoReporteRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteUsuarios(10L);

        assertEquals(1, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteUsuariosHandlesServiceUnavailable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        when(tipoReporteRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteUsuarios(10L);

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReportePedidosFetchesAndCreates() {
        List<PedidoDTO> pedidos = List.of(new PedidoDTO());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(pedidos));
        when(tipoReporteRepository.findById(2L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReportePedidos(10L, LocalDateTime.now().minusDays(7), LocalDateTime.now());

        assertEquals(1, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteInventarioFetchesDataAndCreates() {
        List<ProductoDTO> productos = List.of(new ProductoDTO(), new ProductoDTO());
        List<InventarioStockDTO> stocks = List.of(new InventarioStockDTO());
        when(restTemplate.exchange(contains("/api/catalogo"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(productos));
        when(restTemplate.exchange(contains("/api/inventario"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(stocks));
        when(tipoReporteRepository.findById(3L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteInventario(10L);

        assertEquals(3, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReportePagosFetchesAndCreates() {
        List<PagoDTO> pagos = List.of(new PagoDTO());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(pagos));
        when(tipoReporteRepository.findById(4L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReportePagos(10L);

        assertEquals(1, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteCarritoFetchesAndCreates() {
        List<CarritoDTO> items = List.of(new CarritoDTO());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(items));
        when(tipoReporteRepository.findById(5L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteCarrito(10L);

        assertEquals(1, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteSoporteFetchesAndCreates() {
        List<TicketSoporteDTO> tickets = List.of(new TicketSoporteDTO());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(tickets));
        when(tipoReporteRepository.findById(6L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteSoporte(10L);

        assertEquals(1, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteEnviosFetchesAndCreates() {
        List<EnvioDTO> envios = List.of(new EnvioDTO());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(envios));
        when(tipoReporteRepository.findById(7L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteEnvios(10L);

        assertEquals(1, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteCompletoFetchesAllAndCreates() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(new ClienteDTO(), new PedidoDTO())));
        when(tipoReporteRepository.findById(8L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteCompleto(10L);

        assertEquals(16, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteCompletoHandlesServiceUnavailable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        when(tipoReporteRepository.findById(8L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteCompleto(10L);

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchListHandlesNullBody() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(null));
        when(tipoReporteRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteUsuarios(10L);

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchListHandlesGenericException() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("generic error"));
        when(tipoReporteRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteUsuarios(10L);

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReportePedidosHandlesServiceUnavailable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        when(tipoReporteRepository.findById(2L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReportePedidos(10L, LocalDateTime.now().minusDays(7), LocalDateTime.now());

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteInventarioHandlesServiceUnavailable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        when(tipoReporteRepository.findById(3L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteInventario(10L);

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReportePagosHandlesServiceUnavailable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        when(tipoReporteRepository.findById(4L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReportePagos(10L);

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteCarritoHandlesServiceUnavailable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        when(tipoReporteRepository.findById(5L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteCarrito(10L);

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteSoporteHandlesServiceUnavailable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        when(tipoReporteRepository.findById(6L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteSoporte(10L);

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    @SuppressWarnings("unchecked")
    @Test
    void generarReporteEnviosHandlesServiceUnavailable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ResourceAccessException("timeout"));
        when(tipoReporteRepository.findById(7L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(2L)).thenReturn(Optional.of(estado));
        when(reporteDomainService.crearReporte(10L, tipo, estado)).thenReturn(reporteBase);

        Reporte result = service.generarReporteEnvios(10L);

        assertEquals(0, result.getTotalRegistrosProcesados());
    }

    // ====== ALERTAS ======

    @Test
    void obtenerAlertaDelegates() {
        AlertaSistema alerta = new AlertaSistema();
        when(alertaDomainService.buscarPorId(1L)).thenReturn(alerta);

        assertSame(alerta, service.obtenerAlerta(1L));
    }

    @Test
    void listarAlertasDelegates() {
        when(alertaDomainService.listarTodas()).thenReturn(List.of(new AlertaSistema()));

        assertEquals(1, service.listarAlertas().size());
    }

    @Test
    void listarAlertasPorEstadoDelegates() {
        when(alertaDomainService.buscarPorResuelta(true)).thenReturn(List.of(new AlertaSistema()));

        assertEquals(1, service.listarAlertasPorEstado(true).size());
    }

    @Test
    void crearAlertaCreatesWithValidNivel() {
        NivelAlerta nivel = new NivelAlerta(1L, "CRITICO");
        AlertaSistema alerta = new AlertaSistema();
        when(nivelAlertaRepository.findById(1L)).thenReturn(Optional.of(nivel));
        when(alertaDomainService.crear(nivel, "Mensaje", "Modulo")).thenReturn(alerta);

        assertSame(alerta, service.crearAlerta(new AlertaRequestDTO(1L, "Mensaje", "Modulo")));
    }

    @Test
    void crearAlertaThrowsWhenNivelNotFound() {
        when(nivelAlertaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class,
                () -> service.crearAlerta(new AlertaRequestDTO(99L, "Mensaje", "Modulo")));
    }

    @Test
    void resolverAlertaDelegates() {
        AlertaSistema alerta = new AlertaSistema();
        when(alertaDomainService.marcarResuelta(1L)).thenReturn(alerta);

        assertSame(alerta, service.resolverAlerta(1L));
    }

    // ====== METRICAS ======

    @Test
    void obtenerMetricaPorIdDelegates() {
        MetricaDashboard m = new MetricaDashboard();
        when(metricaDomainService.buscarPorId(1L)).thenReturn(m);

        assertSame(m, service.obtenerMetricaPorId(1L));
    }

    @Test
    void obtenerMetricaPorClaveDelegates() {
        MetricaDashboard m = new MetricaDashboard();
        when(metricaDomainService.buscarPorClave("ventas")).thenReturn(m);

        assertSame(m, service.obtenerMetricaPorClave("ventas"));
    }

    @Test
    void listarMetricasDelegates() {
        when(metricaDomainService.listarTodas()).thenReturn(List.of(new MetricaDashboard()));

        assertEquals(1, service.listarMetricas().size());
    }

    @Test
    void crearMetricaDelegates() {
        MetricaDashboard m = new MetricaDashboard();
        when(metricaDomainService.crear("ventas", 100.0, "texto")).thenReturn(m);

        assertSame(m, service.crearMetrica(new MetricaRequestDTO("ventas", 100.0, "texto")));
    }

    @Test
    void actualizarMetricaDelegates() {
        MetricaDashboard m = new MetricaDashboard();
        when(metricaDomainService.actualizar(1L, 200.0, "new")).thenReturn(m);

        assertSame(m, service.actualizarMetrica(1L, new MetricaRequestDTO("ventas", 200.0, "new")));
    }

    @Test
    void registrarAccionLogsAndSucceeds() {
        service.registrarAccion(new AccionLogDTO("test", "CREAR", 1L, "detalle", LocalDateTime.now()));
    }

    // ====== RESPALDOS ======

    @Test
    void obtenerRespaldoDelegates() {
        RespaldoBaseDatos r = new RespaldoBaseDatos();
        when(respaldoDomainService.buscarPorId(1L)).thenReturn(r);

        assertSame(r, service.obtenerRespaldo(1L));
    }

    @Test
    void listarRespaldosDelegates() {
        when(respaldoDomainService.listarTodos()).thenReturn(List.of(new RespaldoBaseDatos()));

        assertEquals(1, service.listarRespaldos().size());
    }

    @Test
    void ejecutarRespaldoCreatesWithValidEstado() {
        EstadoRespaldo estado = new EstadoRespaldo(1L, "Completado");
        RespaldoBaseDatos r = new RespaldoBaseDatos();
        when(estadoRespaldoRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(respaldoDomainService.crearRespaldo(estado, 10.0, "/ruta")).thenReturn(r);

        assertSame(r, service.ejecutarRespaldo(new RespaldoRequestDTO(1L, 10.0, "/ruta")));
    }

    @Test
    void ejecutarRespaldoThrowsWhenEstadoNotFound() {
        when(estadoRespaldoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class,
                () -> service.ejecutarRespaldo(new RespaldoRequestDTO(99L, 10.0, "/ruta")));
    }

    // ====== crearReporteBase exceptions ======

    @Test
    void generarReporteThrowsWhenTipoNotFound() {
        when(tipoReporteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class,
                () -> service.generarReporte(new ReporteRequestDTO(10L, 99L)));
    }

    @Test
    void generarReporteThrowsWhenEstadoNotFound() {
        when(tipoReporteRepository.findById(1L)).thenReturn(Optional.of(tipo));
        when(estadoReporteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class,
                () -> service.generarReporte(new ReporteRequestDTO(10L, 1L)));
    }
}
