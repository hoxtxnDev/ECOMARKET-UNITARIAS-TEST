package com.ecomarket.soporteservice.controller;

import java.util.List;
 
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomarket.soporteservice.dto.MensajeChatRequestDTO;
import com.ecomarket.soporteservice.model.entity.MensajeChat;
import com.ecomarket.soporteservice.service.MensajeChatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/mensajes-chat")
@RequiredArgsConstructor
public class MensajeChatController {
 
    private final MensajeChatService mensajeChatService;
 
    @GetMapping
    public List<MensajeChat> readAll() {
        return mensajeChatService.readAllMensajes();
    }

    @GetMapping("{id}")
    public MensajeChat obtenerMensajePorId(@PathVariable Long id) {
        return mensajeChatService.findMensajeById(id);
    }

    @PostMapping
    public ResponseEntity<MensajeChat> enviarMensajeChat(
            @RequestHeader("X-User-Id") Long remitenteId,
            @RequestHeader("X-User-Roles") String roles,
            @Valid @RequestBody MensajeChatRequestDTO dto) {
        boolean esCliente = roles.contains("ROLE_CLIENTE");
        boolean esAdmin = roles.contains("ROLE_ADMIN");
        MensajeChat mensaje = mensajeChatService.enviarMensajeChat(
            dto.getTicketId(), remitenteId, esCliente, dto.getContenido(), esAdmin);
        return ResponseEntity.status(201).body(mensaje);
    }

    @PatchMapping("{id}/leido")
    public ResponseEntity<String> marcarComoLeido(@PathVariable Long id) {
        mensajeChatService.marcarComoLeido(id);
        return ResponseEntity.ok("El mensaje con id " + id + " ha sido marcado como leido.");
    }

    @PatchMapping("marcar-leidos/{ticketId}")
    public ResponseEntity<String> marcarTodosComoLeidos(@PathVariable Long ticketId) {
        mensajeChatService.marcarTodosComoLeidos(ticketId);
        return ResponseEntity.ok("Todos los mensajes del ticket " + ticketId + " han sido marcados como leidos.");
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> eliminarMensaje(@PathVariable Long id) {
        mensajeChatService.deleteMensajeById(id);
        return ResponseEntity.ok("El mensaje con id " + id + " ha sido eliminado con exito.");
    }
}
