package com.ecomarket.soporteservice.service;

import java.util.List;
 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import com.ecomarket.soporteservice.model.entity.MensajeChat;
import com.ecomarket.soporteservice.model.entity.Notificacion;
import com.ecomarket.soporteservice.model.entity.Resena;
import com.ecomarket.soporteservice.model.entity.TicketSoporte;


import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class SoporteService {
 
    private final NotificacionService notificacionService;
 
    private final TicketSoporteService ticketSoporteService;
 
    private final MensajeChatService mensajeChatService;
 
    private final ResenaService resenaService;
 
    public Notificacion enviarNotificacionPush(Long destinatarioId, String titulo, String mensaje, Long canalId) {
        return notificacionService.sendNotificacion(destinatarioId, titulo, mensaje, canalId);
    }

    public TicketSoporte ingresarTicket(Long clienteId, Long categoriaId, String asunto, Long pedidoId) throws Exception {
        return ticketSoporteService.ingresarTicket(clienteId, categoriaId, asunto, pedidoId);
    }

    public TicketSoporte asignarTicketEmpleado(Long ticketId, Long empleadoId) {
        return ticketSoporteService.asignarTicketEmpleado(ticketId, empleadoId);
    }

    public MensajeChat enviarMensajeChat(Long ticketId, Long remitenteId, Boolean esCliente, String contenido, Boolean esAdmin) {
        return mensajeChatService.enviarMensajeChat(ticketId, remitenteId, esCliente, contenido, esAdmin);
    }

    public List<MensajeChat> obtenerHistorialChat(Long ticketId, Boolean viewerEsCliente) {
        return mensajeChatService.obtenerHistorialChat(ticketId, viewerEsCliente);
    }

    public TicketSoporte solucionarTicket(Long ticketId, Long remitenteId, String solucionResumen, Boolean esAdmin) {
        TicketSoporte ticket = ticketSoporteService.findTicketById(ticketId);
        if (!esAdmin && !Long.valueOf(remitenteId).equals(ticket.getEmpleadoAsignadoId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Solo el empleado asignado al ticket puede marcarlo como solucionado.");
        }
        ticket = ticketSoporteService.solucionarTicket(ticketId, solucionResumen);
        mensajeChatService.enviarMensajeChat(ticketId, remitenteId, false, solucionResumen, true);
        return ticket;
    }

    public Resena dejarResena(Long productoId, Long clienteId, Integer calificacion, String comentario) {
        return resenaService.dejarResena(productoId, clienteId, calificacion, comentario);
    }

    public TicketSoporte cerrarTicket(Long ticketId, Long remitenteId, Boolean esCliente, Boolean esAdmin) {
        TicketSoporte ticket = ticketSoporteService.findTicketById(ticketId);
        if (esCliente && !ticket.getClienteId().equals(remitenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "No puedes cerrar un ticket que no te pertenece.");
        }
        if (!esCliente && !esAdmin && !Long.valueOf(remitenteId).equals(ticket.getEmpleadoAsignadoId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Solo el empleado asignado al ticket puede cerrarlo.");
        }
        return ticketSoporteService.cerrarTicket(ticketId);
    }
}
