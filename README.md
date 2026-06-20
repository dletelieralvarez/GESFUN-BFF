# GESFUN-BFF 

BFF Spring Boot para Angular + Azure Entra ID.

Este proyecto actua como Backend-For-Frontend entre el frontend Angular y el backend Gesfun. El frontend no debe llamar directo al backend: debe llamar al BFF, y el BFF valida el token de Azure, aplica permisos y reenvia la peticion al backend real.

## Arquitectura local

```text
Frontend Angular: http://localhost:4200
BFF Gesfun:       http://localhost:8081
Backend Gesfun:   http://localhost:8080
```

Flujo esperado:

```text
Angular/Postman -> BFF 8081 -> Backend 8080
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
cors.allowed-origins=http://localhost:4200
```

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

Levantar backend:

```powershell
cd <RUTA_DEL_BACKEND>
.\mvnw.cmd spring-boot:run
```

Levantar BFF:

```powershell
cd <RUTA_DEL_BFF>
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
GET    /api/cotizaciones/{uuid}
GET    /api/cotizaciones/sucursal/{sucursalUuid}

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

Health del backend pasando por el BFF:

```text
GET /api/health/database
```

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
src/main/java/cl/gesfun/gesfun_bff/controller/Bff*Controller.java
src/main/java/cl/gesfun/gesfun_bff/service/ProxyService.java
src/main/java/cl/gesfun/gesfun_bff/service/CrudBffService.java
src/main/java/cl/gesfun/gesfun_bff/service/*BffService.java
src/main/java/cl/gesfun/gesfun_bff/model/FrontendResponse.java
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

Los modelos del BFF son unicos por recurso, por ejemplo `Usuario`, `Empresa`, `Sucursal`, `ProductoServicio`.
No se separan en `CreateRequest` y `UpdateRequest`; el backend mantiene la validacion final de campos obligatorios.

## Verificacion

Compilar, ejecutar tests y generar cobertura:

```powershell
.\mvnw.cmd test
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
