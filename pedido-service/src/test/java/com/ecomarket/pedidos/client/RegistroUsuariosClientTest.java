package com.ecomarket.pedidos.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.pedidos.dto.PerfilUsuarioDTO;
import com.ecomarket.pedidos.exception.NoExisteEnBdException;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistroUsuariosClient")
class RegistroUsuariosClientTest {

    @Mock private RestTemplate restTemplate;

    private RegistroUsuariosClient client;

    @BeforeEach
    void setUp() {
        client = new RegistroUsuariosClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8081");
    }

    @Test
    @DisplayName("obtenerUsuario retorna perfil cuando existe")
    void obtenerUsuarioExitoso() {
        PerfilUsuarioDTO expected = new PerfilUsuarioDTO();
        when(restTemplate.getForEntity("http://localhost:8081/api/usuarios/1", PerfilUsuarioDTO.class))
                .thenReturn(ResponseEntity.ok(expected));

        PerfilUsuarioDTO result = client.obtenerUsuario(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("obtenerUsuario lanza NoExisteEnBdException cuando falla")
    void obtenerUsuarioNoExiste() {
        when(restTemplate.getForEntity("http://localhost:8081/api/usuarios/999", PerfilUsuarioDTO.class))
                .thenThrow(mock(HttpClientErrorException.class));

        assertThrows(NoExisteEnBdException.class, () -> client.obtenerUsuario(999L));
    }
}
