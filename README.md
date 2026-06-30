<p align="center">
  <h1 align="center">— ECOMMARKET —</h1>
  <p align="center">Plataforma de Comercio Electrónico · Arquitectura de Microservicios</p>
  <br>
  <p align="center">
    <img src="https://img.shields.io/badge/Spring_Boot-3.4.1_%2F_4.0.6-6DB33F?logo=springboot" alt="Spring Boot">
    <img src="https://img.shields.io/badge/Java-21_%2F_25-ED8B00?logo=openjdk" alt="Java">
    <img src="https://img.shields.io/badge/Spring_Cloud_Gateway-2024.0.0-6DB33F" alt="Spring Cloud">
    <img src="https://img.shields.io/badge/coverage-100%25-brightgreen" alt="Coverage 100%">
    <img src="https://img.shields.io/badge/MySQL-8+-4479A1?logo=mysql" alt="MySQL">
    <img src="https://img.shields.io/badge/JWT-HS256-000000?logo=jsonwebtokens" alt="JWT">
    <img src="https://img.shields.io/badge/JUnit_5-25.1-25A162?logo=junit5" alt="JUnit 5">
    <img src="https://img.shields.io/badge/Maven-4.0-C71A36?logo=apachemaven" alt="Maven">
    <img src="https://img.shields.io/badge/11_servicios-1149_tests-blue" alt="11 servicios">
  </p>
</p>

<details open>
  <summary>Índice</summary>

