package com.ecomarket.soporteservice.controller;

import com.ecomarket.soporteservice.dto.MensajeChatRequestDTO;
import com.ecomarket.soporteservice.dto.NotificacionRequestDTO;
import com.ecomarket.soporteservice.dto.ResenaRequestDTO;
import com.ecomarket.soporteservice.dto.SoporteTicketRequestDTO;
import com.ecomarket.soporteservice.exception.GlobalExceptionHandler;
import com.ecomarket.soporteservice.exception.NoExisteEnBdException;
import com.ecomarket.soporteservice.exception.PedidoClienteIncompatibleException;
import com.ecomarket.soporteservice.model.entity.MensajeChat;
import com.ecomarket.soporteservice.model.entity.Notificacion;
import com.ecomarket.soporteservice.model.entity.Resena;
import com.ecomarket.soporteservice.model.entity.TicketSoporte;
import com.ecomarket.soporteservice.model.reference.CanalNotificacion;
import com.ecomarket.soporteservice.model.reference.CategoriaTicket;
import com.ecomarket.soporteservice.model.reference.EstadoTicket;
import com.ecomarket.soporteservice.service.NotificacionService;
import com.ecomarket.soporteservice.service.ResenaService;
import com.ecomarket.soporteservice.service.SoporteService;
import com.ecomarket.soporteservice.service.TicketSoporteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
@DisplayName("SoporteController")
class SoporteControllerTest {

    MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock SoporteService soporteService;
    @Mock TicketSoporteService ticketSoporteService;
    @Mock ResenaService resenaService;
    @Mock NotificacionService notificacionService;

