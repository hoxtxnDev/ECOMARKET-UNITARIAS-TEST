# EcoMarket - Microservicio de Proceso de Pago (procesopagoservice)

Microservicio de pagos del proyecto EcoMarket. Gestiona el ciclo de vida completo de las transacciones, incluyendo inicio de pago, procesamiento con Transbank (mock), cupones de descuento, reembolsos, facturación electrónica y envío de boletas por email.

## Características Principales

- **Inicio de Pago:** Crea transacciones para un pedido con soporte de claves de idempotencia para evitar duplicados.
- **Procesamiento Transbank (Mock):** Simula el flujo de aprobación/rechazo de pagos con validación de token.
- **Cupones de Descuento:** Aplica descuentos porcentuales a transacciones pendientes con validación de vigencia y tope máximo.
- **Reembolsos:** Procesa devoluciones completas solo sobre transacciones aprobadas.
- **Facturación Electrónica:** Genera facturas con RUT, giro, folio fiscal y documento XML.
- **Envío de Boleta:** Simulación de envío de comprobante de pago por email.
- **Catálogos:** CRUD completo de métodos de pago y estados de pago.
- **Integración Multi-servicio:** Se comunica con pedido-service, carrito-compra-service y analitica-service.

## Tecnologías Utilizadas

| Componente | Versión |
|---|---|
| **Lenguaje** | Java 25 |
| **Framework** | Spring Boot 4.0.7 |
| **ORM** | Spring Data JPA + Hibernate |
| **Base de Datos** | MySQL 8.0 — `proceso_pago_db` |
| **Comunicación** | RestTemplate + RestClient |
| **Build** | Maven |

## Estructura del Proyecto

```
procesopagoservice/
├── pom.xml
├── src/main/java/com/ecomarket/procesopagoservice/
│   ├── ProcesoPagoServiceApplication.java
│   ├── config/
│   │   ├── RestClientConfig.java
│   │   └── RestTemplateConfig.java
│   ├── controller/
│   │   ├── EstadoPagoController.java
│   │   ├── MetodoPagoTransaccionController.java
│   │   └── PagoController.java
│   ├── dto/ErrorResponseDTO.java
│   ├── exception/ (CuponInvalidoException, EstadoTransaccionInvalidoException,
│   │              GlobalExceptionHandler, ProcesamientoPagoException, RecursoNoEncontradoException)
│   ├── model/ (CuponDescuento, EstadoPago, FacturaElectronica,
│   │           MetodoPagoTransaccion, TransaccionPago)
│   ├── repository/ (CuponRepository, EstadoPagoRepository, FacturaRepository,
│   │                MetodoPagoRepository, TransaccionRepository)
│   └── service/PagoService.java
└── src/test/java/com/ecomarket/procesopagoservice/
    ├── ProcesoPagoServiceApplicationTests.java
    ├── config/ (RestClientConfigTest, RestTemplateConfigTest)
    ├── controller/ (EstadoPagoControllerTest, MetodoPagoTransaccionControllerTest, PagoControllerTest)
    ├── dto/ErrorResponseDTOTest.java
    ├── exception/GlobalExceptionHandlerTest.java
    ├── model/CuponDescuentoTest.java
    └── service/PagoServiceTest.java
```

## API Endpoints

### Pagos - `/api/pagos`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/pagos/iniciar?pedidoId=&idempotencyKey=` | Inicia pago para un pedido |
| GET | `/api/pagos/{transaccionId}` | Obtiene transacción por ID |
| POST | `/api/pagos/{transaccionId}/cupon/{cuponId}` | Aplica cupón de descuento |
| POST | `/api/pagos/{transaccionId}/transbank?token=` | Procesa pago con Transbank |
| POST | `/api/pagos/{transaccionId}/reembolso?motivo=` | Procesa reembolso |
| POST | `/api/pagos/{transaccionId}/factura?rut=&giro=` | Genera factura electrónica |
| POST | `/api/pagos/{transaccionId}/email?correoDestino=` | Envía boleta por email |

### Métodos de Pago - `/api/metodo-pago`

CRUD completo: GET, GET/{id}, POST, PUT/{id}, DELETE/{id}

### Estados de Pago - `/api/estado-pago`

CRUD completo: GET, GET/{id}, POST, PUT/{id}, DELETE/{id}

## Configuración de Base de Datos

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | URL JDBC | `jdbc:mysql://localhost:3306/proceso_pago_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Password BD | |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estrategia DDL | `update` |

## Dependencias entre Servicios

| Servicio | Puerto | Propósito |
|----------|--------|-----------|
| pedido-service | 8089 | Obtener datos del pedido, actualizar estado a CONFIRMADO/CANCELADO |
| carrito-compra | 8082 | Vaciar carrito del cliente tras pago exitoso |
| analitica | 8084 | Registrar auditoría de eventos de pago |

## Notas Importantes

- Estados de pago: 1=PENDIENTE, 2=APROBADO, 3=RECHAZADO, 4=REEMBOLSADO.
- El cupón solo puede aplicarse a transacciones en estado PENDIENTE.
- El reembolso solo es válido para transacciones APROBADO; la transacción pasa a REEMBOLSADO y el pedido a CANCELADO.
- La clave de idempotencia evita crear transacciones duplicadas para un mismo pedido.
- Transbank es una simulación: token="token_valido" → APROBADO, cualquier otro → RECHAZADO.

## Pruebas

```bash
mvn test
```

**96 tests** distribuidos en 10 clases de prueba, cubriendo servicio (7 casos de negocio), controladores (17 casos CRUD + web), DTOs, excepciones (9 tipos) y modelos de dominio.
