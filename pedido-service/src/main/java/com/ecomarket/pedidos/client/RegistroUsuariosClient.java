package com.ecomarket.pedidos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.pedidos.dto.PerfilUsuarioDTO;
import com.ecomarket.pedidos.exception.NoExisteEnBdException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistroUsuariosClient {

    private final RestTemplate restTemplate;

    @Value("${app.services.registro-usuarios-url}")
    private String baseUrl;

    public PerfilUsuarioDTO obtenerUsuario(Long usuarioId) {
        try {
            String url = baseUrl + "/api/usuarios/" + usuarioId;
            return restTemplate.getForEntity(url, PerfilUsuarioDTO.class).getBody();
        } catch (Exception e) {
            throw new NoExisteEnBdException("El usuario con ID " + usuarioId + " no existe.");
        }
    }
}
