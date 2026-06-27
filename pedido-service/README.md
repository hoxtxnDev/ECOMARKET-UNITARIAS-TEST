# EcoMarket - Microservicio de Pedidos (pedidoservice)

Microservicio de gestión de pedidos del proyecto EcoMarket. Convierte carritos de compra en pedidos formales, administra estados del ciclo de vida del pedido y coordina con otros servicios para pagos, envíos y analíticas.

## Características Principales

- **Generación de Pedidos:** Crea pedidos desde un carrito cerrado, validando usuario, productos y dirección de despacho.
- **Gestión de Estados:** Actualización de estados por ID numérico o por nombre (ej. `ENVIADO` → dispara creación automática de envío).
- **Historial por Cliente:** Consulta de todos los pedidos de un cliente específico.
- **Catálogo de Estados:** CRUD completo de estados de pedido (PENDIENTE, CONFIRMADO, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO).
- **Integración Multi-servicio:** Se comunica con registro-usuarios, carrito-compra, catalogo-inventario, logistica-envios y analitica.
- **Disparo Automático de Envío:** Al cambiar el estado a `ENVIADO`, crea automáticamente un envío en logistica-envios-service.
- **Documentación API:** Swagger UI disponible en `/doc/swagger-ui.html`.

## Tecnologías Utilizadas

| Componente | Versión |
|---|---|
| **Lenguaje** | Java 25 |
| **Framework** | Spring Boot 4.0.7 |
| **ORM** | Spring Data JPA + Hibernate |
| **Base de Datos (prod)** | MySQL 8.0 — `pedidos_db` |
| **Base de Datos (test)** | H2 en memoria |
| **Documentación API** | SpringDoc OpenAPI 2.6.0 |
| **Build** | Maven |

## Estructura del Proyecto

```
pedidoservice/
├── pom.xml
├── src/main/java/com/ecomarket/pedidos/
│   ├── PedidoServiceApplication.java
│   ├── client/
│   │   ├── AnaliticaClient.java
│   │   ├── CarritoCompraClient.java
│   │   ├── CatalogoInventarioClient.java
│   │   └── RegistroUsuariosClient.java
│   ├── config/RestTemplateConfig.java
│   ├── controller/
│   │   ├── EstadoPedidoController.java
│   │   └── PedidoController.java
│   ├── dto/ (CarritoDTO, EmpleadoDTO, ErrorResponseDTO, ItemCarritoDTO,
│   │        PerfilUsuarioDTO, ProductoDTO, RolDTO)
│   ├── exception/ (GlobalExceptionHandler, NoExisteEnBdException, YaExisteEnBdException)
│   ├── model/ (EstadoPedido, ItemPedido, Pedido)
│   ├── repository/ (EstadoPedidoRepository, ItemPedidoRepository, PedidoRepository)
│   └── service/PedidoService.java
└── src/test/java/com/ecomarket/pedidos/
    ├── PedidoServiceApplicationTests.java
    ├── client/ (4 tests de clientes REST)
    ├── config/RestTemplateConfigTest.java
    ├── controller/ (PedidoControllerTest, EstadoPedidoControllerTest)
    ├── dto/ (6 tests de DTOs)
    ├── exception/GlobalExceptionHandlerTest.java
    └── service/PedidoServiceTest.java
```

## API Endpoints

### Pedidos - `/api/pedidos`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/pedidos/generar/{clienteId}/{carritoId}?direccionEnvioId=` | Genera pedido desde carrito cerrado |
| PUT | `/api/pedidos/{pedidoId}/estado/{estadoId}` | Actualiza estado por ID |
| PUT | `/api/pedidos/{pedidoId}/estado-nombre` | Actualiza estado por nombre (body: texto plano) |
| GET | `/api/pedidos/cliente/{clienteId}` | Historial de pedidos del cliente |
| GET | `/api/pedidos/{pedidoId}` | Obtiene pedido por ID |

### Estados de Pedido - `/api/estado-pedido`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/estado-pedido` | Lista todos los estados |
| GET | `/api/estado-pedido/{id}` | Obtiene estado por ID |
| POST | `/api/estado-pedido` | Crea nuevo estado |
| PUT | `/api/estado-pedido/{id}` | Actualiza estado |
| DELETE | `/api/estado-pedido/{id}` | Elimina estado |

## Configuración de Base de Datos

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | URL JDBC | `jdbc:mysql://localhost:3306/pedidos_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Password BD | |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia DDL | `update` |

## Dependencias entre Servicios

| Servicio | Puerto | Propósito |
|----------|--------|-----------|
| registro-usuarios | 8081 | Validar usuario y obtener dirección predeterminada |
| carrito-compra | 8082 | Obtener carrito, vaciarlo y cerrarlo |
| catalogo-inventario | 8087 | Validar existencia de productos |
| logistica-envios | 8083 | Crear envío automático al pasar a ENVIADO |
| analitica | 8084 | Registrar log de generación de pedido |

## Notas Importantes

- Los IDs de estado de pedido predefinidos: 1=PENDIENTE, 2=CONFIRMADO, 3=EN_PREPARACION, 4=ENVIADO, 5=ENTREGADO, 6=CANCELADO.
- Al actualizar a estado ENVIADO (ID 4 o nombre "ENVIADO"), se crea automáticamente un envío en logistica-envios-service.
- La dirección de envío se obtiene de registro-usuarios si no se especifica `direccionEnvioId`.
- Swagger UI disponible en `http://localhost:8089/doc/swagger-ui.html`.

## Pruebas

```bash
mvn test
```

**67 tests** distribuidos en 17 clases de prueba, cubriendo servicio, controladores, clientes REST, DTOs, excepciones y configuración.
