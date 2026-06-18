<p align="center">
  <h1 align="center">ECOMARKET</h1>
  <p align="center">Plataforma de Comercio Electrónico · Arquitectura de Microservicios</p>
  <p align="center">
    <img src="https://img.shields.io/badge/Spring_Boot-3.4.1_%2F_4.0.6-6DB33F?logo=springboot" alt="Spring Boot">
    <img src="https://img.shields.io/badge/Java-21_%2F_25-ED8B00?logo=openjdk" alt="Java">
    <img src="https://img.shields.io/badge/Spring_Cloud_Gateway-2024.0.0-6DB33F" alt="Spring Cloud">
    <img src="https://img.shields.io/badge/coverage-100%25-brightgreen" alt="Coverage 100%">
    <img src="https://img.shields.io/badge/MySQL-8+-4479A1?logo=mysql" alt="MySQL">
    <img src="https://img.shields.io/badge/JWT-HS256-000000?logo=jsonwebtokens" alt="JWT">
  </p>
</p>

<details open>
  <summary>Índice</summary>

- [Reparto de Trabajo](#reparto-de-trabajo)
- [Arquitectura](#arquitectura)
- [Servicios](#servicios)
- [Stack Tecnológico](#stack-tecnológico)
- [Comunicación entre Servicios](#comunicación-entre-servicios)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Flujo de Autenticación](#flujo-de-autenticación)
- [Estrategia de Pruebas](#estrategia-de-pruebas)
- [Cómo Ejecutar](#cómo-ejecutar)
- [Notas Técnicas](#notas-técnicas)

</details>

---

## Reparto de Trabajo

El desarrollo se distribuyó entre dos integrantes:

| Integrante | Servicios |
|---|---|
| **Hans** | `analica-service` · `logistica-envios-service` · `soporte-service` · `proceso-pago-service` · `pedido-service` |
| **Horacio** | `registro-usuarios-service` (unificado con auth) · `gestion-tienda-service` · `carrito-compra-service` |
| *(ambos)* | `api-gateway` · `catalogo-inventario-service` |

---

## Arquitectura

### Diagrama General

```
                         ┌─────────────────────┐
                         │   api-gateway:8080    │
                         │ Spring Cloud Gateway  │
                         │ JwtAuthenticationFilter│
                         └──┬───┬───┬───┬───┬───┘
                            │   │   │   │   │
       ┌────────────────────┘   │   │   │   └──────────┐
       │          ┌─────────────┘   └──────────┐       │
       ▼          ▼                             ▼       ▼
  registro:8081 carrito:8082              catalogo:8087 ...
  (JWT-filter)  (JWT-filter)              (JWT-filter)
       │          │                             │
       ▼          ▼                             ▼
  MySQL/usr_db MySQL/carrito_db          MySQL/catalogo_db
```

### Patrón

| Aspecto | Descripción |
|---|---|
| **Entrada única** | `api-gateway:8080` con Spring Cloud Gateway enruta todas las peticiones y filtra JWT |
| **Sin discovery** | Las rutas son estáticas; los servicios se localizan por URL fija |
| **Sin config server** | Cada servicio tiene su propio `application.properties` |
| **BBDD por servicio** | Cada microservicio gestiona su propio schema MySQL (schema-per-service) |
| **Excepción** | `pedido-service` usa H2 en memoria como base de datos primaria |
| **Inter-servicios** | Comunicación vía `RestTemplate` (principal) y `RestClient` (alternativa) |

---

## Servicios

| # | Servicio | Puerto | Propósito | Dueño |
|---|----------|--------|-----------|-------|
| 1 | `api-gateway` | `8080` | Punto de entrada único; enruta requests y valida JWT | — |
| 2 | `registro-usuarios-service` | `8081` | Registro de usuarios, perfiles, roles y permisos | Horacio |
| 3 | `carrito-compra-service` | `8082` | Carrito de compras y orquestación de checkout | Horacio |
| 4 | `logistica-envios-service` | `8083` | Logística de envíos, rutas, puntos de retiro, seguimiento | Hans |
| 5 | `analica-service` | `8084` | Analíticas, reportes, métricas, respaldos, alertas | Hans |
| 6 | `proceso-pago-service` | `8085` | Procesamiento de pagos, transacciones, facturas, cupones | Hans |
| 7 | `iniciosesion-service` | `8086` | Autenticación, login, gestión de tokens JWT, recuperación de contraseña | Horacio |
| 8 | `catalogo-inventario-service` | `8087` | Catálogo de productos, gestión de stock e inventario | — |
| 9 | `soporte-service` | `8088` | Tickets de soporte, chat en vivo, notificaciones, reseñas | Hans |
| 10 | `pedido-service` | `8089` | Gestión de pedidos: generación, estados, historial | Hans |
| 11 | `gestion-tienda-service` | `8090` | Gestión de tiendas, sucursales, tareas de personal, horarios, normativas | Horacio |

### Bases de Datos

| Servicio | Base de Datos MySQL | Schema |
|---|---|---|
| `registro-usuarios` | `usuarios_db` | `com.horacio.ecomarket.usuarios` |
| `carrito-compra` | `ecomarket_carrito` | `com.ecomarket.carritocompraservice` |
| `logistica-envios` | `envio_db` | `com.ecomarket.envioservice` |
| `analica` | `analitica_db` | `com.ecomarket.analicaservice` |
| `proceso-pago` | `proceso_pago_db` | `com.ecomarket.procesopagoservice` |
| `iniciosesion` | `iniciosesion_db` | `com.ecomarket.iniciosesion` |
| `catalogo-inventario` | `catalogo_db` | `com.ecomarket.catalogoinventarioservice` |
| `soporte` | `soporte_db` | `com.ecomarket.soporteservice` |
| `gestion-tienda` | `tienda_db` | `com.ecomarket.gestiontiendaservice` |
| `pedido` | H2 en memoria (`pedidosdb`) | `com.ecomarket.pedidos` |

---

## Stack Tecnológico

### Dependencias por Categoría

| Categoría | Dependencia | ¿Por qué? |
|---|---|---|
| **Web (Servlet)** | `spring-boot-starter-web` / `spring-boot-starter-webmvc` | Servidor HTTP embebido (Tomcat 11) para exponer las APIs REST de cada microservicio. La variante `webmvc` se usa donde no se necesita autoconfiguración completa de web. |
| **Web (Reactivo)** | `spring-boot-starter-webflux` | Solo en `api-gateway`. Spring Cloud Gateway opera sobre el modelo reactivo (Netty), incompatible con el stack Servlet. |
| **Gateway** | `spring-cloud-starter-gateway` | Enrutamiento declarativo de peticiones, filtros y balanceo de carga básico desde el punto de entrada único. |
| **Persistencia** | `spring-boot-starter-data-jpa` | Spring Data JPA + Hibernate 7 como ORM para el mapeo objeto-relacional y operaciones CRUD contra MySQL. |
| **Driver MySQL** | `mysql-connector-j` (runtime) | Conector JDBC oficial de MySQL. Cada servicio apunta a su propia base de datos. |
| **H2 (tests)** | `h2` (test scope) | Base de datos en memoria que reemplaza a MySQL durante las pruebas, eliminando dependencias externas y acelerando la ejecución. |
| **Validación** | `spring-boot-starter-validation` | Validación declarativa de DTOs con Jakarta Validation (`@NotBlank`, `@Email`, `@Size`, etc.). |
| **Seguridad** | `spring-boot-starter-security` | Configuración de seguridad HTTP, cadenas de filtros y endpoints públicos vs. autenticados. |
| **JWT** | `jjwt-api` / `jjwt-impl` / `jjwt-jackson` 0.12.5 | Creación, firma (HMAC-SHA256) y validación de tokens JWT. Usado en `iniciosesion-service` para emitir tokens y en servicios que necesitan validarlos. |
| **Lombok** | `lombok` (optional) | Elimina código boilerplate: `@Data`, `@Builder`, `@Slf4j`, `@RequiredArgsConstructor`, etc. |
| **Jackson** | `jackson-databind` | Serialización y deserialización JSON. Se declara explícitamente en `iniciosesion-service`. |
| **API Docs** | `springdoc-openapi-starter-webmvc-ui` 2.6.0 | Documentación Swagger/OpenAPI interactiva. Solo presente en `pedido-service`. |
| **Pruebas** | `spring-boot-starter-test` | JUnit 5, Mockito, AssertJ y Spring Test. Base de toda la pirámide de pruebas. |
| **Pruebas Web** | `spring-boot-starter-webmvc-test` | MockMvc para pruebas de controladores con `@WebMvcTest` sin levantar el servidor real. |
| **Pruebas JPA** | `spring-boot-starter-data-jpa-test` | Soporte para `@DataJpaTest` y segmentación de tests de repositorios. |
| **Pruebas Validación** | `spring-boot-starter-validation-test` | Utilitarios para pruebas de validación de beans. |
| **Cobertura** | `jacoco-maven-plugin` 0.8.14 | Generación de reportes de cobertura de código (instrucciones, ramas, líneas, complejidad). |
| **Compilación** | `maven-compiler-plugin` | Procesador de anotaciones de Lombok durante la compilación. |

### Versiones

| Componente | api-gateway | Demás servicios |
|---|---|---|
| **Spring Boot** | 3.4.1 | 4.0.6 |
| **Java** | 21 | 25 |
| **Spring Cloud** | 2024.0.0 | — |

> `api-gateway` usa Spring Boot 3.4.1+Java 21 porque Spring Cloud Gateway 2024.0.0 requiere una base estable. El resto de servicios utiliza Spring Boot 4.0.6 con Java 25.

---

## Comunicación entre Servicios

### RestTemplate (mecanismo principal)

Configuración homogénea con `JdkClientHttpRequestFactory` y timeout de 10 segundos:

```java
@Bean
RestTemplate restTemplate() {
    var factory = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();
    var requestFactory = new JdkClientHttpRequestFactory(factory);
    requestFactory.setReadTimeout(Duration.ofSeconds(10));
    return new RestTemplate(requestFactory);
}
```

### RestClient (alternativa)

`gestion-tienda-service` utiliza `RestClient` (sucesor moderno de `RestTemplate`) para comunicarse con `registro-usuarios-service` y `proceso-pago-service`, con manejo de errores mediante `.onStatus()`.

### Mapa de Comunicación

| Desde | Hacia | Medio | Propósito |
|---|---|---|---|
| `api-gateway:8080` | `iniciosesion:8086` | `RestTemplate` | Validar token JWT |
| `carrito-compra:8082` | `catalogo-inventario:8087` | `RestTemplate` | Verificar stock de productos |
| `carrito-compra:8082` | `proceso-pago:8085` | `RestTemplate` | Iniciar pago |
| `carrito-compra:8082` | `logistica-envios:8083` | `RestTemplate` | Crear envío |
| `catalogo-inventario:8087` | `gestion-tienda:8090` | `RestTemplate` | Notificar stock bajo |
| `logistica-envios:8083` | `soporte:8088` | `RestTemplate` | Enviar notificaciones push |
| `logistica-envios:8083` | `analica:8084` | `RestTemplate` | Registrar métricas de envío |
| `soporte:8088` | `analica:8084` | `RestTemplate` | Registrar métricas de soporte |
| `iniciosesion:8086` | `analica:8084` | `RestTemplate` | Registrar eventos de login |
| `gestion-tienda:8090` | `registro-usuarios:8081` | `RestClient` | Consultar empleados |
| `gestion-tienda:8090` | `proceso-pago:8085` | `RestClient` | Consultar transacciones |

---

## Estructura del Proyecto

Todos los servicios siguen una arquitectura en capas homogénea:

```
com.ecomarket.<servicio>/
├── <Servicio>Application.java       # @SpringBootApplication
├── controller/
│   └── *Controller.java             # @RestController — endpoints REST
├── service/
│   ├── *Service.java                # Interfaz (opcional)
│   ├── *ServiceImpl.java            # @Service — lógica de negocio
│   └── *Util.java                   # Utilitarios (JwtUtil, etc.)
├── repository/
│   └── *Repository.java             # Spring Data JPA
├── model/
│   └── *.java                       # @Entity — entidades JPA
├── dto/
│   ├── *Request.java                # DTOs de entrada
│   ├── *Response.java               # DTOs de salida
│   └── ErrorResponseDTO.java        # Error estándar
├── exception/
│   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   └── *Exception.java              # Excepciones personalizadas
├── config/
│   └── *Config.java                 # Beans (RestTemplate, etc.)
└── client/
    └── *Client.java                 # Clientes HTTP a otros servicios
```

> `api-gateway` es la excepción: no tiene capa de persistencia ni modelo. Su estructura es `filter/JwtAuthenticationFilter.java` y `config/RouteConfiguration.java`.

---

## Flujo de Autenticación

```
CLIENTE                    API GATEWAY                  INICIOSESION-SERVICE
  │                            │                              │
  │  POST /api/sesion/login    │                              │
  │ ─────────────────────────► │  POST /api/sesion/login      │
  │                            │ ──────────────────────────►  │
  │                            │                              │─► Validar credenciales
  │                            │                              │─► Generar JWT
  │                            │ ◄──────────────────────────  │
  │ ◄───────────────────────── │  { token, usuarioId, rol }   │
  │                            │                              │
  │  GET /api/** (con JWT)     │                              │
  │ ─────────────────────────► │                              │
  │                            │─► JwtAuthenticationFilter   │
  │                            │   ─► POST /api/sesion/validar│
  │                            │     ───► iniciosesion       │
  │                            │   ◄── { valido: true }       │
  │                            │─► Enrutar al servicio destino│
  │ ◄───────────────────────── │                              │
```

| Tipo de Ruta | Endpoints |
|---|---|
| **Públicas** (sin JWT) | `POST /api/sesion/login`, `POST /api/sesion/credencial` |
| **Protegidas** (JWT requerido) | Todas las demás |

---

## Estrategia de Pruebas

### Frameworks

| Herramienta | Propósito |
|---|---|
| **JUnit 5** | Motor de pruebas; `@Nested` para agrupar por método, `@DisplayName` en español |
| **Mockito** | Simulación de dependencias (`@Mock`, `@InjectMocks`, `@MockitoBean`) |
| **AssertJ** | Aserciones fluidas y legibles (`assertThat(...).isEqualTo(...)`) |
| **MockMvc** | Pruebas de controladores HTTP sin levantar el servidor |
| **H2** | Base de datos en memoria que reemplaza MySQL en tests |
| **JaCoCo 0.8.14** | Medición de cobertura de código (instrucciones, ramas, líneas, complejidad) |

### Tipos de Prueba

| Tipo | Anotaciones | ¿Qué cubre? |
|---|---|---|
| **Unitarias (Service)** | `@ExtendWith(MockitoExtension.class)` + `@Mock` + `@InjectMocks` | Lógica de negocio con todas las dependencias mockeadas |
| **Unitarias (Model/DTO)** | JUnit 5 plano | Constructores, métodos de dominio, validaciones |
| **Web (Controller)** | `@WebMvcTest` + `@AutoConfigureMockMvc(addFilters=false)` + `@ActiveProfiles("test")` | Endpoints REST, validación de request/response, códigos HTTP |
| **Contexto (Integración)** | `@SpringBootTest` + `@ActiveProfiles("test")` | Carga del contexto Spring y configuración general |
| **Repositorio** | `@SpringBootTest` | Operaciones JPA contra H2 |

### Patrón de Prueba de Servicio

```java
@ExtendWith(MockitoExtension.class)
class ServicioImplTest {

    @Mock
    private Repositorio repositorio;

    @InjectMocks
    private ServicioImpl servicio;

    @Nested
    @DisplayName("nombreDelMetodo")
    class NombreDelMetodo {

        @Test
        @DisplayName("descripción del caso")
        void casoDePrueba() {
            when(repositorio.buscarPorId(1L))
                .thenReturn(Optional.of(entidadEsperada));

            var resultado = servicio.ejecutarMetodo(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getCampo()).isEqualTo("valor esperado");
        }
    }
}
```

### Patrón de Prueba de Controlador

```java
@WebMvcTest(Controlador.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Servicio servicio;

    @Test
    @DisplayName("200 OK al ejecutar acción exitosa")
    void casoExitoso() throws Exception {
        when(servicio.ejecutar(any())).thenReturn(respuestaEsperada);

        mockMvc.perform(post("/api/recurso")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campo").value("valor"));
    }
}
```

### Cobertura

Todos los servicios alcanzan **100% de cobertura** en instrucciones, ramas, líneas, complejidad y métodos, verificado por JaCoCo. El reporte HTML se genera en `target/site/jacoco/index.html` tras ejecutar:

```bash
mvn clean test
```

Los tests utilizan un perfil `test` (`application-test.properties`) que configura H2 en memoria con `ddl-auto=create-drop`, eliminando cualquier dependencia de MySQL durante la ejecución de pruebas.

**Resumen de pruebas por servicio:**

| Servicio | Tests |
|---|---|---|
| `api-gateway` | 11 |
| `analica-service` | 112 |
| `carrito-compra-service` | 75 |
| `catalogo-inventario-service` | 106 |
| `gestion-tienda-service` | 45 |
| `iniciosesion-service` | 90 |
| `pedido-service` | 28 |
| `proceso-pago-service` | 44 |
| `logistica-envios-service` | 133 |
| `registro-usuarios-service` | 140 |
| `soporte-service` | 192 |
| **Total** | **976** |

---

## Cómo Ejecutar

### Requisitos

- JDK 21 (`api-gateway`) y JDK 25+ (demás servicios)
- Apache Maven 3.9+
- MySQL 8+ (solo para ejecución completa, no necesario para tests)

### Tests

Cada servicio es un proyecto Maven independiente:

```bash
cd <servicio>
mvn clean test
```

El reporte de cobertura JaCoCo se genera automáticamente en `target/site/jacoco/index.html`.

### Servicio individual

```bash
cd <servicio>
mvn spring-boot:run
```

Requiere MySQL en `localhost:3306` con la base de datos correspondiente creada. Hibernate creará las tablas automáticamente con `spring.jpa.hibernate.ddl-auto=update`.

### Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

Requiere `iniciosesion-service` corriendo en `localhost:8086` para la validación de tokens JWT.

---

## Notas Técnicas

- **ByteBuddy en api-gateway**: Se configura `net.bytebuddy.experimental=true` como `systemPropertyVariables` en el plugin surefire (no como `argLine`) para que Mockito funcione con Java 21 sin interferir con el agente de JaCoCo.
- **Lombok**: Declarado como `optional=true` y excluido del empaquetado del `spring-boot-maven-plugin`. El procesador de anotaciones se configura explícitamente en `maven-compiler-plugin`.
- **OpenAPI/Swagger**: Solo disponible en `pedido-service` en la ruta `/doc/swagger-ui.html`.
- **Jacoco 0.8.14**: Configurado en todos los servicios con las fases `prepare-agent` (antes de los tests) y `report` (después). Soporta Java 25.
- **CORS unificado**: En `registro-usuarios-service` e `iniciosesion-service`, la configuración CORS se extrajo de la lambda inline en `SecurityFilterChain` a un `@Bean CorsConfigurationSource corsConfigurationSource()`, eliminando el `WebMvcConfigurer` redundante. Esto permite testear la configuración CORS unitariamente y evita configuraciones duplicadas.
- **Logging en catch de analytics**: Se agregó `@Slf4j` y `log.warn("...", e)` en los bloques `catch (Exception e)` de los métodos `registrarLog` en `AuthServiceImpl`, `RegistroUsuarioServiceImpl` y `LoginCuentaServiceImpl`, reemplazando los catch vacíos que tragaban silenciosamente los errores de conexión con `analica-service`.
