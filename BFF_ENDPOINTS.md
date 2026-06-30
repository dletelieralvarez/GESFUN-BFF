# BFF hacia backend local

Arquitectura local recomendada:

```text
Frontend Angular: http://localhost:4200
BFF Gesfun:       http://localhost:8081
Backend Gesfun:   http://localhost:8080
Inventario:       http://localhost:8100
```

El frontend debe llamar siempre al BFF. Por ejemplo:

```text
GET http://localhost:8081/api/usuarios
```

El BFF reenvia esa llamada al backend real:

```text
GET http://localhost:8080/api/usuarios
```

## Endpoints disponibles desde el BFF

## CRUD principales para Angular

Usuarios se reenvia directo al backend `/api/usuarios`:

```text
GET    /api/usuarios
GET    /api/usuarios/{id}
POST   /api/usuarios
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}
```

Clientes, proveedores y empleados son vistas BFF sobre el backend `/api/terceros`.
El BFF agrega o fuerza automaticamente el campo `rol` segun la ruta:

```text
clientes    -> rol CLIENTE
proveedores -> rol PROVEEDOR
empleados   -> rol EMPLEADO
```

Clientes:

```text
GET    /api/clientes
GET    /api/clientes/{uuid}
GET    /api/clientes/empresa/{empresaUuid}
POST   /api/clientes
PUT    /api/clientes/{uuid}
PATCH  /api/clientes/{uuid}/desactivar
```

Proveedores:

```text
GET    /api/proveedores
GET    /api/proveedores/{uuid}
GET    /api/proveedores/empresa/{empresaUuid}
POST   /api/proveedores
PUT    /api/proveedores/{uuid}
PATCH  /api/proveedores/{uuid}/desactivar
```

Empleados:

```text
GET    /api/empleados
GET    /api/empleados/{uuid}
GET    /api/empleados/empresa/{empresaUuid}
POST   /api/empleados
PUT    /api/empleados/{uuid}
PATCH  /api/empleados/{uuid}/desactivar
```

## Proxy general

```text
GET    /api/health/database

GET    /api/usuarios
GET    /api/usuarios/{id}
POST   /api/usuarios
PUT    /api/usuarios/{id}
DELETE /api/usuarios/{id}

GET    /api/regiones
GET    /api/regiones/{uuid}
POST   /api/regiones
PUT    /api/regiones/{uuid}
DELETE /api/regiones/{uuid}

GET    /api/comunas
GET    /api/comunas/{uuid}
POST   /api/comunas
PUT    /api/comunas/{uuid}
DELETE /api/comunas/{uuid}

GET    /api/motivos-fallecimiento
GET    /api/motivos-fallecimiento/{uuid}
POST   /api/motivos-fallecimiento
PUT    /api/motivos-fallecimiento/{uuid}
DELETE /api/motivos-fallecimiento/{uuid}

GET    /api/unidades-medida
GET    /api/unidades-medida/{uuid}
POST   /api/unidades-medida
PUT    /api/unidades-medida/{uuid}
DELETE /api/unidades-medida/{uuid}

GET    /api/formas-pago
GET    /api/formas-pago/{uuid}
POST   /api/formas-pago
PUT    /api/formas-pago/{uuid}
DELETE /api/formas-pago/{uuid}

GET    /api/tipos-movimiento
GET    /api/tipos-movimiento/{uuid}
POST   /api/tipos-movimiento
PUT    /api/tipos-movimiento/{uuid}
DELETE /api/tipos-movimiento/{uuid}

GET    /api/suscripcion-planes
GET    /api/suscripcion-planes/{uuid}
POST   /api/suscripcion-planes
PUT    /api/suscripcion-planes/{uuid}
DELETE /api/suscripcion-planes/{uuid}

GET    /api/estados-cotizacion
GET    /api/estados-cotizacion/{uuid}
POST   /api/estados-cotizacion
PUT    /api/estados-cotizacion/{uuid}
DELETE /api/estados-cotizacion/{uuid}

GET    /api/empresas
GET    /api/empresas/{uuid}
GET    /api/empresas/usuario/{usuarioUuid}
POST   /api/empresas
PUT    /api/empresas/{uuid}
PATCH  /api/empresas/{uuid}/desactivar

GET    /api/sucursales
GET    /api/sucursales/{uuid}
GET    /api/sucursales/empresa/{empresaUuid}
POST   /api/sucursales
PUT    /api/sucursales/{uuid}
PATCH  /api/sucursales/{uuid}/desactivar

GET    /api/terceros
GET    /api/terceros/{uuid}
GET    /api/terceros/empresa/{empresaUuid}
GET    /api/terceros/empresa/{empresaUuid}?rol=PROVEEDOR
POST   /api/terceros
PUT    /api/terceros/{uuid}
PATCH  /api/terceros/{uuid}/desactivar

GET    /api/productos-servicios
GET    /api/productos-servicios/{uuid}
GET    /api/productos-servicios/empresa/{empresaUuid}
GET    /api/productos-servicios/empresa/{empresaUuid}?tipoItem=P
GET    /api/productos-servicios/empresa/{empresaUuid}?tipoItem=S
POST   /api/productos-servicios
PUT    /api/productos-servicios/{uuid}
PATCH  /api/productos-servicios/{uuid}/desactivar

GET    /api/planes
GET    /api/planes/{uuid}
GET    /api/planes/sucursal/{sucursalUuid}
POST   /api/planes
PUT    /api/planes/{uuid}
PATCH  /api/planes/{uuid}/desactivar

GET    /api/plan-kit
GET    /api/plan-kit/{uuid}
GET    /api/plan-kit/plan/{planUuid}
POST   /api/plan-kit
PUT    /api/plan-kit/{uuid}
DELETE /api/plan-kit/{uuid}

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
```

## Inventario

Estas rutas se reenvian al microservicio configurado en `inventario.base-url`:

```text
POST  /api/inventario/entradas
POST  /api/inventario/salidas
PATCH /api/inventario/movimientos/{movimientoUuid}/anular
GET   /api/inventario/stock?sucursalUuid={uuid}
GET   /api/inventario/stock/productos/{productoUuid}?sucursalUuid={uuid}
GET   /api/inventario/reportes/kardex?productoUuid={uuid}&sucursalUuid={uuid}
```
