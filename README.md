# GESFUN-BFF 

BFF Spring Boot para Angular + Azure Entra ID.

Este proyecto actua como Backend-For-Frontend entre el frontend Angular y el backend Gesfun. El frontend no debe llamar directo al backend: debe llamar al BFF, y el BFF valida el token de Azure, aplica permisos y reenvia la peticion al backend real.

## Stack y requisitos

```text
Java 21
Spring Boot 3.5.14
Maven Wrapper incluido
OAuth2 Resource Server con Azure Entra ID
JaCoCo para reporte de cobertura
```

No es necesario instalar Maven globalmente si se usa el wrapper del proyecto:

```powershell
.\mvnw.cmd --version
```

## Arquitectura local

```text
Frontend Angular: http://localhost:4200
BFF Gesfun:       http://localhost:8081
Backend Gesfun:   http://localhost:8080
Inventario:       http://localhost:8100
```

Flujo esperado:

```text
Angular/Postman -> BFF 8081 -> Backend Gesfun 8080
                            -> Inventario 8100
```

Ejemplo:

```text
GET http://localhost:8081/api/usuarios
```

El BFF reenvia internamente a:

```text
GET http://localhost:8080/api/usuarios
```

## Configuracion actual

Archivo principal:

```text
src/main/resources/application.properties
```

Propiedades relevantes:

```properties
server.port=8081
backend.base-url=http://localhost:8080
inventario.base-url=${INVENTARIO_URL:http://localhost:8100}
cors.allowed-origins=http://localhost:4200
```

`INVENTARIO_URL` permite cambiar el destino sin modificar el repositorio. Si la
variable no está definida, el BFF usa `http://localhost:8100`.

Seguridad Azure:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/<TENANT_ID>/v2.0
spring.security.oauth2.resourceserver.jwt.audience=<BFF_APP_ID>
security.jwt.allowed-client-ids=<POSTMAN_CLIENT_ID>,<FRONTEND_CLIENT_ID>
```

`security.jwt.allowed-client-ids` contiene los clientes autorizados para llamar al BFF:

```text
<POSTMAN_CLIENT_ID>  -> cliente usado para pruebas en Postman
<FRONTEND_CLIENT_ID> -> cliente usado por el frontend
```

Si Azure cambia el Client ID de `gesfun-client`, se debe agregar o reemplazar en `security.jwt.allowed-client-ids`.

## Azure

Apps usadas:

```text
gesfun-bff            -> API protegida por Azure
gesfun-client         -> frontend Angular
gesfun-postman-client -> cliente para pruebas en Postman
```

El token debe incluir:

```json
{
  "aud": "<BFF_APP_ID>",
  "scp": "access_as_user"
}
```

El BFF acepta el scope:

```text
access_as_user
```

## Ejecutar localmente

Instalar dependencias y compilar:

```powershell
.\mvnw.cmd clean package
```

Levantar backend:

```powershell
cd <RUTA_DEL_BACKEND>
.\mvnw.cmd spring-boot:run
```

Levantar Inventario:

```powershell
cd <RUTA_INVENTARIO_SERVICE>
.\mvnw.cmd spring-boot:run
```

Levantar BFF:

```powershell
cd <RUTA_DEL_BFF>
.\mvnw.cmd spring-boot:run
```

Cambiar el destino de Inventario sin editar archivos:

```powershell
$env:INVENTARIO_URL="http://localhost:8100"
.\mvnw.cmd spring-boot:run
```

Verificar health del BFF:

```powershell
Invoke-RestMethod -Uri http://localhost:8081/actuator/health
```

Respuesta esperada:

```json
{
  "status": "UP"
}
```

## Probar con Postman

Usar siempre el BFF:

```text
GET http://localhost:8081/api/usuarios
Authorization: Bearer <ACCESS_TOKEN>
```

Respuesta esperada desde el BFF:

```json
{
  "success": true,
  "payload": [
    {
      "id": 1,
      "email": "admin@gesfun.cl"
    }
  ],
  "message": null
}
```

Si llamas a `8080`, estas probando el backend directo, no el BFF.

## CRUD principales

El BFF ahora tiene controllers, services y models por recurso, siguiendo la estructura del backend.
Cada controller responde con `FrontendResponse` y cada service reenvia la llamada al backend usando `ProxyService`.

Usuarios:

```text
GET    /api/usuarios
GET    /api/usuarios/{id}
POST   /api/usuarios
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}
```

Clientes, proveedores y empleados son endpoints BFF sobre `/api/terceros`.
El BFF agrega o fuerza el `rol` correcto en `POST` y `PUT`.

```text
GET    /api/clientes
GET    /api/clientes/{uuid}
GET    /api/clientes/empresa/{empresaUuid}
POST   /api/clientes
PUT    /api/clientes/{uuid}
PATCH  /api/clientes/{uuid}/desactivar

