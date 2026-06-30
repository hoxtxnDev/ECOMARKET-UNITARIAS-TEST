package com.ecomarket.soporteservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.soporteservice.client.AnaliticaMetricaClient;
import com.ecomarket.soporteservice.dto.ClienteDTO;
import com.ecomarket.soporteservice.dto.PedidoDTO;
import com.ecomarket.soporteservice.exception.EmpleadoNoValidoException;
import com.ecomarket.soporteservice.exception.NoExisteEnBdException;
import com.ecomarket.soporteservice.exception.PedidoClienteIncompatibleException;
import com.ecomarket.soporteservice.model.entity.TicketSoporte;
import com.ecomarket.soporteservice.model.reference.CategoriaTicket;
import com.ecomarket.soporteservice.model.reference.EstadoTicket;
import com.ecomarket.soporteservice.repository.TicketSoporteRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketSoporteService {
    
    private final TicketSoporteRepository ticketSoporteRepository;

    private final EstadoTicketService estadoTicketService;
 
    private final CategoriaTicketService categoriaTicketService;

    private final RestTemplate restTemplate;

    private final AnaliticaMetricaClient analiticaMetricaClient;

    @org.springframework.beans.factory.annotation.Value("${microservicio.usuarios.url}")
    private String usuariosUrl;

    @org.springframework.beans.factory.annotation.Value("${microservicio.pedidos.url}")
    private String pedidosUrl;

    private static final Logger log = LoggerFactory.getLogger(TicketSoporteService.class);

    public List<TicketSoporte> readAllTickets() {
        return ticketSoporteRepository.findAll();
    }

    public List<TicketSoporte> readTicketsByClienteId(Long clienteId) {
        return ticketSoporteRepository.findByClienteId(clienteId);
    }

    public List<TicketSoporte> readTicketsByEstadoId(Long estadoId) {
        return ticketSoporteRepository.findByEstadoId(estadoId);
    }

    public TicketSoporte findTicketById(Long id) {
        return ticketSoporteRepository.findById(id)
            .orElseThrow(() -> new NoExisteEnBdException("El ticket con id " + id + " no existe en la DB."));
    }

    public TicketSoporte ingresarTicket(Long clienteId, Long categoriaId, String asunto, Long pedidoId) throws Exception {

        EstadoTicket estadoValido = estadoTicketService.findEstadoTicketById(1L);
        CategoriaTicket categoria = categoriaTicketService.findCategoriaTicketById(categoriaId);

        // verificar existencia de cliente en usuario-service
        String urlCliente = usuariosUrl + "/api/usuarios/" + clienteId;

        try {
        @SuppressWarnings("unused")
        ClienteDTO cliente = restTemplate.getForObject(urlCliente, ClienteDTO.class);
        } catch (HttpClientErrorException.NotFound exNotFound) {
            throw new NoExisteEnBdException("No se puede ingresar el ticket debido a que el id del cliente ingresado no existe en DB.");
        } catch (ResourceAccessException e) {
            log.warn("Servicio de clientes no disponible al validar cliente {}: {}", clienteId, e.getMessage());
            throw new NoExisteEnBdException("No se pudo validar el cliente debido a que el servicio de usuarios no esta disponible.");
        }

        // verificar existencia de pedido solo si se proporciona pedidoId
        if (pedidoId != null) {
            String urlPedido = pedidosUrl + "/api/pedidos/" + pedidoId;

            try {
                PedidoDTO pedido = restTemplate.getForObject(urlPedido, PedidoDTO.class);

                if(pedido != null && !pedido.getClienteId().equals(clienteId)) {
                    throw new PedidoClienteIncompatibleException("No se puede ingresar el ticket debido a que el ID del pedido no es compatible con el cliente asignado a ese pedido.");
                }

            } catch (HttpClientErrorException ex) {

                if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new NoExisteEnBdException("No se puede ingresar el ticket debido a que el id del pedido ingresado no existe en DB.");
                }

                log.error("Error HTTP inesperado al validar pedido {}: {}", pedidoId, ex.getStatusCode());
                throw new Exception();
            } catch (ResourceAccessException e) {
                log.warn("Servicio de pedidos no disponible al validar pedido {}: {}", pedidoId, e.getMessage());
                throw new NoExisteEnBdException("No se pudo validar el pedido debido a que el servicio de pedidos no esta disponible.");
            }
        }

        TicketSoporte ticket = new TicketSoporte();
        ticket.setClienteId(clienteId);
        ticket.setCategoria(categoria);
        ticket.setAsunto(asunto.trim());
        ticket.setFechaCreacion(LocalDateTime.now());
        ticket.setPedidoRelacionadoId(pedidoId);
        ticket.setEstado(estadoValido);

       return ticketSoporteRepository.save(ticket);
    }

    public TicketSoporte actualizarEstadoTicket(Long ticketId, Long nuevoEstadoId) {
        TicketSoporte ticket = ticketSoporteRepository.findById(ticketId)
            .orElseThrow(() -> new NoExisteEnBdException("El ticket con id " + ticketId + " no existe en la DB."));
        EstadoTicket nuevoEstado = estadoTicketService.findEstadoTicketById(nuevoEstadoId);
        ticket.setEstado(nuevoEstado);

        if (nuevoEstadoId == 4L || nuevoEstadoId == 5L) {
            ticket.setFechaCierre(LocalDateTime.now());
        }

        return ticketSoporteRepository.save(ticket);
    }

    public TicketSoporte asignarTicketEmpleado(Long ticketId, Long empleadoId) {
        TicketSoporte ticket = ticketSoporteRepository.findById(ticketId)
            .orElseThrow(() -> new NoExisteEnBdException("El ticket con id " + ticketId + " no existe en la DB."));

        if (ticket.getEstado().getId() == 4L || ticket.getEstado().getId() == 5L) {
            throw new NoExisteEnBdException("No se puede reasignar un ticket que ya esta resuelto o cerrado.");
        }

        // Validar que el empleado existe y tiene rol de soporte
        String urlEmpleado = usuariosUrl + "/api/usuarios/" + empleadoId;
        try {
            ClienteDTO empleado = restTemplate.getForObject(urlEmpleado, ClienteDTO.class);
            if (empleado == null || empleado.getRolUsuario() == null) {
                throw new NoExisteEnBdException("No se puede asignar el ticket: el empleado con id " + empleadoId + " no existe en DB.");
            }
            String rolNombre = empleado.getRolUsuario().getNombre();
            if (!"SOPORTE".equalsIgnoreCase(rolNombre) && !"ADMIN".equalsIgnoreCase(rolNombre)) {
                throw new EmpleadoNoValidoException("No se puede asignar el ticket: el usuario con id " + empleadoId
                    + " tiene rol '" + rolNombre + "'. Solo se puede asignar a usuarios con rol SOPORTE o ADMIN.");
            }
        } catch (HttpClientErrorException.NotFound exNotFound) {
            throw new NoExisteEnBdException("No se puede asignar el ticket: el empleado con id " + empleadoId + " no existe en DB.");
        } catch (ResourceAccessException e) {
            log.warn("Servicio de usuarios no disponible al validar empleado {}: {}", empleadoId, e.getMessage());
            throw new NoExisteEnBdException("No se pudo validar el empleado debido a que el servicio de usuarios no esta disponible.");
        }

        boolean esPrimeraAsignacion = ticket.getEmpleadoAsignadoId() == null;
        ticket.setEmpleadoAsignadoId(empleadoId);
        if (esPrimeraAsignacion) {
            // Primera asignacion → EN_PROCESO
            ticket.setEstado(estadoTicketService.findEstadoTicketById(2L));
        } else {
            // Reasignacion → PENDIENTE
            ticket.setEstado(estadoTicketService.findEstadoTicketById(3L));
        }
        return ticketSoporteRepository.save(ticket);
    }

    public TicketSoporte solucionarTicket(Long ticketId, String solucionResumen) {
        TicketSoporte ticket = ticketSoporteRepository.findById(ticketId)
            .orElseThrow(() -> new NoExisteEnBdException("El ticket con id " + ticketId + " no existe en la DB."));

        if (ticket.getEstado().getId() == 4L) {
            throw new NoExisteEnBdException("El ticket con id " + ticketId + " ya se encuentra resuelto.");
        }
        if (ticket.getEstado().getId() == 5L) {
            throw new NoExisteEnBdException("El ticket con id " + ticketId + " ya se encuentra cerrado.");
        }

        EstadoTicket estadoResuelto = estadoTicketService.findEstadoTicketById(4L);
        ticket.setEstado(estadoResuelto);
        ticket.setSolucionResumen(solucionResumen);
        ticket.setFechaCierre(LocalDateTime.now());
        TicketSoporte saved = ticketSoporteRepository.save(ticket);

        // Registrar metrica en analiticaservice al resolver ticket
        analiticaMetricaClient.registrarMetrica("soporte.tickets.resueltos", 1.0,
                "Ticket #" + ticketId + " resuelto: " + solucionResumen);

        return saved;
    }

    public TicketSoporte cerrarTicket(Long ticketId) {
        TicketSoporte ticket = ticketSoporteRepository.findById(ticketId)
            .orElseThrow(() -> new NoExisteEnBdException("El ticket con id " + ticketId + " no existe en la DB."));

        if (ticket.getEstado().getId() == 5L) {
            throw new NoExisteEnBdException("El ticket con id " + ticketId + " ya se encuentra cerrado.");
        }

        EstadoTicket estadoCerrado = estadoTicketService.findEstadoTicketById(5L);
        ticket.setEstado(estadoCerrado);
        ticket.setFechaCierre(LocalDateTime.now());
        return ticketSoporteRepository.save(ticket);
    }

    public void deleteTicketById(Long id) {
        TicketSoporte existente = ticketSoporteRepository.findById(id).orElse(null);
        if (existente == null) {
            throw new NoExisteEnBdException("El ticket con id " + id + " no se puede borrar debido a que no existe en la BD.");
        }
        ticketSoporteRepository.deleteById(id);
    }
}
