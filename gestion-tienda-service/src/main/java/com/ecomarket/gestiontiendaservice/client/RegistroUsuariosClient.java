package com.ecomarket.gestiontiendaservice.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistroUsuariosClient {

    private final RestTemplate restTemplate;

    @Value("${app.services.registro-usuarios-url}")
    private String registroUsuariosUrl;

    public EmpleadoDTO obtenerEmpleado(Long empleadoId) {
        String url = registroUsuariosUrl + "/api/usuarios/" + empleadoId;
        try {
            log.info("Consultando empleado {} en registro-usuarios-service", empleadoId);
            return restTemplate.getForObject(url, EmpleadoDTO.class);
        } catch (Exception e) {
            log.error("Error al obtener empleado {}: {}", empleadoId, e.getMessage());
            throw new RuntimeException("No se pudo obtener el empleado: " + empleadoId, e);
        }
    }

    public boolean empleadoExiste(Long empleadoId) {
        try {
            EmpleadoDTO empleado = obtenerEmpleado(empleadoId);
            return empleado != null;
        } catch (Exception e) {
            log.warn("No se pudo verificar existencia del empleado {}: {}", empleadoId, e.getMessage());
            return false;
        }
    }

    public String obtenerRolNombrePorUsuarioId(Long usuarioId) {
        String url = registroUsuariosUrl + "/api/usuarios/" + usuarioId;
        try {
            log.info("Consultando rol del usuario {} en registro-usuarios-service", usuarioId);
            EmpleadoDTO empleado = restTemplate.getForObject(url, EmpleadoDTO.class);
            if (empleado != null && empleado.getRol() != null) {
                return empleado.getRol().getNombre();
            }
            return null;
        } catch (Exception e) {
            log.error("Error al obtener rol del usuario {}: {}", usuarioId, e.getMessage());
            return null;
        }
    }
}