GET    /api/proveedores
GET    /api/proveedores/{uuid}
GET    /api/proveedores/empresa/{empresaUuid}
POST   /api/proveedores
PUT    /api/proveedores/{uuid}
PATCH  /api/proveedores/{uuid}/desactivar

GET    /api/empleados
GET    /api/empleados/{uuid}
GET    /api/empleados/empresa/{empresaUuid}
POST   /api/empleados
PUT    /api/empleados/{uuid}
PATCH  /api/empleados/{uuid}/desactivar
```

Empresas:

```text
GET    /api/empresas
GET    /api/empresas/{uuid}
GET    /api/empresas/usuario/{usuarioUuid}
POST   /api/empresas
PUT    /api/empresas/{uuid}
PATCH  /api/empresas/{uuid}/desactivar
```

Sucursales:

```text
GET    /api/sucursales
GET    /api/sucursales/{uuid}
GET    /api/sucursales/empresa/{empresaUuid}
POST   /api/sucursales
PUT    /api/sucursales/{uuid}
PATCH  /api/sucursales/{uuid}/desactivar
```

Regiones:

```text
GET    /api/regiones
GET    /api/regiones/{uuid}
POST   /api/regiones
PUT    /api/regiones/{uuid}
DELETE /api/regiones/{uuid}
```

Comunas:

```text
GET    /api/comunas
GET    /api/comunas/{uuid}
POST   /api/comunas
PUT    /api/comunas/{uuid}
DELETE /api/comunas/{uuid}
```

Planes:

```text
GET    /api/planes
GET    /api/planes/{uuid}
GET    /api/planes/sucursal/{sucursalUuid}
POST   /api/planes
PUT    /api/planes/{uuid}
PATCH  /api/planes/{uuid}/desactivar
```

Plan kit:

```text
GET    /api/plan-kit
GET    /api/plan-kit/{uuid}
GET    /api/plan-kit/plan/{planUuid}
POST   /api/plan-kit
PUT    /api/plan-kit/{uuid}
DELETE /api/plan-kit/{uuid}
```

Productos y servicios:

```text
GET    /api/productos-servicios
GET    /api/productos-servicios/{uuid}
GET    /api/productos-servicios/empresa/{empresaUuid}
POST   /api/productos-servicios
PUT    /api/productos-servicios/{uuid}
PATCH  /api/productos-servicios/{uuid}/desactivar
```

Suscripcion de planes:

```text
GET    /api/suscripcion-planes
GET    /api/suscripcion-planes/{uuid}
POST   /api/suscripcion-planes
PUT    /api/suscripcion-planes/{uuid}
DELETE /api/suscripcion-planes/{uuid}
```

Catalogos:

```text
GET    /api/unidades-medida
GET    /api/unidades-medida/{uuid}
POST   /api/unidades-medida
PUT    /api/unidades-medida/{uuid}
DELETE /api/unidades-medida/{uuid}

GET    /api/tipos-movimiento
GET    /api/tipos-movimiento/{uuid}
POST   /api/tipos-movimiento
PUT    /api/tipos-movimiento/{uuid}
DELETE /api/tipos-movimiento/{uuid}

GET    /api/tipos-recurso
GET    /api/tipos-recurso/sucursal/{sucursalUuid}
GET    /api/tipos-recurso/{uuid}
POST   /api/tipos-recurso
PUT    /api/tipos-recurso/{uuid}
DELETE /api/tipos-recurso/{uuid}

GET    /api/formas-pago
GET    /api/formas-pago/{uuid}
POST   /api/formas-pago
PUT    /api/formas-pago/{uuid}
DELETE /api/formas-pago/{uuid}

GET    /api/estados-cotizacion
GET    /api/estados-cotizacion/{uuid}
POST   /api/estados-cotizacion
PUT    /api/estados-cotizacion/{uuid}
DELETE /api/estados-cotizacion/{uuid}

POST   /api/cotizaciones
GET    /api/cotizaciones
GET    /api/cotizaciones/{uuid}
GET    /api/cotizaciones/sucursal/{sucursalUuid}
PATCH  /api/cotizaciones/{uuid}/estado

