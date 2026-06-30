package com.ecomarket.soporteservice.service;

import com.ecomarket.soporteservice.exception.NoExisteEnBdException;
import com.ecomarket.soporteservice.model.entity.MensajeChat;
import com.ecomarket.soporteservice.model.entity.TicketSoporte;
import com.ecomarket.soporteservice.model.reference.EstadoTicket;
import com.ecomarket.soporteservice.repository.MensajeChatRepository;
import com.ecomarket.soporteservice.repository.TicketSoporteRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MensajeChatService")
class MensajeChatServiceTest {

    @Mock
    private MensajeChatRepository repo;

    @Mock
    private TicketSoporteRepository ticketRepo;

    @InjectMocks
    private MensajeChatService service;

    private MensajeChat mensaje(Long id, Long ticketId, String contenido, boolean leido) {
        MensajeChat m = new MensajeChat();
        m.setId(id);
        m.setTicketId(ticketId);
        m.setRemitenteId(1L);
        m.setEsCliente(true);
        m.setContenido(contenido);
        m.setFechaEnvio(LocalDateTime.now());
        m.setLeido(leido);
        return m;
    }

    private TicketSoporte ticket(Long id, Long clienteId) {
        TicketSoporte t = new TicketSoporte();
        t.setId(id);
        t.setClienteId(clienteId);
        EstadoTicket e = new EstadoTicket(); e.setId(1L); e.setNombre("ABIERTO");
        t.setEstado(e);
        return t;
    }

    private TicketSoporte ticketConEmpleado(Long id, Long clienteId, Long empleadoId) {
        TicketSoporte t = ticket(id, clienteId);
        t.setEmpleadoAsignadoId(empleadoId);
        return t;
    }

    private TicketSoporte ticketCerrado(Long id, Long clienteId, Long empleadoId) {
        TicketSoporte t = new TicketSoporte();
        t.setId(id);
        t.setClienteId(clienteId);
        t.setEmpleadoAsignadoId(empleadoId);
        EstadoTicket e = new EstadoTicket(); e.setId(5L); e.setNombre("CERRADO");
        t.setEstado(e);
        return t;
    }

    @Nested
    @DisplayName("readAllMensajes")
    class ReadAll {

        @Test
        @DisplayName("retorna lista completa de mensajes")
        void retornaLista() {
            when(repo.findAll()).thenReturn(List.of(
                    mensaje(1L, 10L, "Hola", false),
                    mensaje(2L, 10L, "Ok", true)));
            assertThat(service.readAllMensajes()).hasSize(2);
        }

        @Test
        @DisplayName("retorna lista vacía cuando no hay mensajes")
        void retornaVacio() {
            when(repo.findAll()).thenReturn(List.of());
            assertThat(service.readAllMensajes()).isEmpty();
        }
    }

    @Nested
    @DisplayName("enviarMensajeChat")
    class EnviarMensaje {

        @Test
        @DisplayName("crea y retorna mensaje con leido=false y fecha asignada")
        void creaConLeidoFalse() {
            when(ticketRepo.findById(10L)).thenReturn(Optional.of(ticket(10L, 5L)));
            when(repo.save(any())).thenAnswer(inv -> {
                MensajeChat m = inv.getArgument(0);
                m.setId(1L);
                return m;
            });

            MensajeChat resultado = service.enviarMensajeChat(10L, 5L, true, "  Hola  ");

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getContenido()).isEqualTo("Hola"); // trim aplicado
            assertThat(resultado.getLeido()).isFalse();
            assertThat(resultado.getFechaEnvio()).isNotNull();
        }

        @Test
        @DisplayName("asigna correctamente ticketId y remitenteId (soporte asignado)")
        void asignaCamposCorrectos() {
            when(ticketRepo.findById(99L)).thenReturn(Optional.of(ticketConEmpleado(99L, 1L, 7L)));
            when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            MensajeChat resultado = service.enviarMensajeChat(99L, 7L, false, "Mensaje");

            assertThat(resultado.getTicketId()).isEqualTo(99L);
            assertThat(resultado.getRemitenteId()).isEqualTo(7L);
            assertThat(resultado.getEsCliente()).isFalse();
        }

