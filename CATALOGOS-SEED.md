# CATALOGOS - Seed SQL

Script único de seed para poblar todos los catálogos del sistema.
Ejecutar una sola vez contra MySQL con `mysql -u root -p < CATALOGOS-SEED.md`.

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- usuarios_db — registro-usuarios-service
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
CREATE DATABASE IF NOT EXISTS usuarios_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE usuarios_db;

-- Roles
INSERT INTO rol (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema con acceso total'),
('CLIENTE', 'Usuario comprador registrado'),
('VENDEDOR', 'Vendedor o personal de tienda'),
('SOPORTE', 'Personal de soporte al cliente'),
('GERENTE', 'Gerente de sucursal'),
('REPARTIDOR', 'Repartidor / personal de logística');

-- Estados de Perfil
INSERT INTO estado_perfil (nombre) VALUES
('ACTIVO'),
('INACTIVO'),
('SUSPENDIDO'),
('PENDIENTE_VERIFICACION'),
('BANEADO');

-- Permisos
INSERT INTO permiso (nombre, descripcion) VALUES
('LEER_PRODUCTOS', 'Visualizar productos del catálogo'),
('CREAR_PRODUCTOS', 'Crear nuevos productos'),
('EDITAR_PRODUCTOS', 'Modificar productos existentes'),
('ELIMINAR_PRODUCTOS', 'Eliminar productos'),
('GESTIONAR_INVENTARIO', 'Administrar stock e inventario'),
('LEER_USUARIOS', 'Visualizar usuarios del sistema'),
('CREAR_USUARIOS', 'Crear nuevos usuarios'),
('EDITAR_USUARIOS', 'Modificar datos de usuarios'),
('ELIMINAR_USUARIOS', 'Eliminar usuarios del sistema'),
('GESTIONAR_ROLES', 'Administrar roles y permisos'),
('GESTIONAR_PEDIDOS', 'Administrar pedidos'),
('PROCESAR_PAGOS', 'Procesar pagos y reembolsos'),
('GESTIONAR_ENVIOS', 'Administrar envíos y logística'),
('VER_REPORTES', 'Visualizar reportes y analíticas'),
('GENERAR_REPORTES', 'Generar reportes del sistema'),
('GESTIONAR_TICKETS', 'Administrar tickets de soporte'),
('GESTIONAR_TIENDAS', 'Administrar sucursales y tiendas'),
('GESTIONAR_TAREAS', 'Administrar tareas del personal');

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- catalogo_db — catalogo-inventario-service
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
CREATE DATABASE IF NOT EXISTS catalogo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE catalogo_db;

-- Categorías de Producto
INSERT INTO categoria_producto (nombre) VALUES
('Electrónica'),
('Computación'),
('Smartphones'),
('Audio y Video'),
('Hogar'),
('Cocina'),
('Jardín'),
('Deportes'),
('Ropa y Accesorios'),
('Salud y Belleza'),
('Juguetes'),
('Mascotas'),
('Libros'),
('Oficina'),
('Automotriz');

-- Estados de Disponibilidad
INSERT INTO estado_disponibilidad (nombre) VALUES
('DISPONIBLE'),
('AGOTADO'),
('POR_ENCARGAR'),
('DESCONTINUADO'),
('PROXIMAMENTE');

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- pedidos_db — pedido-service
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
CREATE DATABASE IF NOT EXISTS pedidos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pedidos_db;

-- Estados de Pedido
INSERT INTO estado_pedido (nombre) VALUES
('PENDIENTE'),
('CONFIRMADO'),
('EN_PREPARACION'),
('ENVIADO'),
('EN_TRANSITO'),
('ENTREGADO'),
('CANCELADO'),
('REEMBOLSADO'),
('DEVUELTO');

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- proceso_pago_db — proceso-pago-service
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
CREATE DATABASE IF NOT EXISTS proceso_pago_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE proceso_pago_db;

-- Métodos de Pago
INSERT INTO metodo_pago_transaccion (nombre) VALUES
('Tarjeta de Crédito'),
('Tarjeta de Débito'),
('Transferencia Bancaria'),
('PayPal'),
('Webpay'),
('Mercado Pago'),
('Criptomonedas'),
('Pago en Efectivo'),
('Contra Entrega');

-- Estados de Pago
INSERT INTO estado_pago (nombre) VALUES
('PENDIENTE'),
('PROCESANDO'),
('APROBADO'),
('RECHAZADO'),
('REEMBOLSADO'),
('REVISION'),
('CANCELADO');

-- Cupones de Descuento
INSERT INTO cupon_descuento (codigo, porcentaje_descuento, monto_maximo_descuento, fecha_expiracion, activo) VALUES
('BIENVENIDO10', 10.0, 5000.0, '2027-12-31 23:59:59', TRUE),
('PRIMERACOMPRA', 15.0, 10000.0, '2027-12-31 23:59:59', TRUE),
('CYBERLUNES25', 25.0, 25000.0, '2026-12-31 23:59:59', TRUE),
('NAVIDAD20', 20.0, 15000.0, '2026-12-31 23:59:59', TRUE),
('ENVIOGRATIS', 0.0, 0.0, '2027-12-31 23:59:59', TRUE);

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- envio_db — logistica-envios-service
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
CREATE DATABASE IF NOT EXISTS envio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE envio_db;

-- Métodos de Envío
INSERT INTO metodo_envio (nombre, costo) VALUES
('Despacho a domicilio', 4990.0),
('Retiro en tienda', 0.0),
('Despacho express (24 hrs)', 7990.0),
('Envío internacional', 25000.0),
('Punto de retiro', 0.0);

-- Estados de Envío
INSERT INTO estado_envio (nombre) VALUES
('PREPARANDO'),
('DESPACHADO'),
('EN_TRANSITO'),
('EN_REPARTO'),
('ENTREGADO'),
('INTENTO_FALLIDO'),
('DEVUELTO_AL_REMITENTE'),
('CANCELADO'),
('EN_ESPERA_RETIRO'),
('RETIRADO');

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- soporte_db — soporte-service
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
CREATE DATABASE IF NOT EXISTS soporte_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE soporte_db;

-- Estados de Ticket
INSERT INTO estado_ticket (nombre) VALUES
('ABIERTO'),
('EN_PROGRESO'),
('DERIVADO'),
('RESUELTO'),
('CERRADO'),
('REABIERTO');

-- Categorías de Ticket
INSERT INTO categoria_ticket (nombre) VALUES
('Problema técnico'),
('Consulta de producto'),
('Problema de pago'),
('Problema de envío'),
('Devolución'),
('Reclamo'),
('Sugerencia'),
('Otro');

-- Canales de Notificación
INSERT INTO canal_notificacion (nombre) VALUES
('Email'),
('SMS'),
('Push Notification'),
('WhatsApp'),
('App Interna');

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- tienda_db — gestion-tienda-service
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
CREATE DATABASE IF NOT EXISTS tienda_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tienda_db;

-- Estados de Tarea Personal
INSERT INTO estado_tarea_personal (nombre) VALUES
('PENDIENTE'),
('ASIGNADA'),
('EN_PROGRESO'),
('COMPLETADA'),
('CANCELADA'),
('VENCIDA');

-- Sucursales (sucursal)
INSERT INTO sucursal (nombre, direccion, telefono, gerente_cargo_id, activa, fecha_inauguracion) VALUES
('Casa Matriz Santiago', 'Av. Providencia 1234, Santiago', '+56212345678', NULL, TRUE, '2024-01-15 09:00:00'),
('Sucursal Viña del Mar', 'Calle Álvarez 567, Viña del Mar', '+56298765432', NULL, TRUE, '2024-03-20 10:00:00'),
('Sucursal Concepción', 'Av. Paicaví 890, Concepción', '+56412345678', NULL, TRUE, '2024-06-10 10:00:00'),
('Sucursal La Serena', 'Av. Francisco de Aguirre 234, La Serena', '+56512345678', NULL, TRUE, '2024-09-05 10:00:00'),
('Sucursal Temuco', 'Av. Alemania 456, Temuco', '+56452345678', NULL, TRUE, '2025-01-20 10:00:00');

-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- analitica_db — analica-service
-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
CREATE DATABASE IF NOT EXISTS analitica_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE analitica_db;

-- Tipos de Reporte
INSERT INTO tipo_reporte (nombre) VALUES
('Reporte de Usuarios'),
('Reporte de Pedidos'),
('Reporte de Inventario'),
('Reporte de Pagos'),
('Reporte de Carrito'),
('Reporte de Soporte'),
('Reporte de Envíos');

-- Estados de Reporte
INSERT INTO estado_reporte (nombre) VALUES
('SOLICITADO'),
('EN_GENERACION'),
('COMPLETADO'),
('FALLIDO'),
('CANCELADO');

-- Niveles de Alerta
INSERT INTO nivel_alerta (nombre) VALUES
('INFO'),
('WARNING'),
('ERROR'),
('CRITICO');

-- Estados de Respaldo
INSERT INTO estado_respaldo (nombre) VALUES
('EN_PROGRESO'),
('COMPLETADO'),
('FALLIDO'),
('RESTAURADO');