GET    /api/pagos
GET    /api/pagos/{uuid}
GET    /api/pagos/cotizacion/{cotizacionUuid}
POST   /api/pagos
PUT    /api/pagos/{uuid}
PATCH  /api/pagos/{uuid}/anular

GET    /api/documentos-tributarios
GET    /api/documentos-tributarios/{uuid}
GET    /api/documentos-tributarios/pago/{pagoUuid}
GET    /api/documentos-tributarios/cotizacion/{cotizacionUuid}
POST   /api/documentos-tributarios/emitir
PUT    /api/documentos-tributarios/{uuid}
POST   /api/documentos-tributarios/{uuid}/reenviar
PATCH  /api/documentos-tributarios/{uuid}/anular

GET    /api/agendas
GET    /api/agendas/{uuid}
GET    /api/agendas/sucursal/{sucursalUuid}
GET    /api/agendas/tipo-recurso/{tipoRecursoUuid}
POST   /api/agendas
PUT    /api/agendas/{uuid}
DELETE /api/agendas/{uuid}

GET    /api/servicios
GET    /api/servicios/{uuid}
GET    /api/servicios/sucursal/{sucursalUuid}
GET    /api/servicios/estado/{estado}
GET    /api/servicios/cotizacion/{cotizacionUuid}
GET    /api/servicios/cliente/{terceroUuid}
POST   /api/servicios
PUT    /api/servicios/{uuid}
PATCH  /api/servicios/{uuid}/desactivar

GET    /api/motivos-fallecimiento
GET    /api/motivos-fallecimiento/{uuid}
POST   /api/motivos-fallecimiento
PUT    /api/motivos-fallecimiento/{uuid}
DELETE /api/motivos-fallecimiento/{uuid}
```

### Cotizaciones

Las llamadas de cotizacion mantienen el mismo flujo protegido del resto del BFF:

```text
Angular/Postman -> BFF 8081 -> Backend 8080
```

Ejemplo para crear una cotizacion:

```http
POST /api/cotizaciones
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

```json
{
  "sucursalUuid": "uuid-sucursal",
  "planUuid": "uuid-plan",
  "formaPagoUuid": "uuid-forma-pago",
  "motivoFallecimientoUuid": "uuid-motivo",
  "fecha": "2026-06-19",
  "fechaValidez": "2026-06-30",
  "observacion": "Cotizacion inicial",
  "fechaFallecimiento": "2026-06-18",
  "horaFallecimiento": "18:30:00",
  "lugarFallecimiento": "Santiago",
  "pagador": {
    "tipoPersona": "N",
    "rut": 12345678,
    "dv": "5",
    "nombres": "Ana",
    "apellidoPaterno": "Perez",
    "apellidoMaterno": "Soto",
    "email": "ana@example.com",
    "telefono": "+56912345678",
    "comunaUuid": "uuid-comuna"
  },
  "fallecido": {
    "tipoPersona": "N",
    "rut": 8765432,
    "dv": "1",
    "nombres": "Juan",
    "apellidoPaterno": "Perez",
    "apellidoMaterno": "Soto",
    "comunaUuid": "uuid-comuna"
  },
  "detalles": [
    {
      "productoServicioUuid": "uuid-producto-servicio",
      "cantidad": 1,
      "descuento": 0,
      "observacion": "Detalle principal"
    }
  ]
}
```

El backend asigna el numero, estado inicial y totales. El BFF valida la estructura, reenvia el token y devuelve la respuesta envuelta en `FrontendResponse`.

Para cambiar el estado de una cotizacion:

```http
PATCH /api/cotizaciones/{uuid}/estado
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

```json
{
  "estadoUuid": "uuid-estado-cotizacion"
}
```

### Pagos y facturacion

Las llamadas de pagos y documentos tributarios mantienen el mismo flujo protegido
del resto del BFF:

```text
Angular/Postman -> BFF 8081 -> Backend 8080
```

Registrar un pago:

```http
POST /api/pagos
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

```json
{
  "cotizacionUuid": "uuid-cotizacion",
  "formaPagoUuid": "uuid-forma-pago",
  "monto": 150000,
  "fechaPago": "2026-06-27T10:30:00",
  "observacion": "Abono inicial"
}
```

Estados de pago:

```text
REGISTRADO
ANULADO
```

Consultar pagos de una cotizacion:

```text
GET /api/pagos/cotizacion/{cotizacionUuid}
```

Anular un pago:

```text
PATCH /api/pagos/{uuid}/anular
```

