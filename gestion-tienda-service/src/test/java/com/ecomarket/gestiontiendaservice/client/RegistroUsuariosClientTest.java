package com.ecomarket.gestiontiendaservice.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroUsuariosClientTest {

    @Mock
    RestTemplate restTemplate;

    RegistroUsuariosClient client;

    @BeforeEach
    void setup() {
        client = new RegistroUsuariosClient(restTemplate);
        ReflectionTestUtils.setField(client, "registroUsuariosUrl", "http://localhost:8085");
    }

    @Nested
    @DisplayName("obtenerEmpleado")
    class ObtenerEmpleado {

        @Test
        @DisplayName("retorna empleado cuando existe")
        void exito() {
            EmpleadoDTO emp = new EmpleadoDTO();
            emp.setId(1L);
            emp.setNombre("Juan");
            when(restTemplate.getForObject(
                    "http://localhost:8085/api/usuarios/1",
                    EmpleadoDTO.class))
                    .thenReturn(emp);

            EmpleadoDTO resultado = client.obtenerEmpleado(1L);

            assertThat(resultado.getNombre()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("lanza RuntimeException cuando falla la llamada")
        void error() {
            when(restTemplate.getForObject(
                    "http://localhost:8085/api/usuarios/1",
                    EmpleadoDTO.class))
                    .thenThrow(new RuntimeException("Error externo"));

            assertThatThrownBy(() -> client.obtenerEmpleado(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No se pudo obtener el empleado");
        }
    }

    @Nested
    @DisplayName("empleadoExiste")
    class EmpleadoExiste {

        @Test
        @DisplayName("retorna true cuando empleado existe")
        void existe() {
            EmpleadoDTO emp = new EmpleadoDTO();
            emp.setId(1L);
            when(restTemplate.getForObject(
                    "http://localhost:8085/api/usuarios/1",
                    EmpleadoDTO.class))
                    .thenReturn(emp);

            boolean resultado = client.empleadoExiste(1L);

            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("retorna false cuando obtenerEmpleado lanza excepcion")
        void noExistePorError() {
            when(restTemplate.getForObject(
                    "http://localhost:8085/api/usuarios/1",
                    EmpleadoDTO.class))
                    .thenThrow(new RuntimeException("Error"));

            boolean resultado = client.empleadoExiste(1L);

            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("retorna false cuando obtenerEmpleado retorna null")
        void noExistePorNull() {
            when(restTemplate.getForObject(
                    "http://localhost:8085/api/usuarios/1",
                    EmpleadoDTO.class))
                    .thenReturn(null);

            boolean resultado = client.empleadoExiste(1L);

            assertThat(resultado).isFalse();
        }
    }

    @Nested
    @DisplayName("obtenerRolNombrePorUsuarioId")
    class ObtenerRolNombrePorUsuarioId {

        @Test
        @DisplayName("retorna el nombre del rol cuando el empleado existe")
        void exito() {
            RolDTO rol = new RolDTO();
            rol.setId(1L);
            rol.setNombre("GERENTE");
            EmpleadoDTO emp = new EmpleadoDTO();
            emp.setId(1L);
            emp.setRol(rol);
            when(restTemplate.getForObject(
                    "http://localhost:8085/api/usuarios/1",
                    EmpleadoDTO.class))
                    .thenReturn(emp);

            String resultado = client.obtenerRolNombrePorUsuarioId(1L);

            assertThat(resultado).isEqualTo("GERENTE");
        }

        @Test
        @DisplayName("retorna null cuando el empleado no tiene rol")
        void empleadoSinRol() {
            EmpleadoDTO emp = new EmpleadoDTO();
            emp.setId(1L);
            when(restTemplate.getForObject(
                    "http://localhost:8085/api/usuarios/1",
                    EmpleadoDTO.class))
                    .thenReturn(emp);

            String resultado = client.obtenerRolNombrePorUsuarioId(1L);

            assertThat(resultado).isNull();
        }

        @Test
        @DisplayName("retorna null cuando empleado es null")
        void empleadoNull() {
            when(restTemplate.getForObject(
                    "http://localhost:8085/api/usuarios/1",
                    EmpleadoDTO.class))
                    .thenReturn(null);

            String resultado = client.obtenerRolNombrePorUsuarioId(1L);

            assertThat(resultado).isNull();
        }

        @Test
        @DisplayName("retorna null cuando la llamada falla")
        void error() {
            when(restTemplate.getForObject(
                    "http://localhost:8085/api/usuarios/1",
                    EmpleadoDTO.class))
                    .thenThrow(new RuntimeException("Error"));

            String resultado = client.obtenerRolNombrePorUsuarioId(1L);

            assertThat(resultado).isNull();
        }
    }
}