- [Tecnologías](#tecnologías)
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

## Tecnologías

| Categoría | Tecnología | Versión |
|---|---|---|
| **Lenguaje** | Java | 21 (api-gateway) / 25 (demás servicios) |
| **Framework** | Spring Boot | 3.4.1 (api-gateway) / 4.0.6 (demás servicios) |
| **Gateway** | Spring Cloud Gateway | 2024.0.0 |
| **ORM** | Spring Data JPA + Hibernate | 7.x |
| **Base de Datos** | MySQL | 8+ |
| **Base de Datos (tests)** | H2 | En memoria |
| **Autenticación** | JWT (HMAC-SHA256) | jjwt 0.12.5 |
| **Documentación API** | SpringDoc OpenAPI | 2.6.0 |
| **Pruebas** | JUnit 5 + Mockito + AssertJ + MockMvc | — |
| **Cobertura** | JaCoCo | 0.8.14 |
| **Build** | Apache Maven | — |
| **Boilerplate** | Lombok | — |
| **Validación** | Jakarta Validation | — |

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
| 1 | `api-gateway` | `8080` | Punto de entrada único; enruta requests y valida JWT | Hans |
| 2 | `registro-usuarios-service` | `8081` | Registro de usuarios, perfiles, roles y permisos | Horacio |
| 3 | `carrito-compra-service` | `8082` | Carrito de compras y orquestación de checkout | Hans |
| 4 | `logistica-envios-service` | `8083` | Logística de envíos, rutas, puntos de retiro, seguimiento | Horacio |
| 5 | `analica-service` | `8084` | Analíticas, reportes, métricas, respaldos, alertas | Hans |
| 6 | `proceso-pago-service` | `8085` | Procesamiento de pagos, transacciones, facturas, cupones | Hans |
| 7 | `iniciosesion-service` | `8086` | Autenticación, login, gestión de tokens JWT, recuperación de contraseña | Horacio |
| 8 | `catalogo-inventario-service` | `8087` | Catálogo de productos, gestión de stock e inventario | Horacio |
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
│   ├── *Service.java                # @Service — lógica de negocio
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

> Los servicios usan clases concretas con `@Service`, sin interfaz separada (estilo moderno Spring). Solo hay una clase por servicio de negocio. `api-gateway` es la excepción: no tiene capa de persistencia ni modelo; su estructura es `filter/JwtAuthenticationFilter.java` y `config/RouteConfiguration.java`.

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

## Flujo Principal de Compra

El flujo completo de la plataforma abarca 7 microservicios y sigue esta secuencia:

```
               ┌──────────────┐
               │  Cliente     │
               │  (registro)  │
               └──────┬───────┘
                      │ 1. POST /api/usuarios/registro
                      ▼
               ┌──────────────┐
               │  registro-   │───► iniciosesion → crea credencial
               │  usuarios    │───► analitica    → log de auditoría
               └──────┬───────┘
                      │ 2. POST /api/sesion/login
                      ▼
               ┌──────────────┐
               │  iniciosesion│───► Devuelve JWT
               └──────┬───────┘
                      │ 3. POST /api/usuarios/direcciones/{id}
                      ▼
               ┌──────────────┐
               │  registro-   │───► Agrega dirección de despacho
               │  usuarios    │
               └──────────────┘
```

### Catálogo e Inventario

```
                      │ 4. POST /api/catalogo           (crear producto)
                      │ 5. POST /api/inventario/ingresar (stock global)
                      │ 6. POST /api/inventario/transferir (a sucursal)
                      ▼
               ┌──────────────┐
               │  catalogo-   │───► gestion-tienda → valida sucursal
               │  inventario  │
               └──────────────┘
```

### Carrito y Pedido

```
                      │ 7. POST /api/carrito                    (agregar producto)
                      │ 8. PUT  /api/carrito/{id}/envio         (método de envío)
                      │ 9. PUT  /api/carrito/{id}/pago          (método de pago)
                      │ 10. POST /api/carrito/{id}/checkout     (reservar stock, cerrar)
                      │ 11. POST /api/pedidos/generar/{cli}/{carrito} (crear pedido)
                      ▼
               ┌──────────────┐
               │  carrito-    │───► catalogo-inventario → verifica stock
               │  compra      │───► pedido-service     → genera pedido
               └──────┬───────┘
                      │ 12. GET /api/pedidos/{id}
                      ▼
               ┌──────────────┐
               │  pedido-     │
               │  service     │
               └──────┬───────┘
```

### Pago y Despacho

```
                      │ 13. POST /api/pagos/iniciar?pedidoId={id}  (crear transacción PENDIENTE)
                      │ 14. POST /api/pagos/{txn}/cupon/{cuponId}  (aplicar descuento, opcional)
                      │ 15. POST /api/pagos/{txn}/transbank?token=… (procesar pago)
                      ▼
               ┌──────────────┐
               │  proceso-    │───► pedido-service → actualiza a CONFIRMADO
               │  pago        │───► carrito-compra → vacía carrito
               └──────┬───────┘───► analitica      → log de auditoría
                      │ 16. PUT /api/pedidos/{id}/estado/{estadoId}
                      ▼
               ┌──────────────┐
               │  pedido-     │───► logistica-envios → crea envío (si estado=ENVIADO)
               │  service     │
               └──────┬───────┘
                      │ 17. GET /api/v1/logistica-envios/envios/pedido/{pedidoId}
                      ▼
               ┌──────────────┐
               │  logistica-  │───► seguimiento del envío
               │  envios      │
               └──────────────┘
```

### Resumen de Pasos

| # | Acción | Endpoint | Servicio | Puerto |
|---|--------|----------|----------|--------|
| 1 | Registrar usuario | `POST /api/usuarios/registro` | registro-usuarios | 8081 |
| 2 | Iniciar sesión | `POST /api/sesion/login` | iniciosesion | 8086 |
| 3 | Agregar dirección | `POST /api/usuarios/direcciones/{id}` | registro-usuarios | 8081 |
| 4 | Crear producto | `POST /api/catalogo` | catalogo-inventario | 8087 |
| 5 | Ingresar stock global | `POST /api/inventario/ingresar` | catalogo-inventario | 8087 |
| 6 | Transferir stock a sucursal | `POST /api/inventario/transferir` | catalogo-inventario | 8087 |
| 7 | Agregar producto al carrito | `POST /api/carrito` | carrito-compra | 8082 |
| 8 | Seleccionar método de envío | `PUT /api/carrito/{id}/envio` | carrito-compra | 8082 |
| 9 | Seleccionar método de pago | `PUT /api/carrito/{id}/pago` | carrito-compra | 8082 |
| 10 | Cerrar carrito (checkout) | `POST /api/carrito/{id}/checkout` | carrito-compra | 8082 |
| 11 | Generar pedido | `POST /api/pedidos/generar/{cli}/{carrito}` | pedido-service | 8089 |
| 12 | Revisar pedido | `GET /api/pedidos/{id}` | pedido-service | 8089 |
| 13 | Iniciar pago | `POST /api/pagos/iniciar?pedidoId={id}` | proceso-pago | 8085 |
| 14 | Aplicar cupón | `POST /api/pagos/{txn}/cupon/{cuponId}` | proceso-pago | 8085 |
| 15 | Confirmar pago Transbank | `POST /api/pagos/{txn}/transbank?token=...` | proceso-pago | 8085 |
| 16 | Actualizar estado pedido | `PUT /api/pedidos/{id}/estado/{estadoId}` | pedido-service | 8089 |
| 17 | Consultar envío por pedido | `GET /api/v1/logistica-envios/envios/pedido/{pedidoId}` | logistica-envios | 8083 |

> Todos los endpoints (excepto login y registro) requieren autenticación JWT. El gateway (`api-gateway:8080`) funciona como proxy único; las peticiones deben dirigirse a `http://localhost:8080/api/...` y el gateway las redirige al servicio correspondiente. Los puertos directos mostrados en la tabla son solo para desarrollo o pruebas sin autenticación.

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

Los tests utilizan un perfil `test` (`application-test.properties`) que configura H2 en memoria con `ddl-auto=create-drop`, eliminando cualquier dependencia de MySQL durante la ejecución de pruebas. El reporte JaCoCo se genera en `target/site/jacoco/index.html` tras ejecutar:

```bash
mvn clean test
```

**Resumen de pruebas por servicio:**

| Servicio | Tests | JaCoCo |
|---|---|---|
| `api-gateway` | 10 | ✅ 100 % |
| `analica-service` | 127 | ✅ 100 % |
| `carrito-compra-service` | 54 | ✅ 100 % |
| `catalogo-inventario-service` | 136 | ✅ 100 % |
| `gestion-tienda-service` | 82 | ✅ 100 % |
| `iniciosesion-service` | 106 | ✅ 100 % |
| `pedido-service` | 67 | ✅ 100 % |
| `proceso-pago-service` | 106 | ✅ 100 % |
| `logistica-envios-service` | 152 | ✅ 100 % |
| `registro-usuarios-service` | 101 | ✅ 100 % |
| `soporte-service` | 208 | ✅ 100 % |
| **Total** | **1149** | — |

> Todos los servicios tienen JaCoCo configurado y generan reporte de cobertura en `target/site/jacoco/index.html`.

---

### Pruebas de Productos y Categorías

Los productos se gestionan en `catalogo-inventario-service` (puerto `8087`). Se incluyen datos de prueba en `test-data/`:

| Archivo | Contenido |
|---------|-----------|
| `test-data/categorias.json` | 10 categorías (Alimentos, Limpieza, Electrónica, etc.) |
| `test-data/estados.json` | 4 estados de disponibilidad |
| `test-data/productos.json` | 100 productos listos para importar |

#### Importar productos con curl

```bash
# 1. Primero crear las categorías (si no existen en BD)
curl -X POST http://localhost:8087/api/catalogo-admin/categorias \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Alimentos y Bebidas"}'

# 2. Importar productos individuales
curl -X POST http://localhost:8087/api/catalogo \
  -H "Content-Type: application/json" \
  -d '{"sku":"ALI-001","nombre":"Arroz Blanco 1kg","descripcion":"Arroz de grano largo premium","precioBase":2800,"categoria":{"id":1},"estado":{"id":1}}'

# 3. Verificar — listar todos los productos
curl http://localhost:8087/api/catalogo
```

#### Probar endpoints de producto

```bash
# Listar todos los productos
curl http://localhost:8087/api/catalogo

# Filtrar por categoría (ej: Electrónica = categoría id 3)
curl http://localhost:8087/api/catalogo/categoria/3

# Buscar por nombre
curl "http://localhost:8087/api/catalogo/buscar?nombre=arroz"

# Obtener detalle de un producto
curl http://localhost:8087/api/catalogo/1

# Crear un nuevo producto
curl -X POST http://localhost:8087/api/catalogo \
  -H "Content-Type: application/json" \
  -d '{"sku":"TEST-001","nombre":"Producto de Prueba","descripcion":"Para verificar funcionalidad","precioBase":10000,"categoria":{"id":1},"estado":{"id":1}}'

# Editar un producto
curl -X PUT http://localhost:8087/api/catalogo/1 \
  -H "Content-Type: application/json" \
  -d '{"sku":"ALI-001","nombre":"Arroz Blanco 1kg (Actualizado)","descripcion":"Descripción actualizada","precioBase":3000,"categoria":{"id":1},"estado":{"id":1}}'

# Eliminar un producto
curl -X DELETE http://localhost:8087/api/catalogo/101

# Probar inventario
curl http://localhost:8087/api/inventario/producto/1
```

> **Nota**: Los endpoints de `catalogo-inventario-service` están protegidos por JWT cuando se accede a través del `api-gateway`. Para pruebas directas sin autenticación, apunta al servicio directamente en `http://localhost:8087`.

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

- **ByteBuddy en todos los servicios**: Se configura `net.bytebuddy.experimental=true` como `systemPropertyVariables` en el plugin surefire de los 11 servicios (no como `argLine`) para que Mockito funcione con Java 21 y 25 sin interferir con el agente de JaCoCo.
- **Lombok**: Declarado como `optional=true` y excluido del empaquetado del `spring-boot-maven-plugin`. El procesador de anotaciones se configura explícitamente en `maven-compiler-plugin`.
- **OpenAPI/Swagger**: Solo disponible en `pedido-service` en la ruta `/doc/swagger-ui.html`.
- **JaCoCo 0.8.14**: Configurado en todos los servicios con las fases `prepare-agent` (antes de tests) y `report` (después). Soporta Java 25.
- **Efecto espejo (mirror effect)**: Cada clase en `src/main` tiene su correspondiente `*Test.java` en `src/test` bajo el mismo package. Auditoría completada en los 11 servicios, incluyendo los **80 DTOs** de todos los proyectos que ahora cuentan con su respectivo test unitario (constructor, builder o setters según la anotación de cada DTO).
- **JWT secret externalizado**: La propiedad `jwt.secret` se lee de la variable de entorno `JWT_SECRET` (no hardcodeada). Ver `.env.example` en `registro-usuarios-service` e `iniciosesion-service` para la configuración local.
- **api-gateway como repositorio propio**: El gateway se independizó de un submodule roto; ahora es un proyecto Maven estándar dentro del mismo repositorio.
- **CI/CD**: GitHub Actions (`ci.yml`) ejecuta `mvn test` para los 11 servicios con `working-directory` por módulo, `fail-fast: false` para visibilidad completa, `JWT_SECRET` inyectado como `${{ secrets.JWT_SECRET }}`, y `java-version` configurado por servicio (21 para api-gateway, 25 para el resto).
- **CORS unificado**: En `registro-usuarios-service` e `iniciosesion-service`, la configuración CORS se extrajo de la lambda inline en `SecurityFilterChain` a un `@Bean CorsConfigurationSource`, eliminando el `WebMvcConfigurer` redundante y permitiendo pruebas unitarias de CORS.
- **Logging en catch de analytics**: Se agregó `@Slf4j` y `log.warn` en los `catch (Exception e)` de `registrarLog()` en `AuthServiceImpl`, `RegistroUsuarioServiceImpl` y `LoginCuentaServiceImpl`, reemplazando catch vacíos.
- **404 en lugar de 400**: Se creó `RecursoNoEncontradoException` y su handler en `GlobalExceptionHandler` retorna `404 NOT_FOUND`. Se reemplazaron `RuntimeException("... no encontrado ...")` en `UsuarioController`, `CatalogoController`, `UsuarioService` y `RegistroUsuarioService`.
- **Refactor `buildPerfil`**: Se fusionaron `buildPerfilDesdeRegistroDTO` y `buildPerfilDesdeModificarDTO` en un único método `buildPerfil(String, String, String, Long, Long)` en `registro-usuarios-service`.
- **Datos de prueba**: 100 productos JSON, 10 categorías y 4 estados de disponibilidad en `test-data/` para pruebas black-box contra `catalogo-inventario-service`.
- **Pruebas de config beans**: Se agregaron tests para todas las clases `@Configuration` que definen beans (`RestTemplate`, `RestClient`) en servicios donde faltaban.
- **Sin interfaces de servicio**: Se eliminaron las 6 interfaces `*Service` que tenían una única implementación (`PedidoService`, `PagoService`, `AuthService`, `RegistroUsuarioService`, `LoginCuentaService`, `GestionTiendaService`). Ahora todos los servicios del proyecto son clases concretas con `@Service`, siguiendo el estilo moderno de Spring Boot.
- **CorreoDuplicadoException en RegistroUsuarioService y UsuarioService**: Se reemplazaron los `RuntimeException` de correo duplicado en `registrarCuenta()`, `modificarDatosUsuario()` y `registrar()` por `CorreoDuplicadoException` (∼409 CONFLICT), emparejando el manejo que ya existía en `LoginCuentaService`.
- **.env agregado a .gitignore**: Se añadió `.env` al `.gitignore` raíz para evitar que archivos de entorno local se trackeen accidentalmente.
