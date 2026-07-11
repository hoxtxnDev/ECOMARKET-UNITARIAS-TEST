# Instrucciones para Testear las APIs con Postman

## Archivos incluidos

| Archivo | Descripción |
|---------|-------------|
| `ECOMMERCE-ALL-APIS.postman_collection.json` | Colección completa con **todas las APIs** (189 endpoints) organizadas por microservicio |
| `flujo principal.postman_collection.json` | Colección con el **flujo de compra completo** paso a paso |
| `API-FLOW.md` | Documentación detallada del flujo de compra (registro → login → carrito → pedido → pago → envío) |
| `CATALOGOS-SEED.md` | Script SQL para poblar los catálogos del sistema |

---

## 1. Importar la Colección en Postman

1. Abre **Postman**
2. Ve a **File → Import** (o presiona `Ctrl+O`)
3. Selecciona la pestaña **Files** y luego **Upload Files**
4. Navega hasta la carpeta `postman/` del proyecto y selecciona `ECOMMERCE-ALL-APIS.postman_collection.json`
5. Haz clic en **Import**

---

## 2. Configurar Variables de Entorno

La colección usa variables de Postman. Debes definirlas para que todo funcione:

### Variables principales:

| Variable | Valor por defecto | Descripción |
|----------|-------------------|-------------|
| `baseUrlGateway` | `http://localhost:8080` | URL del API Gateway |
| `token` | *(se asigna automáticamente)* | Token JWT para autenticación |
| `usuarioId` | *(se asigna automáticamente)* | ID del usuario autenticado |

### Cómo configurar:

**Opción 1 — Variables globales (recomendado):**
1. En Postman, ve a **Environment → Globals** (engranaje en la esquina superior derecha)
2. Agrega la variable `baseUrlGateway` con valor `http://localhost:8080`
3. Haz clic en **Save**

**Opción 2 — Variables de colección:**
1. Selecciona la colección importada
2. Ve a la pestaña **Variables**
3. Agrega `baseUrlGateway` con valor `http://localhost:8080`
4. Guarda los cambios

> ⚠️ El `token` y `usuarioId` se asignan **automáticamente** al ejecutar la request **"Login (Iniciar Sesión)"** — no necesitas configurarlos manualmente.

---

## 3. Autenticación

La colección ya tiene configurada la autenticación **Bearer Token** a nivel de colección. Esto significa que **todas las requests autenticadas** usarán `{{token}}` automáticamente.

### Flujo de autenticación:

1. Ejecuta **Login (Iniciar Sesión)** (endpoint público — sin token)
2. El script de prueba (Postman Test Script) guarda automáticamente `token` y `usuarioId` en las variables globales
3. Todas las requests siguientes usarán el token automáticamente

> El login de ejemplo usa: `correo: admin@example.com`, `contrasena: MiPassword123`. Ajusta según tu base de datos.

---

## 4. Orden Sugerido para Probar

### Flujo completo de compra (19 pasos):

1. **Registrar Usuario** → `POST /api/usuarios/registro`
2. **Login** → `POST /api/sesion/login` *(el token se guarda automáticamente)*
3. **Crear Producto** → `POST /api/catalogo`
4. **Ingresar Stock Global** → `POST /api/inventario/ingresar`
5. **Transferir Stock a Sucursal** → `POST /api/inventario/transferir`
6. **Agregar Dirección** → `POST /api/usuarios/direcciones`
7. **Agregar Producto al Carrito** → `POST /api/carrito`
8. **Seleccionar Método de Envío** → `PUT /api/carrito/envio`
9. **Seleccionar Método de Pago** → `PUT /api/carrito/pago`
10. **Generar Pedido** → `POST /api/pedidos/generar`
11. **Iniciar Pago** → `POST /api/pagos/iniciar?pedidoId=X`
12. **Confirmar Pago (Transbank)** → `POST /api/pagos/{transaccionId}/transbank`
13. **Actualizar Estado del Pedido** → `PUT /api/pedidos/{pedidoId}/estado/{estadoId}`
14. **Consultar Envío** → `GET /api/v1/logistica-envios/envios/pedido/{pedidoId}`
15. **Actualizar Estado del Envío** → `PATCH /api/v1/logistica-envios/envios/{envioId}/estado/{estadoId}`
16. **Soporte — Ingresar Ticket** (opcional) → `POST /api/v1/soporte/ingresar-ticket`

### Catálogos / CRUD (probar en cualquier orden):

- **Roles, Permisos, Estados de Perfil** → Carpeta *4. Catálogos*
- **Categorías, Estados, Especificaciones** → Carpeta *6. Catálogo Admin*
- **Estados de Pedido** → Carpeta *10. Estados de Pedido*
- **Métodos y Estados de Pago** → Carpeta *12. Métodos y Estados de Pago*
- **Logística — Catálogos** → Carpeta *14. Logística - Catálogos*
- **Tienda — Estados de Tarea** → Carpeta *16. Tienda - Catálogos*
- **Soporte — Catálogos** → Carpeta *18. Soporte - Catálogos*

