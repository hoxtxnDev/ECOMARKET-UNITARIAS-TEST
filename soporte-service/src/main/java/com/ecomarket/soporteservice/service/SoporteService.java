package com.ecomarket.soporteservice.service;

import java.util.List;
 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 
import com.ecomarket.soporteservice.model.entity.MensajeChat;
import com.ecomarket.soporteservice.model.entity.Notificacion;
import com.ecomarket.soporteservice.model.entity.Resena;
import com.ecomarket.soporteservice.model.entity.TicketSoporte;


import jakarta.transaction.Transactional;

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

    public MensajeChat enviarMensajeChat(Long ticketId, Long remitenteId, Boolean esCliente, String contenido) {
        return mensajeChatService.enviarMensajeChat(ticketId, remitenteId, esCliente, contenido);
    }

    public List<MensajeChat> obtenerHistorialChat(Long ticketId) {
        return mensajeChatService.obtenerHistorialChat(ticketId);
    }

    public TicketSoporte solucionarTicket(Long ticketId, String solucionResumen) {
        return ticketSoporteService.solucionarTicket(ticketId, solucionResumen);
    }

    public Resena dejarResena(Long productoId, Long clienteId, Integer calificacion, String comentario) {
        return resenaService.dejarResena(productoId, clienteId, calificacion, comentario);
    }
}