    @BeforeEach
    void setup() {
        mvc = standaloneSetup(new SoporteController(soporteService, ticketSoporteService, resenaService, notificacionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private TicketSoporte ticket(Long id) {
        TicketSoporte t = new TicketSoporte();
        t.setId(id);
        t.setClienteId(5L);
        t.setAsunto("Problema con entrega");
        t.setPedidoRelacionadoId(10L);
        EstadoTicket e = new EstadoTicket(); e.setId(1L); e.setNombre("ABIERTO");
        t.setEstado(e);
        CategoriaTicket c = new CategoriaTicket(); c.setId(1L); c.setNombre("ENTREGA");
        t.setCategoria(c);
        t.setFechaCreacion(LocalDateTime.now());
        return t;
    }

    private Notificacion notif(Long id) {
        return Notificacion.builder()
                .id(id).destinatarioId(5L)
                .canal(new CanalNotificacion(1L, "EMAIL"))
                .titulo("Pedido listo").cuerpo("Tu pedido está listo")
                .fechaEnvioNotificacion(LocalDateTime.now())
                .enviadaConExito(true)
                .build();
    }

    private MensajeChat mensaje(Long id) {
        MensajeChat m = new MensajeChat();
        m.setId(id); m.setTicketId(1L); m.setRemitenteId(5L);
        m.setEsCliente(true); m.setContenido("Hola");
        m.setFechaEnvio(LocalDateTime.now()); m.setLeido(false);
        return m;
    }

    private Resena resena(Long id) {
        Resena r = new Resena();
        r.setId(id); r.setProductoId(10L); r.setClienteId(5L);
        r.setCalificacionEstrellas(8);
        r.setComentario("Muy buen producto, lo recomiendo");
        r.setFechaPublicacion(LocalDateTime.now());
        r.setModeracionAprobado(false);
        return r;
    }

    @Nested
    @DisplayName("enviarNotificacionPush")
    class EnviarNotif {

        @Test
        @DisplayName("200 OK al enviar notificación válida")
        void exitoso() throws Exception {
            NotificacionRequestDTO dto = new NotificacionRequestDTO();
            dto.setDestinatarioId(5L); dto.setTitulo("Pedido listo");
            dto.setMensaje("Tu pedido está listo"); dto.setCanalId(1L);

            when(soporteService.enviarNotificacionPush(5L, "Pedido listo", "Tu pedido está listo", 1L))
                    .thenReturn(notif(1L));

            mvc.perform(post("/api/v1/soporte/enviar-notificacion-push")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("400 al faltar campos obligatorios")
        void invalido() throws Exception {
            NotificacionRequestDTO dto = new NotificacionRequestDTO();
            dto.setTitulo("Sin destinatario");
            mvc.perform(post("/api/v1/soporte/enviar-notificacion-push")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 cuando canal no existe")
        void canalNoExiste() throws Exception {
            NotificacionRequestDTO dto = new NotificacionRequestDTO();
            dto.setDestinatarioId(5L); dto.setTitulo("T"); dto.setMensaje("M"); dto.setCanalId(99L);

            when(soporteService.enviarNotificacionPush(anyLong(), anyString(), anyString(), eq(99L)))
                    .thenThrow(new NoExisteEnBdException("Canal 99 no existe."));

            mvc.perform(post("/api/v1/soporte/enviar-notificacion-push")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("ingresarTicket")
    class IngresarTicket {

        private SoporteTicketRequestDTO dto() {
            SoporteTicketRequestDTO d = new SoporteTicketRequestDTO();
            d.setClienteId(5L); d.setCategoriaId(1L);
            d.setAsunto("Problema con entrega"); d.setPedidoId(10L);
            return d;
        }

        @Test
        @DisplayName("200 OK al ingresar ticket válido")
        void exitoso() throws Exception {
            when(soporteService.ingresarTicket(5L, 1L, "Problema con entrega", 10L)).thenReturn(ticket(1L));
            mvc.perform(post("/api/v1/soporte/ingresar-ticket")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("400 al faltar campos obligatorios")
        void invalido() throws Exception {
            SoporteTicketRequestDTO d = new SoporteTicketRequestDTO();
            d.setClienteId(5L);
            mvc.perform(post("/api/v1/soporte/ingresar-ticket")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(d)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 cuando el cliente no existe")
        void clienteNoExiste() throws Exception {
            when(soporteService.ingresarTicket(anyLong(), anyLong(), anyString(), anyLong()))
                    .thenThrow(new NoExisteEnBdException("Cliente no existe."));
            mvc.perform(post("/api/v1/soporte/ingresar-ticket")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto())))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("409 cuando pedido no es compatible con cliente")
        void pedidoIncompatible() throws Exception {
            when(soporteService.ingresarTicket(anyLong(), anyLong(), anyString(), anyLong()))
                    .thenThrow(new PedidoClienteIncompatibleException("Pedido no compatible."));
            mvc.perform(post("/api/v1/soporte/ingresar-ticket")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto())))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("obtenerTickets")
    class ObtenerTickets {

        @Test
        @DisplayName("GET /tickets sin param → 200 lista completa")
        void todosLosTickets() throws Exception {
            when(ticketSoporteService.readAllTickets()).thenReturn(List.of(ticket(1L), ticket(2L)));
            mvc.perform(get("/api/v1/soporte/tickets"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("GET /tickets?clienteId=5 → 200 por cliente")
        void porCliente() throws Exception {
            when(ticketSoporteService.readTicketsByClienteId(5L)).thenReturn(List.of(ticket(1L)));
            mvc.perform(get("/api/v1/soporte/tickets").param("clienteId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("GET /tickets?estadoId=1 → 200 por estado")
        void porEstado() throws Exception {
            when(ticketSoporteService.readTicketsByEstadoId(1L)).thenReturn(List.of(ticket(1L)));
            mvc.perform(get("/api/v1/soporte/tickets").param("estadoId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("GET /tickets/{id} → 200 por id")
        void porId() throws Exception {
            when(ticketSoporteService.findTicketById(1L)).thenReturn(ticket(1L));
            mvc.perform(get("/api/v1/soporte/tickets/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("GET /tickets/{id} inexistente → 404")
        void porIdNoExiste() throws Exception {
            when(ticketSoporteService.findTicketById(99L)).thenThrow(new NoExisteEnBdException("99 no existe."));
            mvc.perform(get("/api/v1/soporte/tickets/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("actualizarEstadoTicket")
    class ActualizarEstado {

        @Test
        @DisplayName("PATCH /tickets/1/estado/2 → 200")
        void exitoso() throws Exception {
            TicketSoporte t = ticket(1L);
            EstadoTicket nuevo = new EstadoTicket(); nuevo.setId(2L); nuevo.setNombre("EN_PROCESO");
            t.setEstado(nuevo);
            when(ticketSoporteService.actualizarEstadoTicket(1L, 2L)).thenReturn(t);

            mvc.perform(patch("/api/v1/soporte/tickets/1/estado/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.estado.nombre").value("EN_PROCESO"));
        }

        @Test
        @DisplayName("PATCH ticket inexistente → 404")
        void ticketNoExiste() throws Exception {
            when(ticketSoporteService.actualizarEstadoTicket(99L, 2L))
                    .thenThrow(new NoExisteEnBdException("99 no existe."));
            mvc.perform(patch("/api/v1/soporte/tickets/99/estado/2"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("asignarTicketEmpleado")
    class AsignarEmpleado {

        @Test
        @DisplayName("PATCH /tickets/1/asignar/7 → 200")
        void exitoso() throws Exception {
            TicketSoporte t = ticket(1L); t.setEmpleadoAsignadoId(7L);
            when(soporteService.asignarTicketEmpleado(1L, 7L)).thenReturn(t);

            mvc.perform(patch("/api/v1/soporte/tickets/1/asignar/7"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.empleadoAsignadoId").value(7));
        }

        @Test
        @DisplayName("PATCH ticket inexistente → 404")
        void ticketNoExiste() throws Exception {
            when(soporteService.asignarTicketEmpleado(99L, 7L))
                    .thenThrow(new NoExisteEnBdException("99 no existe."));
            mvc.perform(patch("/api/v1/soporte/tickets/99/asignar/7"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("solucionarTicket")
    class SolucionarTicket {

        @Test
        @DisplayName("PATCH /tickets/1/solucionar → 200")
        void exitoso() throws Exception {
            TicketSoporte t = ticket(1L);
            t.setSolucionResumen("Se reenvió el paquete.");
            EstadoTicket resuelto = new EstadoTicket(); resuelto.setId(4L); resuelto.setNombre("RESUELTO");
            t.setEstado(resuelto); t.setFechaCierre(LocalDateTime.now());

            when(soporteService.solucionarTicket(1L, "\"Se reenvió el paquete.\"")).thenReturn(t);

            mvc.perform(patch("/api/v1/soporte/tickets/1/solucionar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("\"Se reenvió el paquete.\""))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("PATCH ticket inexistente → 404")
        void ticketNoExiste() throws Exception {
            when(soporteService.solucionarTicket(eq(99L), anyString()))
                    .thenThrow(new NoExisteEnBdException("99 no existe."));
            mvc.perform(patch("/api/v1/soporte/tickets/99/solucionar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("\"Solución\""))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("eliminarTicket")
    class EliminarTicket {

        @Test
        @DisplayName("DELETE /tickets/1 → 200")
        void exitoso() throws Exception {
            doNothing().when(ticketSoporteService).deleteTicketById(1L);
            mvc.perform(delete("/api/v1/soporte/tickets/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("1")));
        }

        @Test
        @DisplayName("DELETE ticket inexistente → 404")
        void noExiste() throws Exception {
            doThrow(new NoExisteEnBdException("99 no existe.")).when(ticketSoporteService).deleteTicketById(99L);
            mvc.perform(delete("/api/v1/soporte/tickets/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("obtenerHistorialChat")
    class HistorialChat {

        @Test
        @DisplayName("GET /tickets/1/mensajes → 200 historial")
        void exitoso() throws Exception {
            when(soporteService.obtenerHistorialChat(1L)).thenReturn(List.of(mensaje(1L), mensaje(2L)));
            mvc.perform(get("/api/v1/soporte/tickets/1/mensajes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("GET /tickets/99/mensajes ticket inexistente → 404")
        void ticketNoExiste() throws Exception {
            when(soporteService.obtenerHistorialChat(99L)).thenThrow(new NoExisteEnBdException("99 no existe."));
            mvc.perform(get("/api/v1/soporte/tickets/99/mensajes"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("enviarMensajeChat")
    class EnviarMensaje {

        private MensajeChatRequestDTO dto() {
            MensajeChatRequestDTO d = new MensajeChatRequestDTO();
            d.setTicketId(1L); d.setRemitenteId(5L);
            d.setEsCliente(true); d.setContenido("Hola");
            return d;
        }

        @Test
        @DisplayName("POST /enviar-mensaje-chat → 201")
        void exitoso() throws Exception {
            when(soporteService.enviarMensajeChat(1L, 5L, true, "Hola")).thenReturn(mensaje(1L));
            mvc.perform(post("/api/v1/soporte/enviar-mensaje-chat")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("POST contenido vacío → 400")
        void invalido() throws Exception {
            MensajeChatRequestDTO d = new MensajeChatRequestDTO();
            d.setTicketId(1L); d.setRemitenteId(5L); d.setEsCliente(true); d.setContenido("");
            mvc.perform(post("/api/v1/soporte/enviar-mensaje-chat")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(d)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("obtenerNotificaciones")
    class ObtenerNotificaciones {

        @Test
        @DisplayName("GET /notificaciones sin param → 200 lista completa")
        void sinParam() throws Exception {
            when(notificacionService.readAllNotificacion()).thenReturn(List.of(notif(1L)));
            mvc.perform(get("/api/v1/soporte/notificaciones"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("GET /notificaciones?destinatarioId=5 → 200 filtrado")
        void conDestinatario() throws Exception {
            when(notificacionService.readNotificacionesByDestinatarioId(5L)).thenReturn(List.of(notif(1L)));
            mvc.perform(get("/api/v1/soporte/notificaciones").param("destinatarioId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("GET /notificaciones/{id} → 200")
        void porId() throws Exception {
            when(notificacionService.findNotificacionById(1L)).thenReturn(notif(1L));
            mvc.perform(get("/api/v1/soporte/notificaciones/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("DELETE /notificaciones/1 → 200")
        void eliminar() throws Exception {
            doNothing().when(notificacionService).deleteNotificacionById(1L);
            mvc.perform(delete("/api/v1/soporte/notificaciones/1"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("DELETE notificacion inexistente → 404")
        void eliminarNoExiste() throws Exception {
            doThrow(new NoExisteEnBdException("99 no existe.")).when(notificacionService).deleteNotificacionById(99L);
            mvc.perform(delete("/api/v1/soporte/notificaciones/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("resenas via SoporteController")
    class ResenasSoporte {

        private ResenaRequestDTO dto() {
            ResenaRequestDTO d = new ResenaRequestDTO();
            d.setProductoId(10L); d.setClienteId(5L);
            d.setCalificacionEstrellas(4);
            d.setComentario("Muy buen producto, lo recomiendo");
            return d;
        }

        @Test
        @DisplayName("POST /dejar-resena → 201")
        void dejarResena() throws Exception {
            when(soporteService.dejarResena(10L, 5L, 4, "Muy buen producto, lo recomiendo")).thenReturn(resena(1L));
            mvc.perform(post("/api/v1/soporte/dejar-resena")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(dto())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("GET /resenas sin param → 200 todas")
        void todasLasResenas() throws Exception {
            when(resenaService.readAllResenas()).thenReturn(List.of(resena(1L)));
            mvc.perform(get("/api/v1/soporte/resenas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("GET /resenas?productoId=10 → 200 por producto")
        void porProducto() throws Exception {
            when(resenaService.readResenasByProductoId(10L)).thenReturn(List.of(resena(1L)));
            mvc.perform(get("/api/v1/soporte/resenas").param("productoId", "10"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("GET /resenas?clienteId=5 → 200 por cliente")
        void porCliente() throws Exception {
            when(resenaService.readResenasByClienteId(5L)).thenReturn(List.of(resena(1L)));
            mvc.perform(get("/api/v1/soporte/resenas").param("clienteId", "5"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("PATCH /resenas/1/aprobar → 200")
        void aprobar() throws Exception {
            doNothing().when(resenaService).aprobarModeracion(1L);
            mvc.perform(patch("/api/v1/soporte/resenas/1/aprobar"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("aprobada")));
        }

        @Test
        @DisplayName("PATCH /resenas/1/rechazar → 200")
        void rechazar() throws Exception {
            doNothing().when(resenaService).rechazarModeracion(1L);
            mvc.perform(patch("/api/v1/soporte/resenas/1/rechazar"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("rechazada")));
        }

        @Test
        @DisplayName("DELETE /resenas/1 → 200")
        void eliminar() throws Exception {
            doNothing().when(resenaService).deleteResenaById(1L);
            mvc.perform(delete("/api/v1/soporte/resenas/1"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("DELETE reseña inexistente → 404")
        void eliminarNoExiste() throws Exception {
            doThrow(new NoExisteEnBdException("99 no existe.")).when(resenaService).deleteResenaById(99L);
            mvc.perform(delete("/api/v1/soporte/resenas/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
