# API Flow — Ecomarket

Flujo completo de compra: registro → login → producto → inventario → dirección → carrito → pedido → pago → envío.

**Base URL Gateway:** `{{baseUrlGateway}} = http://localhost:8080`

**Autenticación:** Usar `Authorization: Bearer {{token}}` en todas las peticiones autenticadas (el token se obtiene del login y se asigna automáticamente via Postman).

---

## 1. Registrar Usuario

```
POST {{baseUrlGateway}}/api/usuarios/registro
```

```json
{
  "nombre": "admin",
  "correo": "admin@example.com",
  "contrasenaInicial": "MiPassword123",
  "telefono": "+56931771971",
  "rolId": 1,
  "estadoPerfilId": 1
}
```

**Response:**
```json
{
  "id": 1,
  "nombre": "admin",
  "correo": "admin@example.com",
  "rol": { "id": 1, "nombre": "ADMIN" },
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
  "correo": "admin@example.com",
  "contrasena": "MiPassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "usuarioId": 1,
  "correo": "admin@example.com",
  "rol": "ADMIN",
  "expiracionMs": 86400000
}
```

> Postman guarda automáticamente `token` como variable global para las siguientes peticiones.

---

## 3. Recuperar Contraseña (Opcional)

```
POST {{baseUrlGateway}}/api/sesion/recuperar
```

```json
{
  "correo": "admin@example.com"
}
```

---

## 4. Crear Producto (Admin)

```
POST {{baseUrlGateway}}/api/catalogo
```

```json
{
  "sku": "PROD-001",
  "nombre": "Notebook Gamer X100",
  "descripcion": "Notebook de alto rendimiento para gaming",
  "precioBase": 899990.0,
  "categoriaId": 2,
  "estadoId": 1,
  "imagenUrl": "https://ejemplo.com/notebook.jpg"
}
```

**Response:**
```json
{
  "id": 1,
  "sku": "PROD-001",
  "nombre": "Notebook Gamer X100",
  "precioBase": 899990.0,
  "categoria": { "id": 2, "nombre": "Computación" },
  "estado": { "id": 1, "nombre": "DISPONIBLE" }
}
```

---

## 5. Ingresar Stock Global (Admin)

Stock global disponible para todas las sucursales.