Emitir un documento tributario simulado:

```http
POST /api/documentos-tributarios/emitir
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

```json
{
  "pagoUuid": "uuid-pago",
  "tipoDocumentoCodigo": "BOLETA",
  "observacion": "Emision por pago de servicio funerario"
}
```

`tipoDocumentoCodigo` debe seleccionarse desde una lista fija del frontend:

```text
BOLETA
FACTURA
```

No se debe consultar `/api/tipos-documento` para poblar tipos DTE. Esa tabla se
usa para documentos operacionales funerarios, no para documentos tributarios.
Si se envia otro valor, el backend responde `400 Bad Request` con el mensaje:

```text
Tipo de documento tributario invalido. Use BOLETA o FACTURA.
```

Estados DTE:

```text
PENDIENTE
ENVIADO
EMITIDO
RECHAZADO
ANULADO
```

El backend no permite emitir DTE para pagos anulados ni emitir mas de un DTE
activo para el mismo pago. La emision simula el proveedor
`DTEEMITE_SIMULADO`; el total se toma desde el monto del pago y el receptor se
toma desde el pagador de la cotizacion.

Para `BOLETA`, el backend responde `tipoDocumentoNombre` como `Boleta`. Para
`FACTURA`, responde `Factura`. La respuesta no incluye `tipoDocumentoUuid`.

Ejemplo de respuesta esperada dentro del `payload`:

```json
{
  "uuid": "uuid-documento",
  "pagoUuid": "uuid-pago",
  "cotizacionUuid": "uuid-cotizacion",
  "cotizacionNumero": 15,
  "tipoDocumentoCodigo": "BOLETA",
  "tipoDocumentoNombre": "Boleta",
  "estado": "EMITIDO",
  "folio": "12345",
  "trackId": "DTE-20260627-12345",
  "proveedor": "DTEEMITE_SIMULADO",
  "pdfUrl": "https://dteemite.local/documentos/12345.pdf",
  "xmlUrl": "https://dteemite.local/documentos/12345.xml"
}
```

La respuesta completa tambien puede incluir `requestJson`, `responseJson` y
`detalles`, envueltos por el BFF en `FrontendResponse`.

Rutas adicionales:

```text
GET   /api/documentos-tributarios/pago/{pagoUuid}
GET   /api/documentos-tributarios/cotizacion/{cotizacionUuid}
POST  /api/documentos-tributarios/{uuid}/reenviar
PATCH /api/documentos-tributarios/{uuid}/anular
```

### Agenda de servicios

Las agendas se reenvian al backend Gesfun en `/api/agendas` y quedan asociadas
a sucursal, tipo de recurso y cotizacion:

```http
POST /api/agendas
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

```json
{
  "fechaHoraInicio": "2026-06-25T10:00:00",
  "fechaHoraFin": "2026-06-25T12:00:00",
  "estado": "OCUPADO",
  "observacion": "Sala de velatorio para servicio",
  "tipoRecursoUuid": "uuid-tipo-recurso",
  "sucursalUuid": "uuid-sucursal",
  "cotizacionUuid": "uuid-cotizacion"
}
```

El backend valida reglas de negocio como rango horario, pertenencia a sucursal,
cotizacion obligatoria y solapamientos para agendas `OCUPADO`.

Health del backend pasando por el BFF:

```text
GET /api/health/database
```

### Servicios funerarios

Los servicios funerarios representan el seguimiento operativo de un caso real.
No son el catalogo comercial de `/api/productos-servicios`.

Rutas disponibles:

```text
GET    /api/servicios
GET    /api/servicios/{uuid}
GET    /api/servicios/sucursal/{sucursalUuid}
GET    /api/servicios/estado/{estado}
GET    /api/servicios/cotizacion/{cotizacionUuid}
GET    /api/servicios/cliente/{terceroUuid}
POST   /api/servicios
PUT    /api/servicios/{uuid}
PATCH  /api/servicios/{uuid}/desactivar
```

Estados validos:

```text
PENDIENTE
PROGRAMADO
EN_CURSO
COMPLETADO
ANULADO
```

Crear un servicio desde una cotizacion:

