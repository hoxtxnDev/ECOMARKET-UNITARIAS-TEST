# EcoMarket - Microservicio de Gestión de Tienda (gestiontiendaservice)

Microservicio de administración de tiendas y sucursales del proyecto EcoMarket. Gestiona sucursales, horarios de atención, reglamentos internos, tareas del personal, permisos POS y comunicación con empleados.

## Características Principales

- **Gestión de Sucursales:** Registro, consulta, listado de sucursales activas y asignación de gerentes.
- **Validación de Empleados:** Verifica roles (GERENTE, EMPLEADO) contra registro-usuarios-service.
- **Gestión de Tareas:** Asignación de tareas a empleados con seguimiento de estado.
- **Catálogo de Estados de Tarea:** CRUD completo de estados personalizables.
- **Permisos POS:** Configuración granular de acciones POS (anulaciones, apertura de caja, descuentos manuales).
- **Reglamentos Internos:** Almacenamiento versionado de contenido legal/regulatorio por sucursal.
- **Horarios de Atención:** Gestión de horarios semanales por sucursal con soporte de festivos.
- **Comunicación Inter-servicios:** Se conecta con registro-usuarios-service y proceso-pago-service mediante RestClient.

## Tecnologías Utilizadas

| Componente | Versión |
|---|---|
| **Lenguaje** | Java 25 |
| **Framework** | Spring Boot 4.0.7 |
| **ORM** | Spring Data JPA + Hibernate |
| **Base de Datos** | MySQL 8.0 — `tienda_db` |
| **Comunicación** | RestTemplate |
| **Build** | Maven |

## Estructura del Proyecto

```
gestiontiendaservice/
├── pom.xml
├── src/main/java/com/ecomarket/gestiontiendaservice/
│   ├── GestiontiendaserviceApplication.java
│   ├── client/ (EmpleadoDTO, ProcesoPagoClient, RegistroUsuariosClient, RolDTO, TransaccionResumenDTO)
│   ├── config/RestTemplateConfig.java
│   ├── controller/GestionTiendaController.java
│   ├── dto/ (ErrorResponseDTO, EstadoRequestDTO, GerenteRequestDTO, SucursalRequestDTO)
│   ├── exception/ (GlobalExceptionHandler, NoExisteEnBdException, YaExisteEnBdException)
│   ├── model/ (EstadoTareaPersonal, HorarioAtencion, PermisoPOS,
│   │           ReglamentoInterno, Sucursal, TareaPersonal)
│   ├── repository/ (6 repositorios JPA)
│   └── service/GestionTiendaService.java
└── src/test/java/com/ecomarket/gestiontiendaservice/
    ├── GestiontiendaserviceApplicationTests.java
    ├── client/ (5 tests de clientes y DTOs)
    ├── config/RestTemplateConfigTest.java
    ├── controller/GestionTiendaControllerTest.java
    ├── dto/ (4 tests de DTOs)
    ├── exception/GlobalExceptionHandlerTest.java
    └── service/GestionTiendaServiceTest.java
```

## API Endpoints

Base path: `/api/tienda`

### Sucursales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/tienda/sucursal` | Registrar nueva sucursal |
| GET | `/api/tienda/sucursal/{sucursalId}` | Obtener sucursal por ID |
| GET | `/api/tienda/sucursales/activas` | Listar sucursales activas |
| PUT | `/api/tienda/sucursal/{sucursalId}/gerente` | Asignar gerente a sucursal |
| PUT | `/api/tienda/sucursal/{sucursalId}/horarios` | Configurar horarios |
| GET | `/api/tienda/sucursal/{sucursalId}/horarios` | Obtener horarios |

### Tareas y Personal

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/tienda/tarea` | Asignar tarea a empleado |
| PATCH | `/api/tienda/tareas/{tareaId}/estado` | Actualizar estado de tarea |

### Catálogos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET/POST/PUT/DELETE | `/api/tienda/estados-tarea` | CRUD estados de tarea |
| POST | `/api/tienda/permisos-pos` | Configurar permisos POS |
| POST | `/api/tienda/sucursal/{id}/reglamento` | Establecer reglamento interno |

## Configuración de Base de Datos

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | URL JDBC | `jdbc:mysql://localhost:3306/tienda_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Password BD | |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia DDL | `update` |

## Dependencias entre Servicios

| Servicio | Puerto | Propósito |
|----------|--------|-----------|
| registro-usuarios | 8081 | Validar empleados y roles (GERENTE, EMPLEADO) |
| proceso-pago | 8085 | Consultar transacciones de clientes |

## Notas Importantes

- Los estados de tarea predefinidos incluyen: PENDIENTE, EN_PROGRESO, COMPLETADA.
- Al asignar un gerente se valida que el usuario exista y tenga rol GERENTE en registro-usuarios-service.
- Los horarios de atención soportan configuración por día de semana y flag de festivo.
- Los permisos POS permiten habilitar/deshabilitar acciones sensibles por rol de empleado.

## Pruebas

```bash
mvn test
```

**82 tests** distribuidos en 14 clases de prueba, cubriendo servicio, controlador, clientes REST, DTOs, excepciones y configuración.