```
POST {{baseUrlGateway}}/api/inventario/ingresar
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

## 6. Asignar Stock a Sucursal (Admin)

Transfiere stock del inventario global a una sucursal específica.

```
POST {{baseUrlGateway}}/api/inventario/transferir
```

```json
{
  "productoId": 1,
  "sucursalId": 1,
  "cantidad": 20
}
```

---

## 7. Agregar Dirección del Usuario

**Admin** (especifica usuarioId en la ruta):
```
POST {{baseUrlGateway}}/api/usuarios/direcciones/{usuarioId}
```

**Cliente** (sin ID en la ruta, se toma del header X-User-Id):
```
POST {{baseUrlGateway}}/api/usuarios/direcciones
```

```json
{
  "calle": "Av. Providencia",
  "numero": "1234",
  "departamento": "Depto 501",
  "ciudad": "Santiago",
  "region": "Región Metropolitana",
  "codigoPostal": "7500000",
  "destinatario": "Juan Pérez",
  "esPredeterminada": true
}
```

---

## 8. Agregar Producto al Carrito

El `usuarioId` se toma automáticamente del header `X-User-Id`.

```
POST {{baseUrlGateway}}/api/carrito
```

```json
{
  "productoId": 1,
  "cantidad": 2
}
```

**Response:**
```json
{
  "id": 1,
  "clienteId": 1,
  "subtotal": 1799980.0,
  "metodoEnvioId": null,
  "metodoPagoId": null,
  "activo": true,
  "cerrado": false,
  "items": [
    {
      "id": 1,
      "productoId": 1,
      "cantidad": 2,
      "precioUnitario": 899990.0,
      "subtotal": 1799980.0
    }
  ]
}
```

> Guardar `id` del carrito como `carritoId`.

---

## 9. Seleccionar Método de Envío

```
PUT {{baseUrlGateway}}/api/carrito/{usuarioId}/envio
```

```json
{ "id": 1 }
```

> `id` = id del método de envío (1 = Despacho a domicilio, 2 = Retiro en tienda).

**Response:** Carrito actualizado con `metodoEnvioId` asignado.

---

## 10. Seleccionar Método de Pago

```
PUT {{baseUrlGateway}}/api/carrito/pago
```

```json
{ "id": 1 }
```

> `id` = id del método de pago (1 = Webpay, etc.).

**Response:** Carrito actualizado con `metodoPagoId` asignado.

---

## 11. Verificar Carrito y Generar Pedido

Valida que el carrito tenga método de envío, método de pago, items y dirección predeterminada. Si todo está correcto, genera el pedido.

**Usar dirección predeterminada:**
```
POST {{baseUrlGateway}}/api/pedidos/generar
```

**Usar dirección específica (no predeterminada):**
```
POST {{baseUrlGateway}}/api/pedidos/generar/{direccionId}
```

> El `clienteId` se obtiene del header `X-User-Id`. No requiere body.

**Response:**
```json
{
  "id": 1,
  "clienteId": 1,
  "subtotal": 1799980.0,
  "total": 1799980.0,
  "estado": { "id": 1, "nombre": "PENDIENTE" },
  "fechaCreacion": "2026-06-27T12:20:00"
}
```

> Guardar `id` del pedido como `pedidoId`.

---

## 12. Revisar Pedido

```
GET {{baseUrlGateway}}/api/pedidos/{pedidoId}
```

---

## 13. Iniciar Pago

El método de pago se lee automáticamente del pedido.

```
POST {{baseUrlGateway}}/api/pagos/iniciar?pedidoId={pedidoId}
```

**Response:**
```json
{
  "id": 1,
  "pedidoId": 1,
  "montoSubtotal": 1799980.0,
  "montoDescuento": 0.0,
  "montoTotal": 1799980.0,
  "metodoPago": { "id": 1, "nombre": "Webpay" },
  "estado": { "id": 1, "nombre": "PENDIENTE" },
  "tokenTransbank": "TB-1A2B3C4D",
  "fechaInicio": "2026-06-27T12:22:00"
}
```

> Guardar `id` de la transacción como `transaccionId`.

---

## 14. Aplicar Cupón de Descuento (Opcional)

```
POST {{baseUrlGateway}}/api/pagos/{transaccionId}/cupon/{cuponId}
```

**Path Variables:** `transaccionId=1`, `cuponId=1` (BIENVENIDO10 = 10%)

**Response:**
```json
{
  "id": 1,
  "montoSubtotal": 1799980.0,
  "montoDescuento": 179998.0,
  "montoTotal": 1619982.0,
  "estado": { "id": 1, "nombre": "PENDIENTE" },
  "cuponUtilizadoId": 1
}
```

---

## 15. Confirmar Pago (Transbank)

```
POST {{baseUrlGateway}}/api/pagos/{transaccionId}/transbank?token={codigoTransbank}
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
  "montoSubtotal": 1799980.0,
  "montoDescuento": 179998.0,
  "montoTotal": 1619982.0,
  "estado": { "id": 3, "nombre": "APROBADO" },
  "codigoAutorizacion": "AUTH-ABC123",
  "fechaFin": "2026-06-27T12:28:00"
}
```

---

## 16. Actualizar Estado del Pedido (Admin / Repartidor)

```
PUT {{baseUrlGateway}}/api/pedidos/{pedidoId}/estado/{estadoId}
```

| Estado | ID |
|--------|----|
| PENDIENTE | 1 |
| CONFIRMADO | 2 |
| EN_PREPARACION | 3 |
| ENVIADO | 4 |
| ENTREGADO | 5 |
| CANCELADO | 6 |
| RECHAZADO | 7 |

Ejemplo — cambiar a ENVIADO:
```
PUT {{baseUrlGateway}}/api/pedidos/{pedidoId}/estado/4
```

> Al cambiar a ENVIADO se refleja automáticamente en logistica-envios-service.

---

## 17. Consultar Envío por ID de Pedido

```
GET {{baseUrlGateway}}/api/v1/logistica-envios/envios/pedido/{pedidoId}
```

---

## 18. Actualizar Estado del Envío (Admin / Repartidor)

```
PATCH {{baseUrlGateway}}/api/v1/logistica-envios/envios/{envioId}/estado/{estadoId}
```

| Estado | ID |
|--------|----|
| PREPARANDO | 1 |
| DESPACHADO | 2 |
| EN_TRANSITO | 3 |
| EN_REPARTO | 4 |
| ENTREGADO | 5 |
| INTENTO_FALLIDO | 6 |

> El cambio de estado del envío afecta al estado del pedido cuando corresponde (EN_TRANSITO → pedido EN_TRANSITO, ENTREGADO → pedido ENTREGADO).

---

## 19. Soporte — Tickets (Opcional)

### 19.1 Ingresar Ticket (Cliente)

```
POST {{baseUrlGateway}}/api/v1/soporte/ingresar-ticket
```

```json
{
  "categoriaId": 1,
  "asunto": "Problema con mi pedido",
  "pedidoId": 1
}
```

> `clienteId` se obtiene del header `X-User-Id`. `pedidoId` es opcional.

### 19.2 Asignar Ticket (Admin / Soporte)

```
PATCH {{baseUrlGateway}}/api/v1/soporte/tickets/{ticketId}/asignar/{empleadoId}
```

### 19.3 Enviar Mensaje (Admin / Soporte / Cliente)

```
POST {{baseUrlGateway}}/api/v1/soporte/enviar-mensaje-chat
```

```json
{
  "ticketId": 1,
  "contenido": "Hola, necesito ayuda con mi pedido"
}
```

> `remitenteId` se obtiene del header `X-User-Id`, `esCliente` se deriva del header `X-User-Roles`.

### 19.4 Ver Mensajes del Ticket

```
GET {{baseUrlGateway}}/api/v1/soporte/tickets/{ticketId}/mensajes
```

> Marca automáticamente como leídos los mensajes del otro lado.

### 19.5 Solucionar Ticket (Admin / Soporte)

```
PATCH {{baseUrlGateway}}/api/v1/soporte/tickets/{ticketId}/solucionar
```

```json
"Resumen de la solución aplicada al ticket"
```

### 19.6 Cerrar Ticket (Admin / Soporte / Cliente)

```
PATCH {{baseUrlGateway}}/api/v1/soporte/tickets/{ticketId}/cerrar
```

> Cliente cierra solo tickets propios. Empleado cierra solo tickets asignados. Admin cierra cualquier ticket.

---

## Resumen del Flujo

| # | Paso | Método | Endpoint |
|---|------|--------|----------|
| 1 | Registro | POST | `/api/usuarios/registro` |
| 2 | Login | POST | `/api/sesion/login` |
| 3 | Recuperar contraseña (opcional) | POST | `/api/sesion/recuperar` |
| 4 | Crear producto | POST | `/api/catalogo` |
| 5 | Ingresar stock | POST | `/api/inventario/ingresar` |
| 6 | Transferir stock a sucursal | POST | `/api/inventario/transferir` |
| 7 | Agregar dirección | POST | `/api/usuarios/direcciones` |
| 8 | Agregar al carrito | POST | `/api/carrito` |
| 9 | Seleccionar envío | PUT | `/api/carrito/{id}/envio` |
| 10 | Seleccionar pago | PUT | `/api/carrito/pago` |
| 11 | Generar pedido | POST | `/api/pedidos/generar` |
| 12 | Revisar pedido | GET | `/api/pedidos/{id}` |
| 13 | Iniciar pago | POST | `/api/pagos/iniciar?pedidoId=X` |
| 14 | Cupón (opcional) | POST | `/api/pagos/{id}/cupon/{cuponId}` |
| 15 | Transbank | POST | `/api/pagos/{id}/transbank?token=...` |
| 16 | Actualizar estado pedido | PUT | `/api/pedidos/{id}/estado/{estadoId}` |
| 17 | Consultar envío por pedido | GET | `/api/v1/logistica-envios/envios/pedido/{pedidoId}` |
| 18 | Actualizar estado envío | PATCH | `/api/v1/logistica-envios/envios/{id}/estado/{estadoId}` |
| 19 | Ingresar ticket | POST | `/api/v1/soporte/ingresar-ticket` |
| 20 | Asignar ticket | PATCH | `/api/v1/soporte/tickets/{id}/asignar/{empleadoId}` |
| 21 | Mensaje chat | POST | `/api/v1/soporte/enviar-mensaje-chat` |
| 22 | Ver mensajes | GET | `/api/v1/soporte/tickets/{id}/mensajes` |
| 23 | Solucionar ticket | PATCH | `/api/v1/soporte/tickets/{id}/solucionar` |
| 24 | Cerrar ticket | PATCH | `/api/v1/soporte/tickets/{id}/cerrar` |

> **Automático (Transbank con método automático):** Pasos 1→15 → salta a 16.
> **Manual (Transferencia, Efectivo, Contra Entrega):** Pasos 1→15 → requiere confirmación manual de pedido antes del paso 16.
