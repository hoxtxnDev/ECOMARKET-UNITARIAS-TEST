package com.horacio.ecomarket.usuarios.service;

import com.horacio.ecomarket.usuarios.exception.RecursoNoEncontradoException;
import com.horacio.ecomarket.usuarios.model.Direccion;
import com.horacio.ecomarket.usuarios.repository.DireccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DireccionService {

    private final DireccionRepository direccionRepository;

    public Direccion agregarDireccion(Long usuarioId, Direccion direccion) {
        // Si se marca como predeterminada, quitamos la marca a las demás
        if (Boolean.TRUE.equals(direccion.getEsPredeterminada())) {
            marcarTodasComoNoPredeterminadas(usuarioId);
        }
        direccion.setUsuarioId(usuarioId);
        return direccionRepository.save(direccion);
    }

    public List<Direccion> listarDirecciones(Long usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId);
    }

    public Direccion obtenerPorId(Long id) {
        return direccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La dirección con id " + id + " no existe."));
    }

    public Direccion obtenerPredeterminada(Long usuarioId) {
        return direccionRepository.findByUsuarioIdAndEsPredeterminadaTrue(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no tiene una dirección predeterminada configurada."));
    }

    public Direccion editarDireccion(Long id, Direccion datosNuevos) {
        Direccion dir = direccionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Dirección no encontrada."));
        
        if (Boolean.TRUE.equals(datosNuevos.getEsPredeterminada())) {
            marcarTodasComoNoPredeterminadas(dir.getUsuarioId());
        }
        
        dir.setCalle(datosNuevos.getCalle());
        dir.setNumero(datosNuevos.getNumero());
        dir.setCiudad(datosNuevos.getCiudad());
        dir.setRegion(datosNuevos.getRegion());
        dir.setDestinatario(datosNuevos.getDestinatario());
        dir.setEsPredeterminada(datosNuevos.getEsPredeterminada());
        
        return direccionRepository.save(dir);
    }

    public void eliminarDireccion(Long id) {
        direccionRepository.deleteById(id);
    }

    @Transactional
    public void marcarTodasComoNoPredeterminadas(Long usuarioId) {
        List<Direccion> direcciones = direccionRepository.findByUsuarioId(usuarioId);
        direcciones.forEach(d -> d.setEsPredeterminada(false));
        direccionRepository.saveAll(direcciones);
    }
}
