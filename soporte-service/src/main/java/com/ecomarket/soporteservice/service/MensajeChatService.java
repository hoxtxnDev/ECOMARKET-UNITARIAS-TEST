package com.ecomarket.soporteservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecomarket.soporteservice.exception.NoExisteEnBdException;
import com.ecomarket.soporteservice.model.entity.MensajeChat;
import com.ecomarket.soporteservice.model.entity.TicketSoporte;
import com.ecomarket.soporteservice.repository.MensajeChatRepository;
import com.ecomarket.soporteservice.repository.TicketSoporteRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
@Transactional
public class MensajeChatService {


    private final MensajeChatRepository mensajeChatRepository;
    private final TicketSoporteRepository ticketSoporteRepository;

    public List<MensajeChat> readAllMensajes() {
        return mensajeChatRepository.findAll();
    }

    public List<MensajeChat> obtenerHistorialChat(Long ticketId) {
        return mensajeChatRepository.findByTicketIdOrderByFechaEnvioAsc(ticketId);
    }

    public List<MensajeChat> obtenerHistorialChat(Long ticketId, Boolean viewerEsCliente) {
        List<MensajeChat> mensajes = obtenerHistorialChat(ticketId);
        List<MensajeChat> aMarcar = mensajes.stream()
            .filter(m -> !m.getEsCliente().equals(viewerEsCliente) && !m.getLeido())
            .collect(Collectors.toList());
        if (!aMarcar.isEmpty()) {
            aMarcar.forEach(m -> m.setLeido(true));
            mensajeChatRepository.saveAll(aMarcar);
        }
        return mensajes;
    }

    public MensajeChat enviarMensajeChat(Long ticketId, Long remitenteId, Boolean esCliente, String contenido) {
        return enviarMensajeChat(ticketId, remitenteId, esCliente, contenido, false);
    }

    public MensajeChat enviarMensajeChat(Long ticketId, Long remitenteId, Boolean esCliente, String contenido, Boolean esAdmin) {
        TicketSoporte ticket = ticketSoporteRepository.findById(ticketId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "El ticket con id " + ticketId + " no existe."));
        if (ticket.getEstado().getId() == 5L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "No se pueden enviar mensajes a un ticket cerrado.");
        }
        if (esCliente && !ticket.getClienteId().equals(remitenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "No puedes enviar mensajes a un ticket que no te pertenece.");
        }
        if (!esCliente && !esAdmin && !ticket.getEmpleadoAsignadoId().equals(remitenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Solo el empleado asignado al ticket puede responder.");
        }
        if (!esCliente) {
            List<MensajeChat> noLeidos = mensajeChatRepository
                .findByTicketIdAndEsClienteAndLeido(ticketId, true, false);
            noLeidos.forEach(m -> m.setLeido(true));
            mensajeChatRepository.saveAll(noLeidos);
        }
        MensajeChat mensaje = new MensajeChat();
        mensaje.setTicketId(ticketId);
        mensaje.setRemitenteId(remitenteId);
        mensaje.setEsCliente(esCliente);
        mensaje.setContenido(contenido.trim());
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setLeido(false);
        return mensajeChatRepository.save(mensaje);
    }

    public void marcarComoLeido(Long mensajeId) {
        MensajeChat mensaje = mensajeChatRepository.findById(mensajeId)
            .orElseThrow(() -> new NoExisteEnBdException("El mensaje con id " + mensajeId + " no existe en la DB."));
        mensaje.setLeido(true);
        mensajeChatRepository.save(mensaje);
    }

    public void marcarTodosComoLeidos(Long ticketId) {
        List<MensajeChat> mensajes = obtenerHistorialChat(ticketId);
        mensajes.forEach(m -> m.setLeido(true));
        mensajeChatRepository.saveAll(mensajes);
    }

    public MensajeChat findMensajeById(Long id) {
        return mensajeChatRepository.findById(id)
            .orElseThrow(() -> new NoExisteEnBdException("El mensaje con id " + id + " no existe en la DB."));
    }

    public void deleteMensajeById(Long id) {
        MensajeChat existente = mensajeChatRepository.findById(id).orElse(null);
        if (existente == null) {
            throw new NoExisteEnBdException("El mensaje con id " + id + " no se puede borrar debido a que no existe en la BD.");
        }
        mensajeChatRepository.deleteById(id);
    }
}
