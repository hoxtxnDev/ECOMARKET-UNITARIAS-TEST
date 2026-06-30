# API Flow — Ecomarket

Flujo completo de compra: registro → login → producto → carrito → pedido → pago → estados → envío.

**Base URL Gateway:** `{{baseUrlGateway}} = http://localhost:8080`

---

## 1. Registrar Usuario

```
POST {{baseUrlGateway}}/api/usuarios/registro
```

```json
{
  "nombre": "Juan Pérez",
  "correo": "juan.perez@email.com",
  "contrasenaInicial": "clave123",
  "telefono": "+56912345678",
  "rolId": 2
}
```

**Response:**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "correo": "juan.perez@email.com",
  "rol": { "id": 2, "nombre": "CLIENTE" },
  "estadoPerfil": { "id": 1, "nombre": "ACTIVO" }
}
```

> Guardar `id` del usuario como `usuarioId` (clienteId).

---

## 2. Iniciar Sesión (Login)

```
POST {{baseUrlGateway}}/api/sesion/login
```

```json
{
  "correo": "juan.perez@email.com",
  "contrasena": "clave123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "usuarioId": 1,
  "correo": "juan.perez@email.com",
  "rol": "CLIENTE",
  "expiracionMs": 1800000
}
```

> Usar `token` como `Authorization: Bearer <token>` en todas las peticiones siguientes.

---

## 3. Crear Producto

```
POST {{baseUrlGateway}}/api/catalogo
Authorization: Bearer eyJ...
```

```json
{
  "sku": "PROD-001",
  "nombre": "Audífonos Bluetooth",
  "descripcion": "Audífonos inalámbricos con cancelación de ruido",
  "precioBase": 29990.0,
  "categoriaId": 4,
  "estadoId": 1,
  "imagenUrl": "https://ejemplo.com/audifonos.jpg"
}
```

**Response:**
```json
{
  "id": 1,
  "sku": "PROD-001",
  "nombre": "Audífonos Bluetooth",
  "precioBase": 29990.0,
  "categoria": { "id": 4, "nombre": "Audio y Video" },
  "estado": { "id": 1, "nombre": "DISPONIBLE" }
}
```

---

## 4. Ingresar Stock Global

```
POST {{baseUrlGateway}}/api/inventario/ingresar
Authorization: Bearer eyJ...
```

```json
{
  "productoId": 1,
  "cantidad": 500
}
```

**Response:**
```json
{
  "id": 1,
  "productoId": 1,
  "cantidadDisponible": 500,
  "ultimaActualizacion": "2026-06-27T12:06:00"
}
```

---

## 5. Agregar al Carrito

```
POST {{baseUrlGateway}}/api/carrito
Authorization: Bearer eyJ...
```

```json
{
  "usuarioId": 1,
  "productoId": 1,
  "cantidad": 2
}
```

**Response:**
```json
{
  "id": 1,
  "clienteId": 1,
  "subtotal": 59980.0,
  "metodoEnvioId": null,
  "metodoPagoId": null,
  "activo": true,
  "cerrado": false,
  "items": [
    {
      "id": 1,
      "productoId": 1,
      "cantidad": 2,
      "precioUnitario": 29990.0,
      "subtotal": 59980.0
    }
  ]
}
```

> Repetir para más productos. Guardar `id` del carrito (`carritoId`).

---

## 6. Seleccionar Método de Envío

```
PUT {{baseUrlGateway}}/api/carrito/1/envio
Authorization: Bearer eyJ...
```

```json
{ "id": 1 }
```

> `id` = id del método de envío (1=Despacho a domicilio, 2=Retiro en tienda).

**Response:** Carrito actualizado con `metodoEnvioId` asignado.

---

## 7. Seleccionar Método de Pago

```
PUT {{baseUrlGateway}}/api/carrito/1/pago
Authorization: Bearer eyJ...
```

```json
{ "id": 5 }
```

> `id` = id del método de pago (5=Webpay para Transbank automático, 3=Transferencia Bancaria para manual).

**Response:** Carrito actualizado con `metodoPagoId` asignado.

---

## 8. Checkout (Cerrar Carrito)

Cierra el carrito y devuelve su ID para generar el pedido.

```
POST {{baseUrlGateway}}/api/carrito/1/checkout
Authorization: Bearer eyJ...
```

**Response:** `1` (carritoId, no pedidoId)

---

## 9. Generar Pedido

Crea el pedido en pedido-service con estado **PENDIENTE** (id=1).

```
POST {{baseUrlGateway}}/api/pedidos/generar/1/1
Authorization: Bearer eyJ...
```

**Path Variables:** `clienteId = 1`, `carritoId = 1`

**Response:**
```json
{
  "id": 1,
  "clienteId": 1,
  "subtotal": 59980.0,
  "total": 59980.0,
  "estado": { "id": 1, "nombre": "PENDIENTE" },
  "fechaCreacion": "2026-06-27T12:20:00"
}
```

> Guardar `id` del pedido (`pedidoId = 1`).

---

## 10. Iniciar Pago

El método de pago se lee automáticamente del pedido (asignado en el paso 7).

```
POST {{baseUrlGateway}}/api/pagos/iniciar?pedidoId=1
Authorization: Bearer eyJ...
```

**Response:**
```json
{
  "id": 1,
  "pedidoId": 1,
  "montoSubtotal": 59980.0,
  "montoDescuento": 0.0,
  "montoTotal": 59980.0,
  "metodoPago": { "id": 5, "nombre": "Webpay" },
  "estado": { "id": 1, "nombre": "PENDIENTE" },
  "tokenTransbank": "TB-1A2B3C4D",
  "fechaInicio": "2026-06-27T12:22:00"
}
```

> Guardar `id` de la transacción (`transaccionId`).

---

## 11. Aplicar Cupón de Descuento (Opcional)

```
POST {{baseUrlGateway}}/api/pagos/1/cupon/1
Authorization: Bearer eyJ...
```

**Path Variables:** `transaccionId=1`, `cuponId=1` (BIENVENIDO10 = 10%)

**Response:**
```json
{
  "id": 1,
  "montoSubtotal": 59980.0,
  "montoDescuento": 5998.0,
  "montoTotal": 53982.0,
  "estado": { "id": 1, "nombre": "PENDIENTE" },
  "cuponUtilizadoId": 1
}
```

---

## 12. Confirmar Pago (Transbank)

```
POST {{baseUrlGateway}}/api/pagos/1/transbank?token=xyz123token
Authorization: Bearer eyJ...
```

**Si el método de pago es automático** (Tarjeta Crédito, Débito, PayPal, Webpay, Mercado Pago, Criptomonedas):
- Estado del pago → **APROBADO**
- Pedido auto-actualizado → **CONFIRMADO** → **EN_PREPARACION**

**Si el método es manual** (Transferencia Bancaria, Pago en Efectivo, Contra Entrega):
- Estado del pago → **APROBADO**
- Pedido queda en **PENDIENTE** (espera revisión manual)

**Response (éxito):**
```json
{
  "id": 1,
  "pedidoId": 1,
  "montoSubtotal": 59980.0,
  "montoDescuento": 5998.0,
  "montoTotal": 53982.0,
  "estado": { "id": 3, "nombre": "APROBADO" },
  "codigoAutorizacion": "AUTH-ABC123",
  "fechaFin": "2026-06-27T12:28:00"
}
```

---

## 13. [Solo Manual] Confirmar Pedido Manualmente

Solo si el método de pago es manual (Transferencia Bancaria, Pago en Efectivo, Contra Entrega).

```
PUT {{baseUrlGateway}}/api/pedidos/1/estado/2
Authorization: Bearer eyJ...
```

**Path Variables:** `pedidoId=1`, `estadoId=2` (CONFIRMADO)

**Respuesta:**
```json
{
  "id": 1,
  "estado": { "id": 2, "nombre": "CONFIRMADO" }
}
```

---

## 14. [Solo Manual] Pasar a EN_PREPARACION

Solo si el método de pago es manual.

```
PUT {{baseUrlGateway}}/api/pedidos/1/estado/3
Authorization: Bearer eyJ...
```

**Path Variables:** `pedidoId=1`, `estadoId=3` (EN_PREPARACION)

---

## 15. Enviar Pedido (ENVIADO)

```
PUT {{baseUrlGateway}}/api/pedidos/1/estado/4
Authorization: Bearer eyJ...
```

**Path Variables:** `pedidoId=1`, `estadoId=4` (ENVIADO)

**Response:**
```json
{
  "id": 1,
  "estado": { "id": 4, "nombre": "ENVIADO" }
}
```

> Este paso también se puede hacer vía nombre:
> `PUT /api/pedidos/1/estado-nombre` con body `"ENVIADO"`

---

## 16. Crear Envío en Logística (Automático)

Al asignar estado ENVIADO, el pedido-service dispara automáticamente la creación del envío en logística-envios-service.

O llamar explícitamente:

```
POST {{baseUrlGateway}}/api/v1/logistica-envios/envios/auto/1
Authorization: Bearer eyJ...
```

**Path Variable:** `pedidoId=1`

**Response:**
```json
{
  "id": 1,
  "pedidoId": 1,
  "clienteId": 1,
  "estadoActual": { "id": 1, "nombre": "PREPARANDO" },
  "costoEnvio": 4990.0,
  "fechaCreacion": "2026-06-27T12:30:00",
  "fechaEstimadaEntrega": "2026-06-29T12:30:00"
}
```

> Guardar `id` del envío (`envioId`).

---

## 17. Consultar Envío

```
GET {{baseUrlGateway}}/api/v1/logistica-envios/envios/1
Authorization: Bearer eyJ...
```

---

## 18. Actualizar Estado del Envío

| Estado | ID | Observación |
|--------|----|-------------|
| PREPARANDO | 1 | (estado inicial) |
| DESPACHADO | 2 | Paquete despachado desde bodega |
| EN_TRANSITO | 3 | En tránsito hacia destino |
| EN_REPARTO | 4 | En reparto - última milla |
| ENTREGADO | 5 | Entregado al cliente |
| INTENTO_FALLIDO | 6 | Intento fallido de entrega |

```
PATCH {{baseUrlGateway}}/api/v1/logistica-envios/envios/1/estado
Authorization: Bearer eyJ...
```

```json
{
  "nuevoEstadoId": 2,
  "observacion": "Paquete despachado desde bodega central"
}
```

**Response (HistorialEnvio):**
```json
{
  "id": 1,
  "envioId": 1,
  "estado": { "id": 2, "nombre": "DESPACHADO" },
  "fechaActualizacion": "2026-06-27T14:00:00",
  "observacion": "Paquete despachado desde bodega central"
}
```

> Repetir para avanzar: 2→3 (EN_TRANSITO), 3→4 (EN_REPARTO), 4→5 (ENTREGADO).

---

## 19. Historial del Envío

```
GET {{baseUrlGateway}}/api/v1/logistica-envios/envios/1/historial
Authorization: Bearer eyJ...
```

**Response:**
```json
[
  { "estado": { "nombre": "DESPACHADO" }, "observacion": "Paquete despachado..." },
  { "estado": { "nombre": "EN_TRANSITO" }, "observacion": "En tránsito..." },
  { "estado": { "nombre": "EN_REPARTO" }, "observacion": "Última milla..." },
  { "estado": { "nombre": "ENTREGADO" }, "observacion": "Entregado al cliente" }
]
```

---

## Resumen del Flujo

| # | Paso | Método | Endpoint |
|---|------|--------|----------|
| 1 | Registro | POST | `/api/usuarios/registro` |
| 2 | Login | POST | `/api/sesion/login` |
| 3 | Crear producto | POST | `/api/catalogo` |
| 4 | Ingresar stock | POST | `/api/inventario/ingresar` |
| 5 | Agregar al carrito | POST | `/api/carrito` |
| 6 | Seleccionar envío | PUT | `/api/carrito/{id}/envio` |
| 7 | Seleccionar pago | PUT | `/api/carrito/{id}/pago` |
| 8 | Checkout (devuelve carritoId) | POST | `/api/carrito/{id}/checkout` |
| 9 | Generar pedido (PENDIENTE) | POST | `/api/pedidos/generar/{cliId}/{carritoId}` |
| 10 | Iniciar pago (lee método del pedido) | POST | `/api/pagos/iniciar?pedidoId=X` |
| 11 | Cupón (opcional) | POST | `/api/pagos/{id}/cupon/{cuponId}` |
| 12 | Transbank (auto: CONFIRMADO+EN_PREP) | POST | `/api/pagos/{id}/transbank?token=...` |
| 13 | **Solo manual:** CONFIRMADO | PUT | `/api/pedidos/{id}/estado/2` |
| 14 | **Solo manual:** EN_PREPARACION | PUT | `/api/pedidos/{id}/estado/3` |
| 15 | ENVIADO | PUT | `/api/pedidos/{id}/estado/4` |
| 16 | Crear envío (auto) | POST | `/api/v1/logistica-envios/envios/auto/{pedidoId}` |
| 17 | Consultar envío | GET | `/api/v1/logistica-envios/envios/{id}` |
| 18 | Avanzar estado envío | PATCH | `/api/v1/logistica-envios/envios/{id}/estado` |
| 19 | Historial envío | GET | `/api/v1/logistica-envios/envios/{id}/historial` |

> **Automático (Transbank con método automático):** Pasos 1→12 → salta a 15.
> **Manual (Transferencia, Efectivo, Contra Entrega):** Pasos 1→12 → 13 → 14 → 15.
