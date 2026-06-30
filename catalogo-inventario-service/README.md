# EcoMarket - Microservicio de Catálogo e Inventario (catalogoinventarioservice)

Microservicio de gestión de productos, catálogo e inventario del proyecto EcoMarket. Administra el ciclo de vida completo de productos, categorías, especificaciones técnicas, stock global y stock por sucursal.

## Características Principales

- **Catálogo de Productos:** CRUD completo, búsqueda por nombre, filtrado por categoría.
- **Categorías de Producto:** Catálogo editable para clasificar productos.
- **Especificaciones Técnicas:** Gestión de atributos y características por producto.
- **Estados de Disponibilidad:** Catálogo de estados (DISPONIBLE, AGOTADO, DESCONTINUADO, etc.).
- **Inventario Global:** Gestión de stock centralizado (bodega general).
- **Inventario por Sucursal:** Stock distribuido por sucursal con transferencias desde el stock global.
- **Reserva y Liberación de Stock:** Mecanismo para reservar stock durante el checkout y liberarlo si la compra no se completa.
- **Alertas de Stock Bajo:** Notificación a gestion-tienda-service cuando el stock cae bajo el mínimo.
- **Validación de Sucursales:** Consulta a gestion-tienda-service para verificar existencia de sucursales.

## Tecnologías Utilizadas

| Componente | Versión |
|---|---|
| **Lenguaje** | Java 25 |
| **Framework** | Spring Boot 4.0.7 |
| **ORM** | Spring Data JPA + Hibernate |
| **Base de Datos** | MySQL 8.0 — `catalogo_db` |
| **Comunicación** | RestTemplate |
| **Build** | Maven |

## Estructura del Proyecto

```
catalogoinventarioservice/
├── pom.xml
├── src/main/java/com/ecomarket/catalogoinventarioservice/
│   ├── CatalogoinventarioserviceApplication.java
│   ├── client/GestionTiendaClient.java
│   ├── config/RestTemplateConfig.java
│   ├── controller/
│   │   ├── CatalogoAdminController.java
│   │   ├── InventarioController.java
│   │   └── ProductoController.java
│   ├── dto/ (CantidadDTO, CantidadGlobalDTO, ErrorResponseDTO, MensajeDTO,
│   │        ProductoRequestDTO, SucursalDTO)
│   ├── exception/ (GlobalExceptionHandler, NoExisteEnBdException, YaExisteEnBdException)
│   ├── model/ (CategoriaProducto, EspecificacionTecnica, EstadoDisponibilidad,
│   │           InventarioStock, Producto, StockGlobal)
│   ├── repository/ (6 repositorios JPA)
│   └── service/ (CatalogoService, InventarioService)
└── src/test/java/com/ecomarket/catalogoinventarioservice/
    ├── CatalogoinventarioserviceApplicationTests.java
    ├── client/GestionTiendaClientTest.java
    ├── config/RestTemplateConfigTest.java
    ├── controller/ (CatalogoAdminControllerTest, InventarioControllerTest, ProductoControllerTest)
    ├── dto/ (6 tests de DTOs)
    ├── exception/GlobalExceptionHandlerTest.java
    ├── model/ (InventarioStockTest, ProductoTest, StockGlobalTest)
    └── service/ (CatalogoServiceTest, InventarioServiceTest)
```

## API Endpoints

### Productos - `/api/catalogo`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/catalogo` | Listar todos los productos |
| GET | `/api/catalogo/categoria/{categoriaId}` | Filtrar por categoría |
| GET | `/api/catalogo/buscar?nombre=` | Buscar por nombre |
| GET | `/api/catalogo/{id}` | Obtener producto por ID |
| POST | `/api/catalogo` | Crear nuevo producto |
| PUT | `/api/catalogo/{id}` | Editar producto |
| DELETE | `/api/catalogo/{id}` | Eliminar producto |

### Inventario - `/api/inventario`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/inventario` | Listar todo el inventario |
| GET | `/api/inventario/disponibilidad/{productoId}/{sucursalId}/{cantidad}` | Verificar disponibilidad |
| GET | `/api/inventario/stock-global/{productoId}` | Stock global de un producto |
| GET | `/api/inventario/global/{productoId}` | Inventario global por producto |
| GET | `/api/inventario/sucursal/{sucursalId}/producto/{productoId}` | Stock en sucursal específica |
| POST | `/api/inventario/ingresar` | Ingresar stock global |
| POST | `/api/inventario/transferir` | Transferir stock a sucursal |
| POST | `/api/inventario/reservar/{productoId}/{sucursalId}` | Reservar stock |
| POST | `/api/inventario/liberar/{productoId}/{sucursalId}` | Liberar stock reservado |
| PUT | `/api/inventario/ajustar/{productoId}/sucursal/{sucursalId}` | Ajustar stock |

### Administración de Catálogo - `/api/catalogo-admin`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET/POST/PUT/DELETE | `/api/catalogo-admin/categoria` | CRUD categorías de producto |
| GET/POST/PUT/DELETE | `/api/catalogo-admin/estado` | CRUD estados de disponibilidad |
| GET/POST/PUT/DELETE | `/api/catalogo-admin/especificaciones` | CRUD especificaciones técnicas |

## Configuración de Base de Datos

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | URL JDBC | `jdbc:mysql://localhost:3306/catalogo_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Password BD | |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia DDL | `update` |

## Dependencias entre Servicios

| Servicio | Puerto | Propósito |
|----------|--------|-----------|
| gestion-tienda | 8090 | Validar sucursales y notificar stock bajo |

## Notas Importantes

- Los estados de disponibilidad predefinidos incluyen: DISPONIBLE, AGOTADO, DESCONTINUADO, PROXIMAMENTE.
- El stock global es el inventario centralizado; las sucursales reciben stock mediante transferencias.
- La reserva de stock disminuye el disponible y aumenta el reservado; la liberación hace la operación inversa.
- Al detectar stock bajo, se notifica automáticamente a gestion-tienda-service.
- Los endpoints de administración (`/api/catalogo-admin`) requieren rol ADMIN.
- Datos de prueba disponibles en `test-data/` (100 productos, 10 categorías, 4 estados).

## Pruebas

```bash
mvn test
```

**136 tests** distribuidos en 18 clases de prueba, cubriendo servicios (catálogo e inventario), controladores (3), clientes REST, DTOs (6), modelos de dominio y excepciones.