        @Test
        @DisplayName("lanza 404 cuando el ticket no existe")
        void lanzaExcepcionTicketNoExiste() {
            when(ticketRepo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.enviarMensajeChat(99L, 5L, true, "Hola"))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("lanza 403 cuando cliente intenta enviar a ticket ajeno")
        void lanzaExcepcionTicketAjeno() {
            when(ticketRepo.findById(10L)).thenReturn(Optional.of(ticket(10L, 99L)));

            assertThatThrownBy(() -> service.enviarMensajeChat(10L, 5L, true, "Hola"))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("soporte asignado puede enviar mensaje")
        void soporteAsignadoEnvia() {
            when(ticketRepo.findById(10L)).thenReturn(Optional.of(ticketConEmpleado(10L, 99L, 7L)));
            when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            MensajeChat resultado = service.enviarMensajeChat(10L, 7L, false, "Mensaje soporte");

            assertThat(resultado.getContenido()).isEqualTo("Mensaje soporte");
            assertThat(resultado.getRemitenteId()).isEqualTo(7L);
        }

        @Test
        @DisplayName("lanza 403 cuando soporte no asignado intenta responder")
        void lanzaExcepcionSoporteNoAsignado() {
            when(ticketRepo.findById(10L)).thenReturn(Optional.of(ticketConEmpleado(10L, 99L, 99L)));

            assertThatThrownBy(() -> service.enviarMensajeChat(10L, 7L, false, "Mensaje"))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);
        }

        @Test
        @DisplayName("admin puede enviar a cualquier ticket sin importar asignacion")
        void adminEnviaACualquierTicket() {
            when(ticketRepo.findById(10L)).thenReturn(Optional.of(ticketConEmpleado(10L, 99L, 99L)));
            when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            MensajeChat resultado = service.enviarMensajeChat(10L, 7L, false, "Mensaje admin", true);

            assertThat(resultado.getContenido()).isEqualTo("Mensaje admin");
            assertThat(resultado.getRemitenteId()).isEqualTo(7L);
        }

        @Test
        @DisplayName("lanza 400 cuando el ticket esta cerrado")
        void lanzaExcepcionTicketCerrado() {
            when(ticketRepo.findById(10L)).thenReturn(Optional.of(ticketCerrado(10L, 5L, 7L)));

            assertThatThrownBy(() -> service.enviarMensajeChat(10L, 7L, false, "Mensaje"))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("obtenerHistorialChat")
    class HistorialChat {

        @Test
        @DisplayName("retorna mensajes ordenados del ticket")
        void retornaHistorial() {
            List<MensajeChat> mensajes = List.of(
                    mensaje(1L, 10L, "Hola", false),
                    mensaje(2L, 10L, "¿En qué le ayudo?", false));

            when(repo.findByTicketIdOrderByFechaEnvioAsc(10L)).thenReturn(mensajes);

            List<MensajeChat> resultado = service.obtenerHistorialChat(10L);
            assertThat(resultado).hasSize(2);
        }

        @Test
        @DisplayName("retorna lista vacía si el ticket no tiene mensajes")
        void retornaVacio() {
            when(repo.findByTicketIdOrderByFechaEnvioAsc(99L)).thenReturn(List.of());
            assertThat(service.obtenerHistorialChat(99L)).isEmpty();
        }
    }

    @Nested
    @DisplayName("marcarComoLeido")
    class MarcarLeido {

        @Test
        @DisplayName("marca el mensaje como leído correctamente")
        void marcaLeido() {
            MensajeChat m = mensaje(1L, 10L, "Hola", false);
            when(repo.findById(1L)).thenReturn(Optional.of(m));
            when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.marcarComoLeido(1L);

            assertThat(m.getLeido()).isTrue();
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando el mensaje no existe")
        void lanzaExcepcion() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.marcarComoLeido(99L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("marcarTodosComoLeidos")
    class MarcarTodosLeidos {

        @Test
        @DisplayName("marca todos los mensajes del ticket como leídos")
        void marcaTodos() {
            List<MensajeChat> mensajes = List.of(
                    mensaje(1L, 10L, "Hola", false),
                    mensaje(2L, 10L, "Ok", false));

            when(repo.findByTicketIdOrderByFechaEnvioAsc(10L)).thenReturn(mensajes);
            when(repo.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

            service.marcarTodosComoLeidos(10L);

            assertThat(mensajes).allMatch(MensajeChat::getLeido);
        }
    }

    @Nested
    @DisplayName("findMensajeById")
    class FindById {

        @Test
        @DisplayName("retorna el mensaje cuando existe")
        void retornaMensaje() {
            when(repo.findById(1L)).thenReturn(Optional.of(mensaje(1L, 10L, "Hola", false)));
            assertThat(service.findMensajeById(1L).getContenido()).isEqualTo("Hola");
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando no existe")
        void lanzaExcepcion() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findMensajeById(99L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("deleteMensajeById")
    class Delete {

        @Test
        @DisplayName("elimina mensaje existente sin excepción")
        void elimina() {
            when(repo.findById(1L)).thenReturn(Optional.of(mensaje(1L, 10L, "Hola", false)));
            doNothing().when(repo).deleteById(1L);

            assertThatCode(() -> service.deleteMensajeById(1L)).doesNotThrowAnyException();
            verify(repo).deleteById(1L);
        }

        @Test
        @DisplayName("lanza NoExisteEnBdException cuando no existe")
        void lanzaExcepcion() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteMensajeById(99L))
                    .isInstanceOf(NoExisteEnBdException.class)
                    .hasMessageContaining("99");

            verify(repo, never()).deleteById(any());
        }
    }
}