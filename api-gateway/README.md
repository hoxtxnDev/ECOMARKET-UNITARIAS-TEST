# EcoMarket - API Gateway (api-gateway)

Punto de entrada único para todas las peticiones del ecosistema EcoMarket. Implementado con Spring Cloud Gateway, enruta las solicitudes a los microservicios backend y valida tokens JWT de forma centralizada.

## Características Principales

- **Enrutamiento Centralizado:** 25 rutas declarativas mapean cada prefijo de API al microservicio correspondiente (puertos 8081-8090).
- **Autenticación JWT:** Filtro personalizado (`JwtAuthenticationFilter`) que intercepta rutas protegidas, extrae el token Bearer y lo valida contra `iniciosesion-service`.
- **Rutas Públicas:** Login, registro y endpoints de catálogos (roles, estados, métodos de pago, etc.) no requieren autenticación.
- **Soporte CORS:** Las peticiones `OPTIONS` (preflight) pasan sin autenticación.
- **Arquitectura Reactiva:** Basado en Spring WebFlux y Netty, no en el stack Servlet tradicional.

## Tecnologías Utilizadas

| Componente | Versión |
|---|---|
| **Lenguaje** | Java 21 |
| **Framework** | Spring Boot 3.4.13 |
| **Gateway** | Spring Cloud Gateway 2024.0.0 |
| **Arquitectura** | Reactiva (WebFlux / Netty) |
| **Validación** | Jakarta Validation |
| **Build** | Maven |
| **Pruebas** | JUnit 5 + Mockito |

## Estructura del Proyecto

```
api-gateway/
├── pom.xml
├── src/main/java/com/ecomarket/gateway/
│   ├── GatewayApplication.java              # @SpringBootApplication
│   ├── config/
│   │   └── RouteConfiguration.java          # Rutas programáticas con JWT filter
│   └── filter/
│       └── JwtAuthenticationFilter.java     # GatewayFilter de autenticación
├── src/main/resources/
│   └── application.properties               # Puerto 8080, rutas declarativas
└── src/test/java/com/ecomarket/gateway/
    ├── GatewayApplicationTest.java
    ├── config/RouteConfigurationTest.java
    └── filter/JwtAuthenticationFilterTest.java
```

## API Routes (Gateway)

| Ruta | Destino | Puerto | Autenticación |
|------|---------|--------|:---:|
| `/api/usuarios/**` | registro-usuarios-service | 8081 | Sí |
| `/api/carrito/**` | carrito-compra-service | 8082 | Sí |
| `/api/v1/logistica-envios/**` | logistica-envios-service | 8083 | Sí |
| `/api/analitica/**` | analica-service | 8084 | Sí |
| `/api/pagos/**` | proceso-pago-service | 8085 | Sí |
| `/api/sesion/**` | iniciosesion-service | 8086 | No (público) |
| `/api/catalogo/**` | catalogo-inventario-service | 8087 | Sí |
| `/api/v1/soporte/**` | soporte-service | 8088 | Sí |
| `/api/pedidos/**` | pedido-service | 8089 | Sí |
| `/api/tienda/**` | gestion-tienda-service | 8090 | Sí |
| `/api/inventario/**` | catalogo-inventario-service | 8087 | Sí |
| `/api/metodo-pago/**` | proceso-pago-service | 8085 | Sí |
| `/api/estado-pago/**` | proceso-pago-service | 8085 | Sí |
| `/api/estado-pedido/**` | pedido-service | 8089 | Sí |

### Rutas Públicas (sin JWT)

- `POST /api/sesion/login` — Inicio de sesión
- `POST /api/sesion/credencial` — Crear credencial
- `POST /api/usuarios/registro` — Registro de usuario
- `GET /api/usuarios/roles` — Listar roles
- `GET /api/usuarios/estados-perfil` — Estados de perfil
- `GET /api/usuarios/permisos` — Listar permisos
- `GET /api/estado-pago`, `/api/metodo-pago`, `/api/estado-pedido` — Catálogos
- `GET /api/v1/estado-envio`, `/api/v1/metodo-envio`, `/api/v1/puntos-retiro` — Catálogos
- `OPTIONS` — CORS preflight

## Despliegue

### Requisitos

- JDK 21
- Maven 3.9+
- `iniciosesion-service` corriendo en `localhost:8086`

### Ejecutar

```bash
cd api-gateway
mvn spring-boot:run
```

El gateway queda disponible en `http://localhost:8080`.

### Compilar JAR

```bash
mvn clean package -DskipTests
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

## Dependencias

El gateway depende de `iniciosesion-service` (puerto 8086) para validar tokens JWT en tiempo real. Si no está disponible, todas las rutas autenticadas devuelven `401 Unauthorized`.

## Notas Importantes

- El gateway usa **dos capas de rutas**: las programáticas (`RouteConfiguration.java`) aplican el filtro JWT; las declarativas (`application.properties`) proporcionan rutas adicionales sin filtro personalizado.
- Las rutas programáticas tienen prioridad sobre las declarativas.
- Este es el único microservicio que usa Spring Boot 3.4.x + Java 21 (por compatibilidad con Spring Cloud Gateway 2024.0.0).

## Pruebas

```bash
mvn test
```

**10 tests** distribuidos en 3 clases de prueba, cubriendo:
- Carga de contexto Spring
- Configuración de rutas (`RouteLocator`)
- Filtro JWT (7 casos: OPTIONS, rutas públicas, token ausente/inválido/válido, excepciones del servicio de validación)
