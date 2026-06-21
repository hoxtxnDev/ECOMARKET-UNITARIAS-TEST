package com.ecomarket.catalogoinventarioservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ecomarket.catalogoinventarioservice.dto.ProductoRequestDTO;
import com.ecomarket.catalogoinventarioservice.exception.NoExisteEnBdException;
import com.ecomarket.catalogoinventarioservice.exception.YaExisteEnBdException;
import com.ecomarket.catalogoinventarioservice.model.CategoriaProducto;
import com.ecomarket.catalogoinventarioservice.model.EspecificacionTecnica;
import com.ecomarket.catalogoinventarioservice.model.EstadoDisponibilidad;
import com.ecomarket.catalogoinventarioservice.model.Producto;
import com.ecomarket.catalogoinventarioservice.repository.CategoriaProductoRepository;
import com.ecomarket.catalogoinventarioservice.repository.EspecificacionTecnicaRepository;
import com.ecomarket.catalogoinventarioservice.repository.EstadoDisponibilidadRepository;
import com.ecomarket.catalogoinventarioservice.repository.ProductoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CatalogoService {
    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final EstadoDisponibilidadRepository estadoRepository;
    private final EspecificacionTecnicaRepository especificacionRepository;

    // --- Producto ---

    public List<Producto> navegarCatalogo(Long categoriaId) {
        CategoriaProducto categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new NoExisteEnBdException("Categoría no encontrada"));
        return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> buscarCoincidenciaPorNombre(String nombreCoincidencia) {
        return productoRepository.findByNombreContainingIgnoreCase(nombreCoincidencia);
    }

    public Producto consultarDetalles(Long productoId) {
        return productoRepository.findById(productoId)
                .orElseThrow(() -> new NoExisteEnBdException("Producto no encontrado: " + productoId));
    }

    public Producto agregarProducto(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        producto.setSku(dto.getSku());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioBase(dto.getPrecioBase());
        producto.setImagenUrl(dto.getImagenUrl());

        CategoriaProducto cat = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new NoExisteEnBdException("Categoría no encontrada: " + dto.getCategoriaId()));
        producto.setCategoria(cat);

        EstadoDisponibilidad est = estadoRepository.findById(dto.getEstadoId())
                .orElseThrow(() -> new NoExisteEnBdException("Estado no encontrado: " + dto.getEstadoId()));
        producto.setEstado(est);

        producto.setFechaCreacion(LocalDateTime.now());
        try {
            return productoRepository.save(producto);
        } catch (DataIntegrityViolationException e) {
            throw new YaExisteEnBdException("El producto con SKU '" + producto.getSku() + "' ya existe.");
        }
    }

    public Producto editarProducto(Long productoId, ProductoRequestDTO dto) {
        Producto existente = consultarDetalles(productoId);
        existente.setSku(dto.getSku());
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setPrecioBase(dto.getPrecioBase());
        existente.setImagenUrl(dto.getImagenUrl());

        CategoriaProducto cat = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new NoExisteEnBdException("Categoría no encontrada: " + dto.getCategoriaId()));
        existente.setCategoria(cat);

        EstadoDisponibilidad est = estadoRepository.findById(dto.getEstadoId())
                .orElseThrow(() -> new NoExisteEnBdException("Estado no encontrado: " + dto.getEstadoId()));
        existente.setEstado(est);

        return productoRepository.save(existente);
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public boolean eliminarProducto(Long productoId) {
        if (!productoRepository.existsById(productoId)) return false;
        productoRepository.deleteById(productoId);
        return true;
    }

    // --- CategoriaProducto ---

    public List<CategoriaProducto> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public CategoriaProducto obtenerCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new NoExisteEnBdException("Categoría no encontrada: " + id));
    }

    public CategoriaProducto crearCategoria(CategoriaProducto categoria) {
        return categoriaRepository.save(categoria);
    }

    public CategoriaProducto editarCategoria(Long id, CategoriaProducto datos) {
        CategoriaProducto existente = obtenerCategoria(id);
        existente.setNombre(datos.getNombre());
        return categoriaRepository.save(existente);
    }

    public boolean eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) return false;
        categoriaRepository.deleteById(id);
        return true;
    }

    // --- EstadoDisponibilidad ---

    public List<EstadoDisponibilidad> listarEstados() {
        return estadoRepository.findAll();
    }

    public EstadoDisponibilidad obtenerEstado(Long id) {
        return estadoRepository.findById(id)
                .orElseThrow(() -> new NoExisteEnBdException("Estado no encontrado: " + id));
    }

    public EstadoDisponibilidad crearEstado(EstadoDisponibilidad estado) {
        return estadoRepository.save(estado);
    }

    public EstadoDisponibilidad editarEstado(Long id, EstadoDisponibilidad datos) {
        EstadoDisponibilidad existente = obtenerEstado(id);
        existente.setNombre(datos.getNombre());
        return estadoRepository.save(existente);
    }

    public boolean eliminarEstado(Long id) {
        if (!estadoRepository.existsById(id)) return false;
        estadoRepository.deleteById(id);
        return true;
    }

    // --- EspecificacionTecnica ---

    public List<EspecificacionTecnica> listarEspecificaciones() {
        return especificacionRepository.findAll();
    }

    public List<EspecificacionTecnica> listarEspecificacionesPorProducto(Long productoId) {
        return especificacionRepository.findAll().stream()
                .filter(e -> e.getProducto().getId().equals(productoId))
                .toList();
    }

    public EspecificacionTecnica obtenerEspecificacion(Long id) {
        return especificacionRepository.findById(id)
                .orElseThrow(() -> new NoExisteEnBdException("Especificación no encontrada: " + id));
    }

    public EspecificacionTecnica crearEspecificacion(EspecificacionTecnica especificacion) {
        return especificacionRepository.save(especificacion);
    }

    public EspecificacionTecnica editarEspecificacion(Long id, EspecificacionTecnica datos) {
        EspecificacionTecnica existente = obtenerEspecificacion(id);
        existente.setClave(datos.getClave());
        existente.setValor(datos.getValor());
        existente.setProducto(datos.getProducto());
        return especificacionRepository.save(existente);
    }

    public boolean eliminarEspecificacion(Long id) {
        if (!especificacionRepository.existsById(id)) return false;
        especificacionRepository.deleteById(id);
        return true;
    }
}
