package com.horacio.ecomarket.usuarios.service;

import com.horacio.ecomarket.usuarios.dto.*;

public interface AuthService {

    IniciarSesionResponse iniciarSesion(IniciarSesionRequest request);

    MensajeResponse cerrarSesion(CerrarSesionRequest request);

    AutenticarJWTResponse autenticarJWT(AutenticarJWTRequest request);

    MensajeResponse cambiarCorreo(CambiarCorreoRequest request);

    MensajeResponse cambiarContrasena(CambiarContrasenaRequest request);

    MensajeResponse recuperarCredenciales(RecuperarCredencialesRequest request);

    MensajeResponse restablecerConToken(RestablecerConTokenRequest request);

    MensajeResponse inhabilitarCredenciales(InhabilitarCredencialesRequest request);

    MensajeResponse crearCredencial(CrearCredencialRequest request);
}
