# EcoMarket - Microservicio de Registro de Usuarios (usuarioservice)

Microservicio de gestión de usuarios del proyecto EcoMarket. Administra el registro, perfiles, roles, permisos y direcciones de los usuarios de la plataforma.

## Características Principales

- **Registro de Usuarios:** Creación de perfiles con validación de correo único y delegación de credenciales a iniciosesion-service.
- **CRUD de Usuarios:** Listar, buscar por ID/correo/rol, modificar y eliminar perfiles.
- **Gestión de Direcciones:** CRUD completo de direcciones de despacho con soporte de dirección predeterminada.
- **Roles y Permisos:** Catálogos editables de roles (ADMIN, CLIENTE, VENDEDOR) y permisos granulares con asignación many-to-many.
- **Estados de Perfil:** Catálogo de estados (ACTIVO, INACTIVO, SUSPENDIDO).
- **Auditoría:** Envío de logs de registro y eliminación a analitica-service.
- **Corrección Automática de Schema:** CommandLineRunner que elimina columnas huérfanas al iniciar.

## Tecnologías Utilizadas

| Componente | Versión |
|---|---|
| **Lenguaje** | Java 25 |
| **Framework** | Spring Boot 4.0.7 |
| **ORM** | Spring Data JPA + Hibernate |
| **Base de Datos** | MySQL 8.0 — `usuarios_db` |
| **Comunicación** | RestTemplate |
| **Build** | Maven |

## Estructura del Proyecto

```
usuarioservice/
├── pom.xml
├── src/main/java/com/horacio/ecomarket/usuarios/
│   ├── UsuariosServiceApplication.java
│   ├── config/ (DatabaseSchemaFixer, RestTemplateConfig)
│   ├── controller/ (CatalogoController, DireccionController, UsuarioController)
│   ├── dto/ (ConfigurarPermisosDTO, ErrorResponseDTO, ModificarUsuarioDTO, RegistroUsuarioDTO)
│   ├── exception/ (CorreoDuplicadoException, GlobalExceptionHandler, RecursoNoEncontradoException)
│   ├── model/ (Direccion, EstadoPerfil, PerfilUsuario, Permiso, Rol)
│   ├── repository/ (5 repositorios JPA)
│   └── service/ (DireccionService, RegistroUsuarioService)
└── src/test/java/com/horacio/ecomarket/usuarios/
    ├── UsuariosServiceApplicationTests.java
    ├── service/ (RegistroUsuarioServiceTest, DireccionServiceTest)
    ├── controller/ (UsuarioControllerTest, DireccionControllerTest, CatalogoControllerTest)
    ├── exception/ (GlobalExceptionHandlerTest, RecursoNoEncontradoExceptionTest)
    ├── dto/ (4 tests de DTOs)
    └── config/ (RestTemplateConfigTest, DatabaseSchemaFixerTest)
```

## API Endpoints

### Usuarios - `/api/usuarios`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/usuarios/registro` | Registrar nuevo usuario |
| GET | `/api/usuarios` | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Buscar usuario por ID |
| GET | `/api/usuarios/correo/{correo}` | Buscar por correo |
| GET | `/api/usuarios/rol/{rolId}` | Listar por rol |
| PUT | `/api/usuarios/{id}` | Modificar datos de usuario |
| PUT | `/api/usuarios/{id}/permisos` | Configurar permisos |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

### Direcciones - `/api/usuarios/direcciones`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/usuarios/direcciones/{usuarioId}` | Agregar dirección |
| GET | `/api/usuarios/direcciones/{usuarioId}` | Listar direcciones del usuario |
| GET | `/api/usuarios/direcciones/id/{id}` | Obtener dirección por ID |
| GET | `/api/usuarios/direcciones/predeterminada/{usuarioId}` | Obtener dirección predeterminada |
| PUT | `/api/usuarios/direcciones/{id}` | Editar dirección |
| DELETE | `/api/usuarios/direcciones/{id}` | Eliminar dirección |

### Catálogos - `/api/usuarios`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET/POST/PUT/DELETE | `/api/usuarios/roles` | CRUD roles |
| GET/POST/PUT/DELETE | `/api/usuarios/permisos` | CRUD permisos |
| GET/POST/PUT/DELETE | `/api/usuarios/estados-perfil` | CRUD estados de perfil |

## Configuración de Base de Datos

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | URL JDBC | `jdbc:mysql://localhost:3306/usuarios_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Password BD | |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia DDL | `update` |

## Dependencias entre Servicios

| Servicio | Puerto | Propósito |
|----------|--------|-----------|
| iniciosesion | 8086 | Crear credenciales (correo+contraseña) al registrar usuario |
| analitica | 8084 | Registrar logs de auditoría (registro/eliminación) |

## Notas Importantes

- El correo electrónico es único a nivel de base de datos y aplicación.
- Al registrar, se delega la creación de credenciales a iniciosesion-service vía RestTemplate.
- Si iniciosesion-service falla, el registro completo se revierte con excepción.
- La auditoría a analitica-service es best-effort (solo log de advertencia si falla).
- El `DatabaseSchemaFixer` elimina automáticamente columnas huérfanas (password) al iniciar.

## Pruebas

```bash
mvn test
```

**101 tests** distribuidos en 14 clases de prueba, cubriendo servicios, controladores, DTOs, excepciones y configuración.