---

## 5. Tabla de IDs de Catálogo

### Estados de Pedido (`/api/estado-pedido`)

| ID | Nombre |
|----|--------|
| 1 | PENDIENTE |
| 2 | CONFIRMADO |
| 3 | EN_PREPARACION |
| 4 | ENVIADO |
| 5 | EN_TRANSITO |
| 6 | ENTREGADO |
| 7 | CANCELADO |
| 8 | REEMBOLSADO |

### Estados de Envío (`/api/v1/logistica-envios/estado-envio`)

| ID | Nombre |
|----|--------|
| 1 | PREPARANDO |
| 2 | DESPACHADO |
| 3 | EN_TRANSITO |
| 4 | EN_REPARTO |
| 5 | ENTREGADO |
| 6 | INTENTO_FALLIDO |

### Estados de Pago (`/api/estado-pago`)

| ID | Nombre |
|----|--------|
| 1 | PENDIENTE |
| 2 | PROCESANDO |
| 3 | APROBADO |
| 4 | RECHAZADO |
| 5 | REEMBOLSADO |

### Métodos de Pago (`/api/metodo-pago`)

| ID | Nombre |
|----|--------|
| 1 | Tarjeta de Crédito |
| 2 | Tarjeta de Débito |
| 3 | Transferencia Bancaria |
| 4 | PayPal |
| 5 | Webpay |
| 6 | Mercado Pago |

### Métodos de Envío (`/api/v1/logistica-envios/metodo-envio`)

| ID | Nombre | Costo |
|----|--------|-------|
| 1 | Despacho a domicilio | $4.990 |
| 2 | Retiro en tienda | $0 |
| 3 | Despacho express (24 hrs) | $7.990 |

### Cupones de Descuento

| ID | Código | Descuento | Máx. descuento |
|----|--------|-----------|----------------|
| 1 | BIENVENIDO10 | 10% | $5.000 |
| 2 | PRIMERACOMPRA | 15% | $10.000 |
| 3 | CYBERLUNES25 | 25% | $25.000 |
| 4 | NAVIDAD20 | 20% | $15.000 |

### Estados de Ticket (`/api/v1/estado-ticket`)

| ID | Nombre |
|----|--------|
| 1 | ABIERTO |
| 2 | EN_PROGRESO |
| 3 | DERIVADO |
| 4 | RESUELTO |
| 5 | CERRADO |

### Categorías de Ticket (`/api/v1/categoria-ticket`)

| ID | Nombre |
|----|--------|
| 1 | Problema técnico |
| 2 | Consulta de producto |
| 3 | Problema de pago |
| 4 | Problema de envío |
| 5 | Devolución |

### Roles (`/api/usuarios/roles`)

| ID | Nombre |
|----|--------|
| 1 | ADMIN |
| 2 | CLIENTE |
| 3 | VENDEDOR |
| 4 | SOPORTE |
| 5 | GERENTE |
| 6 | REPARTIDOR |

---

## 6. Notas Importantes

- **Headers especiales:** Algunos endpoints requieren `X-User-Id` y `X-User-Roles`. La colección ya los incluye donde son necesarios.
- **Base de datos:** Antes de probar, asegúrate de ejecutar el seed SQL (`CATALOGOS-SEED.md`) para poblar los catálogos.
- **Gateway:** Todas las requests pasan por el API Gateway en `http://localhost:8080`. Asegúrate de que el gateway esté corriendo.
- **Variables:** Si cambias el puerto del gateway, solo necesitas actualizar la variable `baseUrlGateway`.
- **Swagger UI:** Cada microservicio tiene Swagger disponible en `http://localhost:{puerto}/doc/swagger-ui.html`.

---

## 7. Estructura de la Colección

```
ECOMMERCE-ALL-APIS
├── 1. Sesión (Login / Auth)          — 8 endpoints
├── 2. Usuarios (Registro y Perfiles) — 8 endpoints
├── 3. Direcciones de Usuario         — 7 endpoints
├── 4. Catálogos (Roles, Permisos)    — 12 endpoints
├── 5. Catálogo de Productos          — 7 endpoints
├── 6. Catálogo Admin                 — 16 endpoints
├── 7. Inventario                     — 10 endpoints
├── 8. Carrito de Compras             — 8 endpoints
├── 9. Pedidos                        — 6 endpoints
├── 10. Estados de Pedido             — 5 endpoints
├── 11. Pagos                         — 7 endpoints
├── 12. Métodos y Estados de Pago     — 10 endpoints
├── 13. Logística / Envíos            — 14 endpoints
├── 14. Logística - Catálogos         — 18 endpoints
├── 15. Tienda (Sucursales)           — 9 endpoints
├── 16. Tienda - Catálogos            — 5 endpoints
├── 17. Soporte (Tickets y Chat)      — 15 endpoints
├── 18. Soporte - Catálogos           — 9 endpoints
├── 19. Reseñas (Productos)           — 7 endpoints
├── 20. Analítica                     — 18 endpoints
└── 21. Mock Transportistas           — 1 endpoint
```
