package com.ecomarket.procesopagoservice.service;

import com.ecomarket.procesopagoservice.model.*;
import com.ecomarket.procesopagoservice.repository.*;
import com.ecomarket.procesopagoservice.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoService {

    private final TransaccionRepository transaccionRepository;
    private final FacturaRepository facturaRepository;
    private final CuponRepository cuponRepository;
    private final EstadoPagoRepository estadoPagoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final RestTemplate restTemplate;

    private static final Set<String> METODOS_MANUALES = Set.of(
            "Transferencia Bancaria", "Pago en Efectivo", "Contra Entrega");

    @SuppressWarnings("unchecked")
    @Transactional
    public TransaccionPago iniciarPago(Long pedidoId, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<TransaccionPago> existingByIdemKey = transaccionRepository.findByIdempotencyKey(idempotencyKey);
            if (existingByIdemKey.isPresent()) return existingByIdemKey.get();
        }

        Map<String, Object> pedidoData;
        MetodoPagoTransaccion metodo;
        try {
            pedidoData = restTemplate.getForObject("http://localhost:8089/api/pedidos/" + pedidoId, Map.class);
            if (pedidoData == null) throw new RecursoNoEncontradoException("Pedido no encontrado con ID: " + pedidoId);

            Object metodoPagoObj = pedidoData.get("metodoPagoId");
            if (metodoPagoObj == null) throw new RecursoNoEncontradoException("El pedido no tiene un método de pago asignado.");
            Long metodoPagoId = Long.valueOf(metodoPagoObj.toString());

            metodo = metodoPagoRepository.findById(metodoPagoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado con ID: " + metodoPagoId));

            Object estadoObj = pedidoData.get("estado");
            String estadoPedido = "DESCONOCIDO";
            if (estadoObj instanceof Map) {
                estadoPedido = (String) ((Map<?, ?>) estadoObj).get("nombre");
            } else if (estadoObj instanceof String) {
                estadoPedido = (String) estadoObj;
            }

            if ("CONFIRMADO".equals(estadoPedido) || "ENVIADO".equals(estadoPedido)) {
                throw new EstadoTransaccionInvalidoException("El pedido ya ha sido procesado o enviado.");
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("El pedido " + pedidoId + " no existe en el servicio de pedidos.");
        } catch (Exception e) {
            if (e instanceof RecursoNoEncontradoException || e instanceof EstadoTransaccionInvalidoException) throw (RuntimeException) e;
            log.error("Error inesperado al validar pedido {}: {}", pedidoId, e.getMessage());
            throw new RecursoNoEncontradoException("Error interno al validar el pedido " + pedidoId);
        }

        Long clienteId = Long.valueOf(pedidoData.get("clienteId").toString());
        Double montoTotal = Double.valueOf(pedidoData.get("total").toString());

        List<EstadoPago> terminalStates = new ArrayList<>();
        estadoPagoRepository.findByNombre("APROBADO").ifPresent(terminalStates::add);
        estadoPagoRepository.findByNombre("RECHAZADO").ifPresent(terminalStates::add);
        estadoPagoRepository.findByNombre("REEMBOLSADO").ifPresent(terminalStates::add);

        Optional<TransaccionPago> activeTransaction = transaccionRepository.findFirstByPedidoIdAndEstadoNotIn(pedidoId, terminalStates);
        if (activeTransaction.isPresent()) return activeTransaction.get();

        EstadoPago estadoInicial = estadoPagoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró un estado inicial"));

        String finalIdempotencyKey = (idempotencyKey == null || idempotencyKey.isBlank()) 
                ? UUID.randomUUID().toString() : idempotencyKey;

        TransaccionPago transaccion = new TransaccionPago();
        transaccion.setPedidoId(pedidoId);
        transaccion.setClienteId(clienteId);
        transaccion.setMontoSubtotal(montoTotal);
        transaccion.setMontoDescuento(0.0);
        transaccion.setMontoTotal(montoTotal);
        transaccion.setMetodoPago(metodo);
        transaccion.setEstado(estadoInicial);
        transaccion.setIdempotencyKey(finalIdempotencyKey);
        transaccion.setFechaInicio(LocalDateTime.now());
        transaccion.setFechaUltimaActualizacion(LocalDateTime.now());
        transaccion.setTokenTransbank("TB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return transaccionRepository.save(transaccion);
    }

    @Transactional
    public TransaccionPago procesarConTransbank(Long transaccionId, String token) {
        TransaccionPago transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada: " + transaccionId));

        EstadoPago estadoProceso = estadoPagoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró estado de proceso"));
        
        transaccion.setEstado(estadoProceso);
        transaccion.setFechaUltimaActualizacion(LocalDateTime.now());
        transaccionRepository.save(transaccion);

        try {
            if (token == null || token.isEmpty() || token.equals("error")) {
                throw new ProcesamientoPagoException("Token de pago inválido o rechazado por el banco");
            }

            EstadoPago estadoAprobado = estadoPagoRepository.findByNombre("APROBADO")
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estado APROBADO no encontrado"));
            
            transaccion.setEstado(estadoAprobado);
            transaccion.setCodigoAutorizacion("AUTH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            transaccion.setFechaFin(LocalDateTime.now());
            transaccion.setFechaUltimaActualizacion(LocalDateTime.now());

            boolean esManual = METODOS_MANUALES.contains(transaccion.getMetodoPago().getNombre());
            if (!esManual) {
                actualizarEstadoPedido(transaccion.getPedidoId(), "CONFIRMADO");
                actualizarEstadoPedido(transaccion.getPedidoId(), "EN PREPARACION");
            }
            ejecutarFlujoPostPago(transaccion);

        } catch (ProcesamientoPagoException e) {
            EstadoPago estadoRechazado = estadoPagoRepository.findByNombre("RECHAZADO")
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estado RECHAZADO no encontrado"));
            
            transaccion.setEstado(estadoRechazado);
            transaccion.setMensajeError(e.getMessage());
            transaccion.setFechaFin(LocalDateTime.now());
            transaccion.setFechaUltimaActualizacion(LocalDateTime.now());
            actualizarEstadoPedido(transaccion.getPedidoId(), "CANCELADO");
            log.error("Rechazo en procesamiento de pago {}: {}", transaccionId, e.getMessage());
        } catch (Exception e) {
            EstadoPago estadoRechazado = estadoPagoRepository.findByNombre("RECHAZADO")
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estado RECHAZADO no encontrado"));
            
            transaccion.setEstado(estadoRechazado);
            transaccion.setMensajeError("Error interno: " + e.getMessage());
            transaccion.setFechaFin(LocalDateTime.now());
            transaccion.setFechaUltimaActualizacion(LocalDateTime.now());
            log.error("Fallo técnico en procesamiento de pago {}: {}", transaccionId, e.getMessage());
        }

        return transaccionRepository.save(transaccion);
    }

    private void actualizarEstadoPedido(Long pedidoId, String nuevoEstado) {
        try {
            log.info("Actualizando pedido {} a estado {}", pedidoId, nuevoEstado);
            restTemplate.put("http://localhost:8089/api/pedidos/" + pedidoId + "/estado-nombre", nuevoEstado);
        } catch (Exception e) {
            log.error("Error al actualizar estado del pedido {}: {}", pedidoId, e.getMessage());
        }
    }

    private void ejecutarFlujoPostPago(TransaccionPago transaccion) {
        // Eliminamos la llamada a /api/pedidos/generar porque el pedido ya fue generado antes de iniciar el pago.
        String emptyCartUrl = "http://localhost:8082/api/carrito/" + transaccion.getClienteId() + "/vaciar";
        try {
            restTemplate.delete(emptyCartUrl);
        } catch (Exception e) {
            log.error("Error al vaciar carrito: {}", e.getMessage());
        }

        registrarLog(transaccion.getClienteId(), "PAGO_APROBADO", "Pago aprobado: " + transaccion.getMontoTotal());
    }

    @Transactional
    public Boolean procesarReembolso(Long transaccionId, String motivo) {
        TransaccionPago transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada: " + transaccionId));

        if (!"APROBADO".equals(transaccion.getEstado().getNombre())) {
            throw new EstadoTransaccionInvalidoException("Solo se pueden reembolsar transacciones APROBADAS");
        }

        EstadoPago estadoReembolsado = estadoPagoRepository.findByNombre("REEMBOLSADO")
                .orElseThrow(() -> new RecursoNoEncontradoException("Estado REEMBOLSADO no encontrado"));

        transaccion.setEstado(estadoReembolsado);
        transaccion.setFechaFin(LocalDateTime.now());
        transaccion.setFechaUltimaActualizacion(LocalDateTime.now());
        actualizarEstadoPedido(transaccion.getPedidoId(), "CANCELADO");
        transaccionRepository.save(transaccion);
        log.info("Reembolso procesando para {}: {}", transaccionId, motivo);
        return true;
    }

    public FacturaElectronica generarFactura(Long transaccionId, Long rut, String giro) {
        TransaccionPago transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada: " + transaccionId));

        FacturaElectronica factura = new FacturaElectronica();
        factura.setTransaccionId(transaccionId);
        factura.setClienteId(transaccion.getClienteId());
        factura.setRutReceptor(String.valueOf(rut));
        factura.setRazonSocial(giro);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setFolioFiscal(System.currentTimeMillis());
        factura.setXmlDocumento("<factura><transaccion>" + transaccionId + "</transaccion></factura>");

        return facturaRepository.save(factura);
    }

    public Boolean enviarBoletaEmail(Long transaccionId, String correoDestino) {
        transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada: " + transaccionId));
        log.info("Enviando boleta de transacción {} a {}", transaccionId, correoDestino);
        return true;
    }

    public TransaccionPago obtenerTransaccion(Long transaccionId) {
        return transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada: " + transaccionId));
    }

    @Transactional
    public TransaccionPago anadirCuponDescuento(Long transaccionId, Long cuponId) {
        TransaccionPago transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transacción no encontrada: " + transaccionId));
        
        String estadoNombre = transaccion.getEstado().getNombre();
        if ("APROBADO".equals(estadoNombre) || "RECHAZADO".equals(estadoNombre) || "REEMBOLSADO".equals(estadoNombre)) {
            throw new EstadoTransaccionInvalidoException("No se puede aplicar cupón a una transacción en estado " + estadoNombre);
        }

        CuponDescuento cupon = cuponRepository.findById(cuponId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cupón no encontrado: " + cuponId));

        if (!cupon.esValido()) {
            throw new CuponInvalidoException("El cupón no es válido o ha expirado");
        }

        Double descuento = transaccion.getMontoSubtotal() * (cupon.getPorcentajeDescuento() / 100.0);
        if (cupon.getMontoMaximoDescuento() != null && descuento > cupon.getMontoMaximoDescuento()) {
            descuento = cupon.getMontoMaximoDescuento();
        }

        transaccion.setMontoDescuento(descuento);
        transaccion.setMontoTotal(transaccion.getMontoSubtotal() - descuento);
        transaccion.setCuponUtilizadoId(cuponId);
        transaccion.setFechaUltimaActualizacion(LocalDateTime.now());

        return transaccionRepository.save(transaccion);
    }

    private void registrarLog(Long usuarioId, String accion, String detalles) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("microservicio", "servicepago");
        logData.put("accion", accion);
        logData.put("usuarioId", usuarioId);
        logData.put("detalles", detalles);
        logData.put("fecha", LocalDateTime.now());

        try {
            restTemplate.postForEntity("http://localhost:8084/api/analitica/logs", logData, String.class);
        } catch (Exception e) {
            log.error("Error registrando log: {}", e.getMessage());
        }
    }
}