```http
POST /api/servicios
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

```json
{
  "folio": "ES-2026-0001",
  "estado": "PENDIENTE",
  "cotizacionUuid": "uuid-cotizacion",
  "agendaUuid": null,
  "fechaIngreso": "2026-06-30T00:00:00",
  "fechaVelatorio": null,
  "fechaCeremonia": null,
  "fechaTermino": null,
  "destino": "Cementerio General",
  "observacion": "Servicio creado desde cotizacion"
}
```

En creacion, `cotizacionUuid` es obligatorio. El frontend no debe duplicar
datos maestros de la cotizacion en el payload. El backend deriva estos campos:

```text
terceroUuid / clienteNombre / clienteRut
sucursalUuid / sucursalNombre
fallecidoNombre / fallecidoRut
suscripcionPlanUuid / planNombre
motivoFallecimientoUuid / motivoFallecimientoNombre
montoTotal
```

`estado` es opcional; si no se envia, el backend usa `PENDIENTE`.
`montoPagado` inicia en `0` si no se envia.

Respuesta esperada dentro del `payload`:

```json
{
  "uuid": "uuid-servicio",
  "folio": "ES-2026-0001",
  "cotizacionUuid": "uuid-cotizacion",
  "cotizacionNumero": 15,
  "terceroUuid": "uuid-cliente",
  "clienteNombre": "Roberto Soto Vidal",
  "clienteRut": "10224881-5",
  "fallecidoNombre": "Nombre Apellido",
  "fallecidoRut": "12345678-9",
  "suscripcionPlanUuid": "uuid-plan",
  "planNombre": "Plan Tradicional",
  "sucursalUuid": "uuid-sucursal",
  "sucursalNombre": "Casa matriz",
  "agendaUuid": null,
  "salaNombre": null,
  "estado": "PENDIENTE",
  "estadoNombre": "Pendiente",
  "fechaIngreso": "2026-06-30T00:00:00",
  "fechaVelatorio": null,
  "fechaCeremonia": null,
  "fechaTermino": null,
  "destino": "Cementerio General",
  "montoTotal": 2050000,
  "montoPagado": 0,
  "saldoPendiente": 2050000,
  "observacion": "Servicio creado desde cotizacion",
  "activo": true
}
```

Reglas aplicadas por el backend:

```text
No debe existir mas de un servicio activo para la misma cotizacion.
Si se envia agendaUuid, debe pertenecer a la misma cotizacion y sucursal.
saldoPendiente se calcula como montoTotal - montoPagado.
El backend deriva datos maestros desde cotizacionUuid al crear el servicio.
COMPLETADO y ANULADO no permiten modificaciones operativas criticas.
PATCH /desactivar marca el servicio como inactivo y lo deja en ANULADO.
```

### Inventario

El BFF dirige estas rutas al microservicio configurado mediante
`inventario.base-url` (por defecto `http://localhost:8100`):

```text
POST  /api/inventario/entradas
POST  /api/inventario/salidas
PATCH /api/inventario/movimientos/{movimientoUuid}/anular
GET   /api/inventario/stock?sucursalUuid={uuid}
GET   /api/inventario/stock/productos/{productoUuid}?sucursalUuid={uuid}
GET   /api/inventario/reportes/kardex?productoUuid={uuid}&sucursalUuid={uuid}
```

El frontend sigue llamando únicamente al BFF en el puerto `8081`. El BFF valida
el token, lo reenvía al microservicio y envuelve su respuesta en
`FrontendResponse`.

Ejemplo de entrada completa enviada en una sola petición:

```http
POST /api/inventario/entradas
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

```json
{
  "sucursalUuid": "uuid-sucursal",
  "tipoMovimientoUuid": "uuid-tipo-entrada",
  "formaPagoUuid": "uuid-forma-pago",
  "terceroUuid": "uuid-proveedor",
  "recibidoPorUuid": null,
  "usuarioUuid": "uuid-usuario",
  "fechaDocumento": "2026-06-20",
  "fechaRecepcion": "2026-06-20",
  "fechaPago": "2026-06-20",
  "numeroOc": "OC-POSTMAN-001",
  "numeroGuia": "GUIA-POSTMAN-001",
  "numeroFactura": "FACT-POSTMAN-001",
  "observacion": "Entrada creada desde Postman",
  "detalles": [
    {
      "productoUuid": "uuid-producto-uno",
      "cantidad": 3,
      "costoUnitario": 180000,
      "descuento": 0,
      "observacion": "Primer producto"
    },
    {
      "productoUuid": "uuid-producto-dos",
      "cantidad": 4,
      "costoUnitario": 90000,
      "descuento": 0,
      "observacion": "Segundo producto"
    }
  ]
}
```

El BFF reenvía ese body como una sola llamada a
`POST http://localhost:8100/api/inventario/entradas`. Inventario valida y guarda
la cabecera y todos los detalles dentro de una única transacción. Si falla una
línea, no se registra ninguna parte de la entrada.

