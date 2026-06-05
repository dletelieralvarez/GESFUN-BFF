# GESFUN-BFF

BFF Spring Boot para Angular + Azure Entra ID

Este repositorio contiene un Backend-For-Frontend (BFF) desarrollado con Spring Boot. El objetivo es recibir peticiones desde un frontend Angular, validar usuarios y permisos mediante Azure Entra ID (Azure AD), aplicar autorización, reenviar/consultar servicios de backend y devolver respuestas adaptadas y consistentes para el frontend.

Características principales
- Validación de tokens JWT emitidos por Azure Entra ID (configurado como resource server).
- Transformación de claims `scp` en authorities (`SCOPE_...`).
- Control de acceso por scope usando `@PreAuthorize`.
- Proxy de peticiones a un backend real (clase `ProxyService`).
- Formato de respuesta uniforme para el frontend: `FrontendResponse`.
- Manejo global de errores para devolver siempre JSON legible por Angular.

Archivos añadidos y su función
- `src/main/java/cl/gesfun/gesfun_bff/config/SecurityConfig.java`: configuración de Spring Security, CORS, y conversión de claims a authorities.
- `src/main/java/cl/gesfun/gesfun_bff/service/ProxyService.java`: lógica de reenvío (proxy) de las peticiones entrantes hacia `backend.base-url`.
- `src/main/java/cl/gesfun/gesfun_bff/controller/BffProxyController.java`: controlador principal en `/api/**` que valida permisos y llama al proxy.
- `src/main/java/cl/gesfun/gesfun_bff/model/FrontendResponse.java`: modelo de respuesta consistente para el frontend.
- `src/main/java/cl/gesfun/gesfun_bff/error/GlobalErrorHandler.java`: manejo centralizado de excepciones.
- `src/main/resources/application.properties`: propiedades de configuración (issuer, audience, backend.base-url, cors.allowed-origins).

Configuración importante
- `spring.security.oauth2.resourceserver.jwt.issuer-uri`: URL del issuer de Azure (ejemplo: `https://login.microsoftonline.com/<TENANT_ID>/v2.0`).
- `spring.security.oauth2.resourceserver.jwt.audience`: audience (client id) del API (ejemplo `api://<CLIENT_ID>` o el GUID del App-Id URI).
- `backend.base-url`: URL del backend real al que el BFF hará proxy (no debe ser el mismo puerto donde corre el BFF).
- `cors.allowed-origins`: orígenes permitidos por CORS (ej: `http://localhost:4200`).

Scopes y roles
El proyecto convierte los scopes de Azure que vienen en el claim `scp` a authorities con prefijo `SCOPE_`. Por ejemplo, si el token contiene `scp: "access_as_user"`, el authority resultante es `SCOPE_access_as_user`.

En la aplicación actual se exige el siguiente scope en el controlador proxy:
`SCOPE_https://duocactividadazure.onmicrosoft.com/daead1c3-a4cc-4647-9423-e1fc626d8003/access_as_user`

Además, en la base de datos o modelo de usuario se contemplan roles posibles (ejemplo mostrado en tu captura):
- `CLIENTE`
- `PROVEEDOR`
- `EMPLEADO`
- `FALLECIDO`

Cómo ejecutar localmente
1. Ajusta `src/main/resources/application.properties`:

```
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/<TENANT_ID>/v2.0
spring.security.oauth2.resourceserver.jwt.audience=api://<CLIENT_ID>
backend.base-url=http://localhost:8081   # URL del backend real
cors.allowed-origins=http://localhost:4200
```

2. Ejecutar la aplicación (Windows PowerShell):

```powershell
cd gesfun-bff
.\mvnw.cmd spring-boot:run
```

3. Probar la salud del servicio (sin token):

```powershell
Invoke-WebRequest -Uri http://localhost:8080/actuator/health -UseBasicParsing
```

Probar con Postman (flujo típico)
1. Obtener token de acceso desde Azure (OAuth 2.0) para la aplicación cliente que tenga permiso sobre este API. El scope que debes solicitar depende de cómo registraste tu App-API en Azure (ej: `api://<CLIENT_ID>/access_as_user` o `access_as_user`).
2. En Postman, crea una petición a `http://localhost:8080/api/<ruta-del-backend>` y añade header:

`Authorization: Bearer <ACCESS_TOKEN>`

3. Envía la petición. El BFF validará el JWT, comprobará los scopes y reenviará la petición a `backend.base-url` copiando `Authorization` y `Content-Type`.

Notas y recomendaciones
- Verifica el claim `scp` del JWT en https://jwt.ms para saber si el valor es la URI completa o solo `access_as_user`. Ajusta la expresión de `@PreAuthorize` en `BffProxyController` si hace falta (por ejemplo `hasAuthority('SCOPE_access_as_user')`).
- Asegúrate de que `backend.base-url` no apunte al mismo puerto del BFF para evitar loops.
- Puedes adaptar el `FrontendResponse` para incluir metadatos adicionales que requiera Angular (paginación, código interno, etc.).

Contribuir
- Crear un fork, abrir PRs. Para discutir cambios mayores, abre un Issue primero.

Contacto
- Autor: Danitza (repositorio inicial y cambios BFF)

---
Fecha de documentación: 2026-06-04
