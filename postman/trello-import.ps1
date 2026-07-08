<#
.SYNOPSIS
    Importa el tablero Ecomarket a Trello con estructura Kanban profesional.
.DESCRIPTION
    Crea el board, lists, labels, cards con checklists y referencias a commits.
    Basado en analisis completo del proyecto: 11 servicios, 253+ tests, 216 endpoints.
.PARAMETER ApiKey
    Tu API Key de Trello (https://trello.com/app-key)
.PARAMETER Token
    Tu Token de Trello (generado desde la misma pagina)
.PARAMETER BoardName
    Nombre del board. Default: "Ecomarket - Microservicios"
.EXAMPLE
    .\trello-import.ps1 -ApiKey "xxx" -Token "yyy"
.NOTES
    Requiere PowerShell 5.1+. Cada tarjeta representa una feature completa, NO un commit individual.
#>

param(
    [Parameter(Mandatory = $true)]
    [string]$ApiKey,
    [Parameter(Mandatory = $true)]
    [string]$Token,
    [string]$BoardName = "Ecomarket - Microservicios"
)

$baseUrl = "https://api.trello.com/1"
$auth = "key=$ApiKey&token=$Token"

# --- Funciones auxiliares ---

function Invoke-Trello {
    param([string]$Method, [string]$Endpoint, [hashtable]$Body = @{})
    $url = "$baseUrl$Endpoint" + "?" + $auth
    if ($Body.Count -gt 0) {
        $jsonBody = ($Body | ConvertTo-Json)
        $resp = Invoke-RestMethod -Uri $url -Method $Method -Body $jsonBody -ContentType "application/json"
    } else {
        $resp = Invoke-RestMethod -Uri $url -Method $Method
    }
    return $resp
}

function New-TrelloList {
    param([string]$BoardId, [string]$Name, [int]$Pos)
    return Invoke-Trello -Method Post -Endpoint "/boards/$BoardId/lists" -Body @{
        name = $Name; pos = $Pos
    }
}

function New-TrelloLabel {
    param([string]$BoardId, [string]$Name, [string]$Color)
    return Invoke-Trello -Method Post -Endpoint "/boards/$BoardId/labels" -Body @{
        name = $Name; color = $Color
    }
}

function New-TrelloCard {
    param([string]$ListId, [string]$Name, [string]$Desc, [string[]]$LabelIds)
    $body = @{ name = $Name; desc = $Desc; pos = "bottom" }
    if ($LabelIds -and $LabelIds.Count -gt 0) {
        $body.idLabels = ($LabelIds -join ",")
    }
    return Invoke-Trello -Method Post -Endpoint "/lists/$ListId/cards" -Body $body
}

function Add-TrelloChecklist {
    param([string]$CardId, [string]$Name, [string[]]$Items)
    $cl = Invoke-Trello -Method Post -Endpoint "/cards/$CardId/checklists" -Body @{ name = $Name }
    foreach ($item in $Items) {
        Invoke-Trello -Method Post -Endpoint "/checklists/$($cl.id)/checkItems" -Body @{ name = $item }
    }
}

# --- 1. Crear Board ---

Write-Host "Creando board: $BoardName ..." -ForegroundColor Cyan
$board = Invoke-Trello -Method Post -Endpoint "/boards" -Body @{
    name = $BoardName
    desc = "Gestion agil del proyecto Ecomarket - 11 microservicios Spring Boot 4.0.7 con JWT, Gateway, 32 controladores, 216 endpoints, 253+ tests y cobertura 100% en 9/11 servicios"
    defaultLabels = $false
    defaultLists = $false
    prefs_permissionLevel = "private"
    prefs_background = "blue"
}
$boardId = $board.id
Write-Host "  Board ID: $boardId" -ForegroundColor Green

# --- 2. Crear Labels ---

Write-Host "Creando labels ..." -ForegroundColor Cyan
$labels = @{}
$labelDefs = @(
    @{name = "feat";       color = "green";       desc = "Nueva funcionalidad"},
    @{name = "fix";        color = "red";         desc = "Correccion de bug"},
    @{name = "refactor";   color = "yellow";      desc = "Reestructuracion sin cambiar comportamiento"},
    @{name = "tests";      color = "lime";        desc = "Tests y cobertura"},
    @{name = "docs";       color = "sky";         desc = "Documentacion"},
    @{name = "chore";      color = "orange";      desc = "Tareas de mantenimiento"},
    @{name = "infra";      color = "purple";      desc = "CI/CD, Docker, gateway"},
    @{name = "security";   color = "pink";        desc = "Autenticacion, JWT, roles"},
    @{name = "bug";        color = "red";         desc = "Bug reportado"}
)
foreach ($ld in $labelDefs) {
    $l = New-TrelloLabel -BoardId $boardId -Name $ld.name -Color $ld.color
    $labels[$ld.name] = $l.id
}
Write-Host "  $($labelDefs.Count) labels creados" -ForegroundColor Green

# --- 3. Crear Lists en orden ---

Write-Host "Creando lists ..." -ForegroundColor Cyan
$lists = @{}
$listDefs = @(
    @{name = "1- Product Backlog";        pos = 1},
    @{name = "2- To Do (Sprint Backlog)"; pos = 2},
    @{name = "3- En Progreso (WIP: 2)";   pos = 3},
    @{name = "4- En Revision / QA";       pos = 4},
    @{name = "5- Hecho (Done)";           pos = 5},
    @{name = "6- Bugs";                   pos = 6}
)
foreach ($ld in $listDefs) {
    $l = New-TrelloList -BoardId $boardId -Name $ld.name -Pos $ld.pos
    $lists[$ld.name] = $l.id
}
Write-Host "  $($listDefs.Count) lists creados" -ForegroundColor Green

# --- 4. Crear Cards en "Hecho (Done)" ---

Write-Host "Creando cards en Hecho (Done) ..." -ForegroundColor Cyan
$doneListId = $lists["5- Hecho (Done)"]

# --- Card 1: Fundacion - Estructura Inicial ---
$card = New-TrelloCard -ListId $doneListId -Name "Fundacion: Estructura inicial del proyecto (11 microservicios)" -Desc @"
**Objetivo:** Crear el esqueleto del proyecto con todos los microservicios, configuracion base y BD.

**Microservicios:**
- api-gateway (8080) - Spring Cloud Gateway 2024.0.0
- registro-usuarios-service (8081) - Spring Boot 4.0.7
- carrito-compra-service (8082) - Spring Boot 4.0.7
- logistica-envios-service (8083) - Spring Boot 4.0.7
- analica-service (8084) - Spring Boot 4.0.7
- proceso-pago-service (8085) - Spring Boot 4.0.7
- iniciosesion-service (8086) - Spring Boot 4.0.7
- catalogo-inventario-service (8087) - Spring Boot 4.0.7
- soporte-service (8088) - Spring Boot 4.0.7
- pedido-service (8089) - Spring Boot 4.0.7
- gestion-tienda-service (8090) - Spring Boot 4.0.7

**Commits:** a7cce7b, e2c2a8c, f086bbc
**Logros:**
- Pom.xml raiz para deteccion multi-modulo
- MySQL con auto-create database (spring.jpa.hibernate.ddl-auto=update)
- 9 schemas MySQL + 1 H2 (pedido-service)
- Pruebas unitarias iniciales en todos los servicios
"@ -LabelIds @($labels["chore"].id)

Add-TrelloChecklist -CardId $card.id -Name "Servicios configurados" -Items @(
    "api-gateway - Spring Cloud Gateway 4.0.7 - Java 21",
    "registro-usuarios-service - Spring Boot 4.0.7 - Java 25",
    "carrito-compra-service - Spring Boot 4.0.7 - Java 25",
    "logistica-envios-service - Spring Boot 4.0.7 - Java 25",
    "analica-service - Spring Boot 4.0.7 - Java 25",
    "proceso-pago-service - Spring Boot 4.0.7 - Java 25",
    "iniciosesion-service - Spring Boot 4.0.7 - Java 25",
    "catalogo-inventario-service - Spring Boot 4.0.7 - Java 25",
    "soporte-service - Spring Boot 4.0.7 - Java 25",
    "pedido-service - Spring Boot 4.0.7 - Java 25",
    "gestion-tienda-service - Spring Boot 4.0.7 - Java 25"
)

# --- Card 2: Autenticacion JWT ---
$card = New-TrelloCard -ListId $doneListId -Name "Seguridad: Autenticacion JWT + RBAC + 4 roles" -Desc @"
**Objetivo:** Implementar autenticacion stateless con JWT, separando registro de usuarios (creacion de cuentas) de inicio de sesion (emision de tokens).

**Commits:** 24d9148, fe84c30, e63cdbb

**Arquitectura:**
- registro-usuarios-service (8081): crea usuarios, perfiles, direcciones, roles, permisos
- iniciosesion-service (8086): login, emision/validacion JWT, logout (blacklist), recuperacion de contrasena
- api-gateway (JwtAuthenticationFilter): filtro global que valida tokens y aplica RBAC

**Roles implementados:**
- ROLE_ADMIN: acceso total a todos los endpoints
- ROLE_SOPORTE: acceso completo a soporte (equivalente admin en ese dominio)
- ROLE_CLIENTE: restringido a sus propios recursos (X-User-Id)
- ROLE_REPARTIDOR: solo cambio de estado de pedidos y envios

**Flujo:**
1. Usuario se registra -> registro-usuarios llama a iniciosesion para crear credencial
2. Usuario hace login -> iniciosesion valida y emite JWT (HS256, jjwt 0.12.5)
3. Gateway recibe request -> valida JWT contra iniciosesion -> inyecta X-User-Id y X-User-Roles
4. Servicio destino recibe headers y aplica logica segun el rol
"@ -LabelIds @($labels["security"].id, $labels["feat"].id)

Add-TrelloChecklist -CardId $card.id -Name "Componentes de seguridad" -Items @(
    "JwtAuthenticationFilter en gateway (AbstractGatewayFilterFactory)",
    "LoginCuentaServiceImpl - BCrypt + JWT generation",
    "JwtUtil - generacion y validacion HMAC-SHA",
    "Token blacklisting via SesionJWT entity (logout)",
    "Recovery token system con expiracion de 2h",
    "JwtAuthFilter en iniciosesion-service (Spring Security)",
    "SecurityConfig con cadena de filtros",
    "Endpoints publicos: login, registro, credencial"
)

# --- Card 3: CI/CD ---
$card = New-TrelloCard -ListId $doneListId -Name "Infra: Pipeline CI/CD (GitHub Actions) con matrix build" -Desc @"
**Objetivo:** Pipeline de integracion continua que compile, ejecute tests y publique cobertura para los 11 microservicios en paralelo.

**Commits:** 577e3d5, 9d08cb9, d09cdc8

**Caracteristicas:**
- Build matrix con 11 servicios, cada uno con su working-directory
- fail-fast: false para visibilidad completa de fallos
- Java 21 (api-gateway) + Java 25 (resto) via matrix
- ByteBuddy 1.17.0 para compatibilidad con Java 25
- application-test.properties para entorno CI (sin depender de variables de entorno)
- JWT_SECRET inyectado como secret de GitHub
- JaCoCo para reporte de cobertura (9/11 servicios)
- Maven cache para builds mas rapidos
"@ -LabelIds @($labels["infra"].id)

# --- Card 4: Refactor para 100% coverage ---
$card = New-TrelloCard -ListId $doneListId -Name "Refactor masivo: 100% coverage con logica corregida inter-servicios" -Desc @"
**Objetivo:** Refactorizar la logica de negocio de cada microservicio para correcta comunicacion entre ellos y alcanzar 100% de cobertura de tests.

**Commits:** 5a6b86b, 9c7f73a, 801b9a5, 32c0de4, 298359f, 68cf2e6, 428c2aa, 7d6cb48, f0310a2, 8240dde, 0b69203, 5a2a0ea

**Cambios principales:**
- Comunicacion inter-servicios via RestTemplate con manejo de errores
- Validacion cruzada (pedido valida cliente, stock, carrito, etc.)
- DTOs alineados entre servicios (80+ DTOs)
- Excepciones personalizadas con GlobalExceptionHandler
- Eliminacion de imports y codigo muerto
- Fix de bugs en flujo de pago (cupones, Transbank)

**Estado actual: 253 tests, 32 controladores, 216 endpoints**
"@ -LabelIds @($labels["refactor"].id, $labels["tests"].id)

Add-TrelloChecklist -CardId $card.id -Name "Cobertura 100% alcanzada" -Items @(
    "api-gateway - 4 tests, 100% coverage",
    "catalogo-inventario-service - 21 tests, OK",
    "carrito-compra-service - 19 tests, 100%",
    "pedido-service - 21 tests, 100%",
    "proceso-pago-service - 14 tests, 100%",
    "logistica-envios-service - 34 tests, 100%",
    "soporte-service - 36 tests, 100%",
    "analitica-service - 38 tests, 100%",
    "registro-usuarios-service - 21 tests, 100%",
    "iniciosesion-service - 25 tests, 100%",
    "gestion-tienda-service - 20 tests, OK"
)

# --- Card 5: DTO Tests ---
$card = New-TrelloCard -ListId $doneListId -Name "Tests: 76+ tests unitarios para DTOs (cobertura de serializacion)" -Desc @"
**Objetivo:** Garantizar que todos los DTOs tengan tests de serializacion/deserializacion, construccion y validacion (getters, setters, equals, hashcode).

**Commits:** 70f5fb4, c316bcc

**Logros:**
- 80+ DTOs con tests unitarios en todos los servicios
- Tests de constructor, builder, setter/getter segun la notacion de cada DTO
- Patron mirror: cada clase en src/main tiene su *Test.java en src/test bajo el mismo package
- Incluye DTOs de request, response y DTOs internos de comunicacion

**Distribucion:**
- analica-service: 14 DTO tests
- soporte-service: 10 DTO tests
- logistica-envios-service: 9 DTO tests
- pedido-service: 8 DTO tests
- iniciosesion-service: 10 DTO tests
- catalogo-inventario: 6 DTO tests
- registro-usuarios: 5 DTO tests
- gestion-tienda: 5 DTO tests
- proceso-pago: 1 DTO test
- carrito-compra: 6 DTO tests
"@ -LabelIds @($labels["tests"].id)

# --- Card 6: Upgrade Spring Boot 4.0.7 ---
$card = New-TrelloCard -ListId $doneListId -Name "Chore: Upgrade Spring Boot 4.0.7 + Fix Lombok + POM raiz" -Desc @"
**Objetivo:** Unificar todos los servicios en Spring Boot 4.0.7 y solucionar problemas de compilacion con Lombok y deteccion de modulos.

**Commits:** 5762b28, 552049e, e2c2a8c

**Cambios:**
- Spring Boot 3.4.x -> 4.0.7 en 10 servicios (api-gateway queda en 3.4.13 por compatibilidad Gateway)
- Java 21 para api-gateway, Java 25 para el resto
- maven-compiler-plugin con annotationProcessorPaths para Lombok (fix compilacion)
- Pom.xml raiz creado para deteccion multi-modulo desde IDE
- Imports innecesarios eliminados de tests y produccion
- ByteBuddy 1.17.0 para compatibilidad nativa Java 25
"@ -LabelIds @($labels["chore"].id)

# --- Card 7: Gateway YAML + Swagger ---
$card = New-TrelloCard -ListId $doneListId -Name "Gateway: Migracion a YAML, Swagger/OpenAPI y 25 rutas" -Desc @"
**Objetivo:** Migrar configuracion del gateway de RouteConfiguration.java + application.properties a application.yml. Agregar documentacion interactiva con Swagger.

**Commits:** 768a9d2, a2a3874, bed68a3

**Cambios:**
- 25 rutas definidas en YAML con filtro JwtAuthentication
- RouteConfiguration.java eliminado (codigo redundante)
- application.properties eliminado (todo migrado a YAML)
- Swagger UI en /doc/swagger-ui.html (WebFlux)
- Byte Buddy 1.17.0 para compatibilidad con Java 25

**Rutas del gateway:**
- registro, carrito, envios, analitica, pago, sesion (publica), catalogo, soporte, pedidos, tiendas
- inventario, catalogo-admin, estado-pedido, metodo-pago, estado-pago
- estado-ticket, categoria-ticket, canal-notificacion, resenas, notificaciones
- mensajes-chat, respaldos, reportes, metricas, alertas
"@ -LabelIds @($labels["infra"].id, $labels["refactor"].id)

Add-TrelloChecklist -CardId $card.id -Name "Rutas en YAML" -Items @(
    "registro-service -> 8081 (JwtAuthentication)",
    "carrito-service -> 8082 (JwtAuthentication)",
    "envios-service -> 8083 (JwtAuthentication)",
    "analitica-service -> 8084 (JwtAuthentication)",
    "pago-service -> 8085 (JwtAuthentication)",
    "sesion-service -> 8086 (SIN filtro, publica)",
    "catalogo-service -> 8087 (JwtAuthentication)",
    "soporte-service -> 8088 (JwtAuthentication)",
    "pedidos-service -> 8089 (JwtAuthentication)",
    "tiendas-service -> 8090 (JwtAuthentication)",
    "Mas 15 rutas secundarias"
)

# --- Card 8: JWT externalizado ---
$card = New-TrelloCard -ListId $doneListId -Name "Seguridad: JWT externalizado a variables de entorno + .env" -Desc @"
**Objetivo:** Eliminar el secret JWT hardcodeado del application.properties y externalizarlo a variables de entorno.

**Commits:** 079417e, d8a910c

**Cambios:**
- iniciosesion-service/application.properties: jwt.secret=${JWT_SECRET}, jwt.expiration-ms=${JWT_EXPIRATION_MS}
- iniciosesion-service/.env.example: template para nuevos desarrolladores
- iniciosesion-service/.env: valores por defecto (no ignorado por git por ser educativo)
- registro-usuarios-service/.env.example eliminado (ese servicio no usa JWT)
- .env eliminado del .gitignore raiz
"@ -LabelIds @($labels["security"].id, $labels["refactor"].id)

# --- Card 9: Documentacion ---
$card = New-TrelloCard -ListId $doneListId -Name "Docs: READMEs, API-FLOW, Swagger y documentacion del proyecto" -Desc @"
**Objetivo:** Documentar la arquitectura, flujos y estado del proyecto.

**Commits:** 1b361eb, 5ac83fd, 1d6be84

**Documentos:**
- README.md raiz: arquitectura, stack, instrucciones de inicio, estado de cobertura por servicio
- postman/API-FLOW.md: flujo completo de compra y soporte (24 pasos) actualizado
- postman/CATALOGOS-SEED.md: seed SQL para catalogo
- postman/flujo principal.postman_collection.json: collection de Postman
- README individual de cada servicio
- Swagger UI en api-gateway (/doc/swagger-ui.html)
- OpenAPI/Swagger en pedido-service y soporte-service
"@ -LabelIds @($labels["docs"].id)

Add-TrelloChecklist -CardId $card.id -Name "Documentacion completada" -Items @(
    "README.md raiz actualizado con tecnologias y cobertura",
    "API-FLOW.md con flujo completo de 24 pasos",
    "Postman collection exportada (flujo principal)",
    "11 READMEs de servicio",
    "OpenAPI/Swagger en gateway, pedido y soporte"
)

# --- Card 10: Limpieza General ---
$card = New-TrelloCard -ListId $doneListId -Name "Chore: Limpieza de archivos huerfanos y reorganizacion" -Desc @"
**Objetivo:** Eliminar archivos muertos y reorganizar documentacion en carpetas.

**Commits:** d9484c6, 7b0184a, 881694b, 7cfca21, 079417e

**Archivos eliminados:**
- test-data/ (obsoleto, microservicios se comunican entre si)
- soporte-service/mock-data/db.json (template json-server)
- Dockerfiles obsoletos de analica, logistica-envios y soporte
- RouteConfiguration.java (migrado a YAML)
- application.properties del gateway (migrado a YAML)
- Interfaces de servicio con una unica implementacion (6 eliminadas)

**Archivos movidos:**
- API-FLOW.md, CATALOGOS-SEED.md, flujo principal.postman_collection.json -> postman/

**.gitignore agregados:**
- registro-usuarios-service, iniciosesion-service, pedido-service, proceso-pago-service
"@ -LabelIds @($labels["chore"].id)

# --- Card 11: Soporte Service ---
$card = New-TrelloCard -ListId $doneListId -Name "Feat: Soporte-service completo - tickets, chat, resenas, notificaciones" -Desc @"
**Objetivo:** Implementar modulo de soporte completo: tickets con maquina de estados, chat en vivo, resenas con moderacion, notificaciones push.

**Commits:** 68cf2e6, f0310a2, ce8c4f2, 3cf005d

**Componentes (7 controladores, 28 endpoints):**
- TicketSoporte: CRUD completo + asignacion + solucion + cierre
- MensajeChat: mensajes asociados a tickets con marcado de lectura
- Resena: reseñas de productos con moderacion (aprobar/rechazar)
- Notificacion: notificaciones por destinatario con canales
- EstadoTicket, CategoriaTicket, CanalNotificacion: catalogos de referencia

**Reglas de negocio:**
- Auto-estado al asignar: 1ra asignacion -> EN_PROCESO, reasignacion -> PENDIENTE
- Rechazar asignacion si ya esta RESUELTO o CERRADO
- Bloquear mensajes en tickets CERRADOS (400)
- Ticket state-machine con 5 estados (ABIERTO -> EN_PROCESO -> PENDIENTE -> RESUELTO -> CERRADO)
- Validacion de empleado asignado (roles SOPORTE/ADMIN via usuarios-service)
- 36 tests, 100% coverage
"@ -LabelIds @($labels["feat"].id, $labels["refactor"].id)

Add-TrelloChecklist -CardId $card.id -Name "Componentes de soporte" -Items @(
    "SoporteController - tickets CRUD + estado + asignar + solucionar + cerrar",
    "MensajeChatController - chat por ticket + marcar leido",
    "ResenaController - resenas + aprobar + rechazar",
    "NotificacionController - notificaciones por destinatario",
    "EstadoTicketController - catalogo de estados",
    "CategoriaTicketController - catalogo de categorias",
    "CanalNotificacionController - canales de notificacion"
)

# --- Card 12: Carrito y Pedido ---
$card = New-TrelloCard -ListId $doneListId -Name "Refactor: Carrito-compra + Pedido-service con comunicacion inter-servicios" -Desc @"
**Objetivo:** Flujo de compra completo desde el carrito hasta la generacion de pedido con validacion cruzada.

**Commits:** 9c7f73a, 801b9a5

**Carrito-compra-service (8082, 10 endpoints):**
- Obtencion/creacion de carrito activo por X-User-Id
- Agregar/remover productos con validacion de stock via catalogo-inventario
- Seleccion de metodo de pago (via proceso-pago) y envio (via logistica-envios)
- Cierre de carrito para generar pedido

**Pedido-service (8089, 8 endpoints):**
- POST /api/pedidos/generar - genera pedido desde carrito
- PUT /api/pedidos/{id}/estado/{estadoId} - actualiza estado
- GET /api/pedidos/cliente - historial por X-User-Id
- POST /internal/actualizar-por-envio - recibe actualizaciones de envios
- Auto-creacion de envio en logistica-envios al llegar a CONFIRMADO

**Clientes inter-servicio:**
- RegistroUsuariosClient, CarritoCompraClient, CatalogoInventarioClient, AnaliticaClient
"@ -LabelIds @($labels["refactor"].id, $labels["feat"].id)

# --- Card 13: Pago y Cupones ---
$card = New-TrelloCard -ListId $doneListId -Name "Fix+Feat: Proceso-pago completo - Transbank, cupones, facturas, idempotencia" -Desc @"
**Objetivo:** Sistema de pagos completo con soporte Transbank, cupones de descuento, facturacion, envio de boletas por email e idempotencia.

**Commits:** 66a4c76, b773d95, 32c0de4, a9c0af0

**Funcionalidades:**
- Inicio de pago con idempotency key (evita duplicados)
- Procesamiento Transbank simulado (token-based, rechazo si token=error)
- Cupones de descuento: porcentaje con tope maximo, validacion de estado terminal
- Reembolsos con motivo
- Generacion de factura electronica (RUT + giro)
- Envio de boleta por email
- Actualizacion automatica de estado de pedido (CONFIRMADO/CANCELADO)
- Vaciado de carrito post-pago exitoso

**Estados de transaccion:** PENDIENTE -> APROBADO/RECHAZADO -> REEMBOLSADO
"@ -LabelIds @($labels["fix"].id, $labels["feat"].id)

Add-TrelloChecklist -CardId $card.id -Name "Componentes de pago" -Items @(
    "PagoController - 7 endpoints: iniciar, obtener, cupon, transbank, reembolso, factura, email",
    "Idempotency key para evitar pagos duplicados",
    "CuponDescuento con validacion de porcentaje y tope",
    "TransaccionPago entity con maquina de estados",
    "FacturaElectronica con RUT y giro",
    "RestTemplate a pedido-service y carrito-service"
)

# --- Card 14: Envios ---
$card = New-TrelloCard -ListId $doneListId -Name "Refactor: Logistica-envios completo - rutas, puntos de retiro, historial" -Desc @"
**Objetivo:** Sistema de envios completo con estado por path param, propagacion a pedido, planificacion de rutas y puntos de retiro.

**Commits:** 298359f

**Funcionalidades (5 controladores, 30 endpoints):**
- Creacion de envio manual y automatico (desde pedido al confirmar)
- Actualizacion de estado via PATCH /envios/{id}/estado/{estadoId}
- Cancelacion, recepcion, seleccion de punto de retiro
- Planificacion de rutas de transporte
- Historial de cambios de estado por envio
- CRUD de direcciones, puntos de retiro, metodos de envio, estados de envio

**Integraciones:**
- Notifica a pedido-service via /internal/actualizar-por-envio
- Envia notificaciones push a soporte-service
- Registra metricas en analitica-service

**Estados:** Pendiente -> En Transito -> En Punto Retiro -> Entregado -> Cancelado
"@ -LabelIds @($labels["refactor"].id, $labels["feat"].id)

Add-TrelloChecklist -CardId $card.id -Name "Componentes de envios" -Items @(
    "EnvioController - 14 endpoints (crear, estado, cancelar, recepcion, ruta, historial)",
    "EstadoEnvioController - catalogo de estados",
    "MetodoEnvioController - metodos de envio",
    "PuntoRetiroController - puntos de retiro",
    "DireccionController - direcciones de envio",
    "8 Domain services para cada entidad"
)

# ===== NUEVAS CARDS EN "DONE" =====

# --- Card 15: Analitica Service ---
$card = New-TrelloCard -ListId $doneListId -Name "Feat: Analitica-service completo - reportes, metricas, alertas, respaldos, logs" -Desc @"
**Objetivo:** Servicio central de inteligencia de negocio: logs de auditoria, reportes custom, metricas de dashboard, alertas del sistema y respaldos.

**Commits:** 428c2aa

**Componentes (5 controladores, 27 endpoints):**
- Reportes: 8 tipos (usuarios, pedidos, inventario, pagos, carrito, soporte, envios, completo)
- Metricas: CRUD de metricas por clave para dashboard
- Alertas: creacion y resolucion de alertas del sistema
- Respaldos: ejecucion y listado de respaldos de BD
- Logs: recepcion de logs de auditoria desde otros servicios

**Integraciones:**
- Recibe logs de: registro-usuarios, iniciosesion, proceso-pago, pedido y mas
- Genera reportes consultando datos via RestTemplate a cada servicio
- 38 tests, 100% coverage

**Entidades:** AlertaSistema, MetricaDashboard, Reporte, RespaldoBaseDatos, EstadoReporte, EstadoRespaldo, NivelAlerta, TipoReporte
"@ -LabelIds @($labels["feat"].id)

Add-TrelloChecklist -CardId $card.id -Name "Componentes de analitica" -Items @(
    "ReporteController - 14 endpoints de generacion de reportes",
    "MetricaController - CRUD de metricas de dashboard",
    "AlertaController - alertas con resolucion",
    "RespaldoController - respaldos de base de datos",
    "LogController - recepcion de logs de auditoria",
    "8 Domain services para cada entidad"
)

# --- Card 16: Catalogo e Inventario ---
$card = New-TrelloCard -ListId $doneListId -Name "Feat: Catalogo-inventario-service - dual inventory, stock, categorias, especificaciones" -Desc @"
**Objetivo:** Gestion de catalogo de productos con inventario dual (stock global + stock por sucursal), categorias, estados de disponibilidad y especificaciones tecnicas.

**Commits:** 5a6b86b, 5a2a0ea

**Componentes (3 controladores, 29 endpoints):**
- Productos: CRUD completo con busqueda por nombre y filtro por categoria
- Inventario: stock global + stock por sucursal, reserva/liberacion, transferencias, ajustes
- Admin: categorias, estados de disponibilidad, especificaciones tecnicas

**Logica de inventario:**
- StockGlobal: inventario central del deposito
- InventarioStock: inventario por sucursal (cantidad disponible + reservada)
- Reservar stock: decrementa disponible, incrementa reservado
- Liberar stock: reversa reserva
- Transferir: mueve stock global a sucursal
- Ajustar: setea cantidad exacta en sucursal

**Integraciones:** Validacion de sucursales via gestion-tienda-service, notificacion de stock bajo
"@ -LabelIds @($labels["feat"].id)

Add-TrelloChecklist -CardId $card.id -Name "Componentes de catalogo" -Items @(
    "ProductoController - 7 endpoints (CRUD + busqueda + filtro)",
    "InventarioController - 10 endpoints (stock, reserva, liberar, transferir, ajustar)",
    "CatalogoAdminController - 12 endpoints (categorias, estados, especificaciones)",
    "Sistema dual: StockGlobal + InventarioStock por sucursal",
    "21 tests"
)

# --- Card 17: Gestion Tienda ---
$card = New-TrelloCard -ListId $doneListId -Name "Feat: Gestion-tienda-service - sucursales, tareas, horarios, reglamentos, POS" -Desc @"
**Objetivo:** Gestion completa de tiendas: registro de sucursales, asignacion de gerentes, tareas de personal, permisos POS, horarios, reglamentos internos.

**Commits:** fe84c30, e63cdbb

**Componentes (1 controlador, 15 endpoints):**
- Sucursales: registro, consulta, activas, asignacion de gerente
- Tareas de personal: asignacion, actualizacion de estado
- Permisos POS: configuracion de permisos en punto de venta
- Reglamentos internos: establecimiento por sucursal
- Horarios: administracion y consulta de horarios de atencion

**Integraciones:**
- RegistroUsuariosClient: valida roles (GERENTE, EMPLEADO)
- ProcesoPagoClient: consulta transacciones

**Entidades:** Sucursal, TareaPersonal, EstadoTareaPersonal, PermisoPOS, ReglamentoInterno, HorarioAtencion
"@ -LabelIds @($labels["feat"].id)

Add-TrelloChecklist -CardId $card.id -Name "Componentes de tienda" -Items @(
    "GestionTiendaController - 15 endpoints",
    "Registro de sucursales con gerente",
    "Tareas de personal con estados",
    "Permisos POS por empleado",
    "Reglamentos internos por sucursal",
    "Horarios de atencion",
    "20 tests"
)

# --- Card 18: Registro de Usuarios completo ---
$card = New-TrelloCard -ListId $doneListId -Name "Feat: Registro-usuarios-service completo - perfiles, direcciones, roles, permisos" -Desc @"
**Objetivo:** Gestion de usuarios con registro, perfiles, multiples direcciones, roles y permisos, validacion de telefono y correo.

**Commits:** fe84c30, 06b6a07

**Componentes (3 controladores, 22 endpoints):**
- Usuarios: registro, modificacion, busqueda por ID/correo/rol, configuracion de permisos, eliminacion
- Direcciones: CRUD completo con direccion predeterminada, por usuario o por ID
- Catalogos: CRUD de roles, permisos y estados de perfil

**Validaciones:**
- CorreoDuplicadoException (409 Conflict)
- TelefonoDuplicadoException: strips non-digits, quita prefijo 56, compara
- RecursoNoEncontradoException (404)
- 404 en lugar de 400 para recursos no encontrados

**Integraciones:**
- Al registrarse, llama a iniciosesion-service para crear credencial (BCrypt)
- Envia logs de auditoria a analitica-service
- DatabaseSchemaFixer para compatibilidad

**Entidades:** PerfilUsuario, Direccion, Rol, Permiso, EstadoPerfil
"@ -LabelIds @($labels["feat"].id)

# --- Card 19: Patron de comunicacion inter-servicios ---
$card = New-TrelloCard -ListId $doneListId -Name "Refactor: Comunicacion inter-servicios estandarizada (RestTemplate + Clients)" -Desc @"
**Objetivo:** Estandarizar la comunicacion entre microservicios usando RestTemplate con configuracion homogenea y manejo de errores consistente.

**Commits:** fe84c30, 5a6b86b

**Patron implementado:**
- RestTemplate configurado como @Bean con JDK HttpClient + timeout 10s
- Clientes especializados (@Component) por servicio destino
- Manejo de errores con try-catch -> excepcion personalizada

**Mapa de comunicacion (14 conexiones):**
- api-gateway -> iniciosesion (validar JWT)
- carrito-compra -> catalogo-inventario (stock), proceso-pago (pago), logistica-envios (envio)
- registro-usuarios -> iniciosesion (credencial), analitica (logs)
- iniciosesion -> analitica (logs)
- proceso-pago -> pedido (estado), carrito (vaciar), analitica (logs)
- pedido -> registro-usuarios, carrito-compra, catalogo-inventario, analitica, logistica-envios
- logistica-envios -> soporte (notificaciones), analitica (metricas), pedido (propagacion)
- gestion-tienda -> registro-usuarios (roles), proceso-pago (transacciones)
- catalogo-inventario -> gestion-tienda (sucursales, stock bajo)

**Alternativa:** gestion-tienda-service usa RestClient (moderno) con .onStatus()
"@ -LabelIds @($labels["refactor"].id)

# --- Card 20: GlobalExceptionHandler estandarizado ---
$card = New-TrelloCard -ListId $doneListId -Name "Refactor: GlobalExceptionHandler estandarizado en 10 de 11 servicios" -Desc @"
**Objetivo:** Manejo consistente de errores HTTP en todos los microservicios con GlobalExceptionHandler y excepciones personalizadas.

**Commits:** fe84c30, f0310a2, 8240dde

**Excepciones estandar:**
- RecursoNoEncontradoException -> 404 NOT FOUND
- NoExisteEnBdException -> 404 NOT FOUND
- YaExisteEnBdException -> 409 CONFLICT
- CorreoDuplicadoException -> 409 CONFLICT
- TelefonoDuplicadoException -> 409 CONFLICT
- EstadoTransaccionInvalidoException -> 400 BAD REQUEST
- EmpleadoNoValidoException -> 403 FORBIDDEN
- CuponInvalidoException -> 400 BAD REQUEST
- AutenticacionException -> 401 UNAUTHORIZED
- TokenInvalidoException -> 401 UNAUTHORIZED
- CuentaBloqueadaException -> 423 LOCKED

**Servicios con GlobalExceptionHandler:**
registro-usuarios, carrito-compra, logistica-envios, analica, proceso-pago, iniciosesion, catalogo-inventario, soporte, pedido, gestion-tienda
"@ -LabelIds @($labels["refactor"].id)

# --- Card 21: Refactor sin interfaces de servicio ---
$card = New-TrelloCard -ListId $doneListId -Name "Refactor: Eliminacion de interfaces de servicio (estilo moderno Spring)" -Desc @"
**Objetivo:** Simplificar la arquitectura eliminando interfaces de servicio con una unica implementacion, siguiendo el estilo moderno de Spring Boot.

**Commits:** f0310a2

**Interfaces eliminadas (6):**
- PedidoService -> clase concreta @Service
- PagoService -> clase concreta @Service
- AuthService -> clase concreta @Service
- RegistroUsuarioService -> clase concreta @Service
- LoginCuentaService -> clase concreta @Service
- GestionTiendaService -> clase concreta @Service

**Cambios adicionales:**
- @Slf4j y log.warn en catch vacios de analitica
- CorreoDuplicadoException en RegistroUsuarioService y UsuarioService
- Fusion de buildPerfilDesdeRegistroDTO y buildPerfilDesdeModificarDTO en un unico metodo
- CORS unificado en @Bean CorsConfigurationSource
- Config beans con tests en servicios donde faltaban
"@ -LabelIds @($labels["refactor"].id)

# --- Card 22: Iniciosesion completo ---
$card = New-TrelloCard -ListId $doneListId -Name "Feat: Iniciosesion-service - login, JWT, logout, recuperacion de contrasena" -Desc @"
**Objetivo:** Servicio de autenticacion con login JWT, logout con blacklist, recuperacion de contrasena por token y gestion de credenciales.

**Commits:** 24d9148, 2ebd1b5, fe84c30

**Endpoints (9):**
- POST /api/sesion/credencial - crear credencial (llamado por registro-usuarios)
- POST /api/sesion/login - login, devuelve JWT + usuarioId + rol
- POST /api/sesion/logout - logout (blacklist del token)
- POST /api/sesion/validar - validar JWT (llamado por api-gateway)
- PUT /api/sesion/correo - cambiar correo
- PUT /api/sesion/contrasena - cambiar contrasena
- POST /api/sesion/recuperar - recuperar (genera token de recuperacion)
- POST /api/sesion/restablecer - restablecer con token
- DELETE /api/sesion/inhabilitar - inhabilitar credenciales

**Seguridad:**
- BCryptPasswordEncoder para almacenamiento de contrasenas
- JWT con jjwt 0.12.5, HMAC-SHA256
- TokenRecuperacion con expiracion de 2 horas
- SesionJWT para blacklist de tokens (logout)
- SecurityConfig con Spring Security filter chain
"@ -LabelIds @($labels["feat"].id, $labels["security"].id)

# --- 5. Cards en "Bugs" ---

Write-Host "Creando cards en Bugs ..." -ForegroundColor Cyan
$bugsListId = $lists["6- Bugs"]

# Bug 1: 500 al cerrar ticket
New-TrelloCard -ListId $bugsListId -Name "[soporte-service] 500 al cerrar ticket resuelto" -Desc @"
**Endpoint:** PATCH /api/v1/soporte/tickets/{id}/cerrar

**Comportamiento:** Cuando el ticket esta en estado RESUELTO (4) y se intenta cerrar, devuelve 500 Internal Server Error con mensaje generico.

**Posible causa:** ResponseStatusException capturado por el catch-all Exception.class del GlobalExceptionHandler.

**Prioridad:** Alta - bloquea el flujo de cierre de tickets.
"@ -LabelIds @($labels["bug"].id)

# Bug 2: Connection refused
New-TrelloCard -ListId $bugsListId -Name "[General] Connection refused si iniciosesion-service no esta corriendo" -Desc @"
**Endpoint:** POST /api/usuarios/registro

**Comportamiento:** Si iniciosesion-service (puerto 8086) no esta disponible, el registro de usuarios falla con Connection refused: connect.

**Impacto:** Bloquea el registro completo. Se podria mejorar con mensaje amigable o reintento.

**Nota:** Documentado en README como requisito conocido.
"@ -LabelIds @($labels["bug"].id)

# Bug 3: JaCoCo en catalogo-inventario
New-TrelloCard -ListId $bugsListId -Name "[catalogo-inventario] JaCoCo no configurado - sin cobertura" -Desc @"
**Servicio:** catalogo-inventario-service (8087)

**Problema:** No tiene el plugin jacoco-maven-plugin configurado en su pom.xml. No se genera reporte de cobertura.

**Impacto:** No se puede verificar la cobertura real del servicio. Tiene 21 tests pero no sabemos si cubren todo.

**Prioridad:** Media
"@ -LabelIds @($labels["bug"].id)

# Bug 4: JaCoCo en gestion-tienda
New-TrelloCard -ListId $bugsListId -Name "[gestion-tienda] JaCoCo no configurado - sin cobertura" -Desc @"
**Servicio:** gestion-tienda-service (8090)

**Problema:** No tiene el plugin jacoco-maven-plugin configurado en su pom.xml. No se genera reporte de cobertura.

**Impacto:** No se puede verificar la cobertura real del servicio. Tiene 20 tests pero no sabemos si cubren todo.

**Prioridad:** Media
"@ -LabelIds @($labels["bug"].id)

# Bug 5: application-test.properties faltantes
New-TrelloCard -ListId $bugsListId -Name "[General] 5 servicios sin application-test.properties" -Desc @"
**Problema:** Los siguientes servicios NO tienen archivo application-test.properties en src/test/resources:
- api-gateway
- carrito-compra-service
- analica-service
- catalogo-inventario-service
- gestion-tienda-service

**Impacto:** Si estos servicios dependen de MySQL para pruebas, fallaran en CI sin una BD disponible. Los otros 6 servicios si tienen el perfil test configurado.

**Prioridad:** Media
"@ -LabelIds @($labels["bug"].id)

# --- 6. Cards en "Product Backlog" ---

Write-Host "Creando cards en Product Backlog ..." -ForegroundColor Cyan
$backlogListId = $lists["1- Product Backlog"]

# Backlog 1: JaCoCo
New-TrelloCard -ListId $backlogListId -Name "Infra: Agregar JaCoCo a catalogo-inventario y gestion-tienda" -Desc @"
Agregar jacoco-maven-plugin a los 2 servicios que faltan para tener cobertura completa en los 11 servicios.

**Tareas:**
- Agregar plugin en pom.xml
- Configurar exclusiones estandar (Application.class, etc.)
- Verificar reporte en target/site/jacoco/index.html
"@ -LabelIds @($labels["infra"].id)

# Backlog 2: Docker
New-TrelloCard -ListId $backlogListId -Name "Infra: Dockerizar todos los servicios" -Desc @"
Crear Dockerfile para cada microservicio y docker-compose.yml para orquestacion local.

**Pendiente:**
- Dockerfile por servicio (multi-stage build)
- docker-compose con MySQL + todos los servicios
- Red interna para comunicacion entre contenedores
- Volumenes para persistencia de datos
"@ -LabelIds @($labels["infra"].id)

# Backlog 3: Refresh tokens
New-TrelloCard -ListId $backlogListId -Name "Seguridad: Refresh tokens rotativos" -Desc @"
Implementar refresh token rotativo para mejorar experiencia de usuario sin sacrificar seguridad.

**Flujo:**
- Access token: 15 min de expiracion
- Refresh token: 7 dias, almacenado en BD
- Endpoint: POST /api/sesion/refresh
- Revocacion de refresh tokens
"@ -LabelIds @($labels["security"].id, $labels["feat"].id)

# Backlog 4: Health checks
New-TrelloCard -ListId $backlogListId -Name "Infra: Health checks y Actuator en todos los servicios" -Desc @"
Agregar Spring Boot Actuator a cada servicio para monitoreo.

**Endpoints:**
- /actuator/health - liveness/readiness probes
- /actuator/metrics - metricas de negocio
- /actuator/prometheus - exportacion a Prometheus (opcional)
"@ -LabelIds @($labels["infra"].id)

# Backlog 5: Unificar GlobalExceptionHandler
New-TrelloCard -ListId $backlogListId -Name "Refactor: Unificar formato de respuesta de error en todos los servicios" -Desc @"
Asegurar que todos los servicios tengan el mismo formato de respuesta de error.

**Objetivo:**
- Mismo JSON structure en todos los servicios
- Codigos de error estandar
- Logging centralizado de errores
"@ -LabelIds @($labels["refactor"].id)

# Backlog 6: Circuit Breakers
New-TrelloCard -ListId $backlogListId -Name "Infra: Circuit Breakers con Resilience4j para comunicacion inter-servicios" -Desc @"
Agregar resiliencia a las llamadas HTTP entre servicios para evitar fallos en cascada.

**Implementacion:**
- Resilience4j Spring Boot 2 starter
- Timeout, retry, circuit breaker en cada RestTemplate call
- Fallback methods para respuestas parciales
- Health check de circuit breakers
"@ -LabelIds @($labels["infra"].id)

# Backlog 7: application-test.properties
New-TrelloCard -ListId $backlogListId -Name "Refactor: Crear application-test.properties en los 5 servicios faltantes" -Desc @"
Los servicios que faltan deben tener su perfil test configurado para no depender de MySQL en CI.

**Tareas:**
- api-gateway: configurar H2 o mock para tests
- carrito-compra-service: configurar H2
- analica-service: configurar H2
- catalogo-inventario-service: configurar H2
- gestion-tienda-service: configurar H2
"@ -LabelIds @($labels["refactor"].id)

# Backlog 8: Pagination
New-TrelloCard -ListId $backlogListId -Name "Feat: Paginacion y filtros estandar en endpoints de listado" -Desc @"
Estandarizar paginacion y filtros en todos los endpoints que devuelven listas.

**Formato propuesto:**
- Query params: page, size, sort, filter
- Response: { content, totalPages, totalElements, size, number }
- Implementar en Spring Data JPA con Pageable
"@ -LabelIds @($labels["feat"].id)

# Backlog 9: E2E Tests
New-TrelloCard -ListId $backlogListId -Name "Tests: Pruebas de integracion E2E multi-servicio" -Desc @"
Agregar pruebas de integracion que prueben flujos completos atravesando multiples servicios.

**Flujos a cubrir:**
- Compra completa: registro -> login -> carrito -> pago -> pedido -> envio
- Soporte: crear ticket -> asignar -> chatear -> resolver -> cerrar
- Administrativo: crear producto -> ajustar inventario -> transferir stock
"@ -LabelIds @($labels["tests"].id)

# Backlog 10: API Versioning
New-TrelloCard -ListId $backlogListId -Name "Feat: Versionado de API (v1, v2)" -Desc @"
Implementar estrategia de versionado de API para poder evolucionar los endpoints sin romper clientes existentes.

**Estrategia propuesta:**
- Version en path: /api/v1/... y /api/v2/...
- Header de version aceptada (Accept-version)
- Deprecation header en endpoints v1
- Mantener compatibilidad hacia atras por al menos 2 versiones
"@ -LabelIds @($labels["feat"].id)

# Backlog 11: Logging centralizado
New-TrelloCard -ListId $backlogListId -Name "Infra: Logging centralizado (ELK o Loki)" -Desc @"
Implementar agregacion de logs para debugging en entorno multi-servicio.

**Opciones:**
- ELK Stack (Elasticsearch + Logstash + Kibana)
- Grafana + Loki + Promtail (mas liviano)
- Formato JSON estructurado en logs
"@ -LabelIds @($labels["infra"].id)

# Backlog 12: Rate Limiting
New-TrelloCard -ListId $backlogListId -Name "Infra: Rate limiting en api-gateway" -Desc @"
Proteger los servicios contra abuso implementando rate limiting en el gateway.

**Implementacion:**
- Spring Cloud Gateway RequestRateLimiter filter
- Redis-based rate limiter (token bucket)
- Configurable por ruta y por rol
- Headers X-RateLimit-Remaining, X-RateLimit-Reset
"@ -LabelIds @($labels["infra"].id, $labels["security"].id)

Write-Host ""
Write-Host "==== TABLERO IMPORTADO EXITOSAMENTE ====" -ForegroundColor Green
Write-Host "Board ID: $boardId" -ForegroundColor Cyan
Write-Host "URL: https://trello.com/b/$($board.shortLink)" -ForegroundColor Cyan
Write-Host ""
Write-Host "Resumen:" -ForegroundColor White
Write-Host "  22 cards en Hecho (Done)"
Write-Host "  5 cards en Bugs"
Write-Host "  12 cards en Product Backlog"
Write-Host "  Total: 39 cards"
Write-Host ""
Write-Host "Recomendaciones post-importacion:" -ForegroundColor White
Write-Host "  1. Fijar los labels en el menu del board"
Write-Host "  2. Asignar miembros a las cards segun corresponda"
Write-Host "  3. Vincular GitHub Power-Up para asociar commits a cards"
Write-Host "  4. Mover cards a To Do cuando empieces un sprint"
Write-Host "  5. Activar Power-Up Calendar si tenes deadlines"