## Diagnostico de token

Endpoint del BFF para revisar el token autenticado:

```text
GET http://localhost:8081/bff/me
Authorization: Bearer <ACCESS_TOKEN>
```

Devuelve datos utiles del JWT, como issuer, audience, scope, version, clientId y username.

Tambien puedes inspeccionar el token en:

```text
https://jwt.ms
```

Campos importantes:

```text
iss
aud
scp
azp / appid / client_id
```

## Errores comunes

`Port 8081 is already in use`

Ya hay una instancia del BFF corriendo. Buscar el proceso:

```powershell
netstat -ano | Select-String ':8081'
```

Cerrar el PID encontrado:

```powershell
Stop-Process -Id <PID>
```

`401 Unauthorized`

El BFF no acepto el token. Revisar:

```text
issuer
audience
scope access_as_user
client id permitido
```

`403 Forbidden`

El token fue valido, pero no trae el scope requerido.

`Connection refused http://localhost:8080/...`

El BFF esta levantado, pero el backend no esta respondiendo en `8080`.

`Connection refused http://localhost:8100/...`

El BFF esta levantado, pero el microservicio de Inventario no esta respondiendo
en `8100`, o `INVENTARIO_URL` apunta a una dirección incorrecta.

## Endpoints

La lista de endpoints disponibles esta en:

```text
BFF_ENDPOINTS.md
```

## Archivos principales

```text
src/main/java/cl/gesfun/gesfun_bff/config/SecurityConfig.java
src/main/java/cl/gesfun/gesfun_bff/controller/BffProxyController.java
src/main/java/cl/gesfun/gesfun_bff/controller/BffDiagnosticsController.java
src/main/java/cl/gesfun/gesfun_bff/controller/BffServicioFunerarioController.java
src/main/java/cl/gesfun/gesfun_bff/controller/Bff*Controller.java
src/main/java/cl/gesfun/gesfun_bff/service/ProxyService.java
src/main/java/cl/gesfun/gesfun_bff/service/CrudBffService.java
src/main/java/cl/gesfun/gesfun_bff/service/ServicioFunerarioBffService.java
src/main/java/cl/gesfun/gesfun_bff/service/*BffService.java
src/main/java/cl/gesfun/gesfun_bff/model/FrontendResponse.java
src/main/java/cl/gesfun/gesfun_bff/model/ServicioFunerarioCreate.java
src/main/java/cl/gesfun/gesfun_bff/model/ServicioFunerario.java
src/main/java/cl/gesfun/gesfun_bff/model/*.java
src/main/java/cl/gesfun/gesfun_bff/error/GlobalErrorHandler.java
src/main/resources/application.properties
```

## Estructura de capas

```text
controller -> recibe llamadas del frontend/Postman
service    -> arma la llamada hacia el backend
model      -> representa el body que envia el frontend
ProxyService -> reenvia HTTP al backend local
```

Los modelos del BFF son unicos por recurso, por ejemplo `Usuario`, `Empresa`, `Sucursal`, `ProductoServicio`, `ServicioFunerario`.
Cuando el backend expone contratos distintos para crear y actualizar, el BFF usa un modelo especifico como `ServicioFunerarioCreate` para evitar que el frontend duplique datos derivados por el backend.

## Verificacion

Compilar, ejecutar tests y generar cobertura:

```powershell
.\mvnw.cmd test
```

Generar el artefacto ejecutable:

```powershell
.\mvnw.cmd clean package
java -jar target\gesfun-bff-0.0.1-SNAPSHOT.jar
```

Construir imagen Docker:

```powershell
docker build -t gesfun-bff .
docker run --rm -p 8081:8081 -e INVENTARIO_URL=http://host.docker.internal:8100 gesfun-bff
```

El reporte HTML de JaCoCo queda en:

```text
target/site/jacoco/index.html
```

Cobertura actual de referencia:

```text
Total:       93% instrucciones
Branches:   75%
Controller: 98%
Service:    81%
Config:     99%
Error:      100%
```

JaCoCo excluye del reporte los `model/*` y `GesfunBffApplication`, porque son records/clases de arranque sin logica de negocio. Los tests unitarios principales estan en:

```text
src/test/java/cl/gesfun/gesfun_bff/config
src/test/java/cl/gesfun/gesfun_bff/controller
src/test/java/cl/gesfun/gesfun_bff/error
src/test/java/cl/gesfun/gesfun_bff/service
```
