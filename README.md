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
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/0848441e-8d61-4f58-84b7-9f55266c7ee4/v2.0
spring.security.oauth2.resourceserver.jwt.audience=daead1c3-a4cc-4647-9423-e1fc626d8003
security.jwt.allowed-client-ids=7c4068b3-4cdf-42f3-84ac-f8e2d2042118,e0090ab4-8924-4d8e-a5be-3edca3ebe556
```

`security.jwt.allowed-client-ids` contiene los clientes autorizados para llamar al BFF:

```text
7c4068b3-4cdf-42f3-84ac-f8e2d2042118 -> gesfun-postman-client
e0090ab4-8924-4d8e-a5be-3edca3ebe556 -> gesfun-client
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
  "aud": "daead1c3-a4cc-4647-9423-e1fc626d8003",
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
cd C:\Users\Gonzalo\Documents\DUOC\Bimestre7\Semana1\gesfun-backend-main\gesfun-backend-main
.\mvnw.cmd spring-boot:run
```

Levantar BFF:

```powershell
cd C:\Users\Gonzalo\Documents\DUOC\Bimestre7\PROYECTO\gesfun-bff\gesfun-bff
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

GET    /api/motivos-fallecimiento
GET    /api/motivos-fallecimiento/{uuid}
POST   /api/motivos-fallecimiento
PUT    /api/motivos-fallecimiento/{uuid}
DELETE /api/motivos-fallecimiento/{uuid}
```

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

Compilar y ejecutar tests:

```powershell
.\mvnw.cmd test
```
