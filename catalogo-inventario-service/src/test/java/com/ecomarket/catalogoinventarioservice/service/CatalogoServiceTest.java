package com.ecomarket.catalogoinventarioservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

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

@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    @Mock private ProductoRepository productoRepository;
    @Mock private CategoriaProductoRepository categoriaRepository;
    @Mock private EstadoDisponibilidadRepository estadoRepository;
    @Mock private EspecificacionTecnicaRepository especificacionRepository;

    @InjectMocks private CatalogoService catalogoService;

    private Producto producto;
    private CategoriaProducto categoria;
    private EstadoDisponibilidad estado;

    @BeforeEach
    void setUp() {
        categoria = new CategoriaProducto(1L, "Electronica");
        estado = new EstadoDisponibilidad(1L, "Disponible");
        producto = new Producto(1L, "SKU-001", "Laptop", "Laptop Gamer", 1500.0, categoria, estado, "img.jpg", LocalDateTime.now());
    }

    @Test
    void navegarCatalogoReturnsProductsWhenCategoryExists() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.findByCategoria(categoria)).thenReturn(List.of(producto));

        List<Producto> result = catalogoService.navegarCatalogo(1L);

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getNombre());
    }

    @Test
    void navegarCatalogoThrowsWhenCategoryNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> catalogoService.navegarCatalogo(99L));
    }

    @Test
    void buscarCoincidenciaPorNombreReturnsFilteredProducts() {
        when(productoRepository.findByNombreContainingIgnoreCase("laptop")).thenReturn(List.of(producto));

        List<Producto> result = catalogoService.buscarCoincidenciaPorNombre("laptop");

        assertEquals(1, result.size());
    }

    @Test
    void consultarDetallesReturnsProductWhenFound() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto result = catalogoService.consultarDetalles(1L);

        assertNotNull(result);
        assertEquals("SKU-001", result.getSku());
    }

    @Test
    void consultarDetallesThrowsWhenNotFound() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> catalogoService.consultarDetalles(99L));
    }

    @Test
    void agregarProductoSavesWithCategoryAndEstado() {
        Producto nuevo = new Producto(null, "SKU-002", "Mouse", "Mouse inalambrico", 50.0, new CategoriaProducto(1L, null), new EstadoDisponibilidad(1L, null), null, null);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto result = catalogoService.agregarProducto(nuevo);

        assertNotNull(result);
        assertEquals(categoria, result.getCategoria());
        assertEquals(estado, result.getEstado());
    }

    @Test
    void agregarProductoThrowsWhenDuplicateSku() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(productoRepository.save(any(Producto.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(YaExisteEnBdException.class, () -> catalogoService.agregarProducto(producto));
    }

    @Test
    void editarProductoUpdatesExistingProduct() {
        Producto nuevosDatos = new Producto(null, "SKU-002", "Laptop Pro", "Nueva descripcion", 2000.0, new CategoriaProducto(1L, null), new EstadoDisponibilidad(1L, null), "new.jpg", null);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto result = catalogoService.editarProducto(1L, nuevosDatos);

        assertEquals("SKU-002", result.getSku());
        assertEquals("Laptop Pro", result.getNombre());
        assertEquals("Nueva descripcion", result.getDescripcion());
        assertEquals(2000.0, result.getPrecioBase());
    }

    @Test
    void listarTodosReturnsAllProducts() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> result = catalogoService.listarTodos();

        assertEquals(1, result.size());
    }

    @Test
    void eliminarProductoReturnsTrueWhenExists() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        boolean result = catalogoService.eliminarProducto(1L);

        assertTrue(result);
        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminarProductoReturnsFalseWhenNotFound() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        boolean result = catalogoService.eliminarProducto(99L);

        assertFalse(result);
        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    void listarCategoriasReturnsAll() {
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        assertEquals(1, catalogoService.listarCategorias().size());
    }

    @Test
    void obtenerCategoriaReturnsWhenFound() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        assertNotNull(catalogoService.obtenerCategoria(1L));
    }

    @Test
    void obtenerCategoriaThrowsWhenNotFound() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> catalogoService.obtenerCategoria(99L));
    }

    @Test
    void crearCategoriaSavesAndReturns() {
        when(categoriaRepository.save(any(CategoriaProducto.class))).thenReturn(categoria);

        assertNotNull(catalogoService.crearCategoria(categoria));
    }

    @Test
    void editarCategoriaUpdatesName() {
        CategoriaProducto datos = new CategoriaProducto(null, "Hogar");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(CategoriaProducto.class))).thenAnswer(i -> i.getArgument(0));

        CategoriaProducto result = catalogoService.editarCategoria(1L, datos);

        assertEquals("Hogar", result.getNombre());
    }

    @Test
    void eliminarCategoriaReturnsTrueWhenExists() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        assertTrue(catalogoService.eliminarCategoria(1L));
    }

    @Test
    void eliminarCategoriaReturnsFalseWhenNotFound() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        assertFalse(catalogoService.eliminarCategoria(99L));
    }

    @Test
    void listarEstadosReturnsAll() {
        when(estadoRepository.findAll()).thenReturn(List.of(estado));

        assertEquals(1, catalogoService.listarEstados().size());
    }

    @Test
    void obtenerEstadoReturnsWhenFound() {
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estado));

        assertNotNull(catalogoService.obtenerEstado(1L));
    }

    @Test
    void obtenerEstadoThrowsWhenNotFound() {
        when(estadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> catalogoService.obtenerEstado(99L));
    }

    @Test
    void crearEstadoSavesAndReturns() {
        when(estadoRepository.save(any(EstadoDisponibilidad.class))).thenReturn(estado);

        assertNotNull(catalogoService.crearEstado(estado));
    }

    @Test
    void editarEstadoUpdatesName() {
        EstadoDisponibilidad datos = new EstadoDisponibilidad(null, "Agotado");
        when(estadoRepository.findById(1L)).thenReturn(Optional.of(estado));
        when(estadoRepository.save(any(EstadoDisponibilidad.class))).thenAnswer(i -> i.getArgument(0));

        EstadoDisponibilidad result = catalogoService.editarEstado(1L, datos);

        assertEquals("Agotado", result.getNombre());
    }

    @Test
    void eliminarEstadoReturnsTrueWhenExists() {
        when(estadoRepository.existsById(1L)).thenReturn(true);

        assertTrue(catalogoService.eliminarEstado(1L));
    }

    @Test
    void eliminarEstadoReturnsFalseWhenNotFound() {
        when(estadoRepository.existsById(99L)).thenReturn(false);

        assertFalse(catalogoService.eliminarEstado(99L));
    }

    @Test
    void listarEspecificacionesReturnsAll() {
        EspecificacionTecnica esp = new EspecificacionTecnica(1L, "RAM", "16GB", producto);
        when(especificacionRepository.findAll()).thenReturn(List.of(esp));

        assertEquals(1, catalogoService.listarEspecificaciones().size());
    }

    @Test
    void listarEspecificacionesPorProductoFiltersByProduct() {
        EspecificacionTecnica esp = new EspecificacionTecnica(1L, "RAM", "16GB", producto);
        when(especificacionRepository.findAll()).thenReturn(List.of(esp));

        List<EspecificacionTecnica> result = catalogoService.listarEspecificacionesPorProducto(1L);

        assertEquals(1, result.size());
    }

    @Test
    void obtenerEspecificacionReturnsWhenFound() {
        EspecificacionTecnica esp = new EspecificacionTecnica(1L, "RAM", "16GB", producto);
        when(especificacionRepository.findById(1L)).thenReturn(Optional.of(esp));

        assertNotNull(catalogoService.obtenerEspecificacion(1L));
    }

    @Test
    void obtenerEspecificacionThrowsWhenNotFound() {
        when(especificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> catalogoService.obtenerEspecificacion(99L));
    }

    @Test
    void crearEspecificacionSavesAndReturns() {
        EspecificacionTecnica esp = new EspecificacionTecnica(null, "RAM", "16GB", producto);
        when(especificacionRepository.save(any(EspecificacionTecnica.class))).thenReturn(esp);

        assertNotNull(catalogoService.crearEspecificacion(esp));
    }

    @Test
    void editarEspecificacionUpdatesFields() {
        Producto otroProducto = new Producto(2L, "SKU-003", "Monitor", null, 300.0, null, null, null, null);
        EspecificacionTecnica existente = new EspecificacionTecnica(1L, "RAM", "8GB", producto);
        EspecificacionTecnica datos = new EspecificacionTecnica(null, "RAM", "32GB", otroProducto);

        when(especificacionRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(especificacionRepository.save(any(EspecificacionTecnica.class))).thenAnswer(i -> i.getArgument(0));

        EspecificacionTecnica result = catalogoService.editarEspecificacion(1L, datos);

        assertEquals("RAM", result.getClave());
        assertEquals("32GB", result.getValor());
        assertEquals(otroProducto, result.getProducto());
    }

    @Test
    void eliminarEspecificacionReturnsTrueWhenExists() {
        when(especificacionRepository.existsById(1L)).thenReturn(true);

        assertTrue(catalogoService.eliminarEspecificacion(1L));
    }

    @Test
    void eliminarEspecificacionReturnsFalseWhenNotFound() {
        when(especificacionRepository.existsById(99L)).thenReturn(false);

        assertFalse(catalogoService.eliminarEspecificacion(99L));
    }

    @Test
    void agregarProducto_skipsCategoriaWhenCategoriaIdIsNull() {
        Producto nuevo = new Producto(null, "SKU-005", "Test", null, 10.0, new CategoriaProducto(null, "SinID"), null, null, null);
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto result = catalogoService.agregarProducto(nuevo);

        assertNull(result.getCategoria().getId());
    }

    @Test
    void agregarProducto_skipsEstadoWhenEstadoIdIsNull() {
        Producto nuevo = new Producto(null, "SKU-006", "Test", null, 10.0, null, new EstadoDisponibilidad(null, "SinID"), null, null);
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto result = catalogoService.agregarProducto(nuevo);

        assertNull(result.getEstado().getId());
    }

    @Test
    void agregarProducto_throwsWhenCategoriaNotFound() {
        Producto nuevo = new Producto(null, "SKU-007", "Test", null, 10.0, new CategoriaProducto(99L, null), null, null, null);
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> catalogoService.agregarProducto(nuevo));
    }

    @Test
    void agregarProducto_throwsWhenEstadoNotFound() {
        Producto nuevo = new Producto(null, "SKU-008", "Test", null, 10.0, null, new EstadoDisponibilidad(99L, null), null, null);
        when(estadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> catalogoService.agregarProducto(nuevo));
    }

    @Test
    void editarProducto_skipsCategoriaWhenCategoriaIdIsNull() {
        Producto nuevosDatos = new Producto(null, "SKU-009", "Test", null, 10.0, new CategoriaProducto(null, "SinID"), null, null, null);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto result = catalogoService.editarProducto(1L, nuevosDatos);

        assertEquals(categoria, result.getCategoria());
    }

    @Test
    void editarProducto_skipsEstadoWhenEstadoIdIsNull() {
        Producto nuevosDatos = new Producto(null, "SKU-010", "Test", null, 10.0, null, new EstadoDisponibilidad(null, "SinID"), null, null);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto result = catalogoService.editarProducto(1L, nuevosDatos);

        assertEquals(estado, result.getEstado());
    }

    @Test
    void editarProducto_throwsWhenCategoriaNotFound() {
        Producto nuevosDatos = new Producto(null, "SKU-011", "Test", null, 10.0, new CategoriaProducto(99L, null), null, null, null);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> catalogoService.editarProducto(1L, nuevosDatos));
    }

    @Test
    void editarProducto_throwsWhenEstadoNotFound() {
        Producto nuevosDatos = new Producto(null, "SKU-012", "Test", null, 10.0, null, new EstadoDisponibilidad(99L, null), null, null);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(estadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoExisteEnBdException.class, () -> catalogoService.editarProducto(1L, nuevosDatos));
    }

    @Test
    void agregarProductoWithoutCategoryAndEstado() {
        Producto nuevo = new Producto(null, "SKU-003", "Teclado", null, 80.0, null, null, null, null);
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto result = catalogoService.agregarProducto(nuevo);

        assertNull(result.getCategoria());
        assertNull(result.getEstado());
        assertNotNull(result.getFechaCreacion());
    }

    @Test
    void editarProductoWithoutCategoryAndEstadoUpdate() {
        Producto nuevosDatos = new Producto(null, "SKU-004", "Laptop Basic", "Basica", 1200.0, null, null, null, null);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto result = catalogoService.editarProducto(1L, nuevosDatos);

        assertEquals("SKU-004", result.getSku());
        assertEquals("Laptop Basic", result.getNombre());
        assertEquals(categoria, result.getCategoria());
        assertEquals(estado, result.getEstado());
    }

}
