package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.Comuna;
import cl.gesfun.gesfun_bff.model.Empresa;
import cl.gesfun.gesfun_bff.model.EstadoCotizacion;
import cl.gesfun.gesfun_bff.model.FormaPago;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.MotivoFallecimiento;
import cl.gesfun.gesfun_bff.model.Plan;
import cl.gesfun.gesfun_bff.model.PlanKit;
import cl.gesfun.gesfun_bff.model.ProductoServicio;
import cl.gesfun.gesfun_bff.model.Region;
import cl.gesfun.gesfun_bff.model.Sucursal;
import cl.gesfun.gesfun_bff.model.SuscripcionPlan;
import cl.gesfun.gesfun_bff.model.TipoMovimiento;
import cl.gesfun.gesfun_bff.model.UnidadMedida;
import cl.gesfun.gesfun_bff.model.Usuario;
import cl.gesfun.gesfun_bff.service.ComunaBffService;
import cl.gesfun.gesfun_bff.service.DatabaseHealthBffService;
import cl.gesfun.gesfun_bff.service.EmpresaBffService;
import cl.gesfun.gesfun_bff.service.EstadoCotizacionBffService;
import cl.gesfun.gesfun_bff.service.FormaPagoBffService;
import cl.gesfun.gesfun_bff.service.MotivoFallecimientoBffService;
import cl.gesfun.gesfun_bff.service.PlanBffService;
import cl.gesfun.gesfun_bff.service.PlanKitBffService;
import cl.gesfun.gesfun_bff.service.ProductoServicioBffService;
import cl.gesfun.gesfun_bff.service.ProxyService;
import cl.gesfun.gesfun_bff.service.RegionBffService;
import cl.gesfun.gesfun_bff.service.SucursalBffService;
import cl.gesfun.gesfun_bff.service.SuscripcionPlanBffService;
import cl.gesfun.gesfun_bff.service.TipoMovimientoBffService;
import cl.gesfun.gesfun_bff.service.UnidadMedidaBffService;
import cl.gesfun.gesfun_bff.service.UsuarioBffService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllControllersCoverageTest {

    @Mock private Jwt jwt;
    @Mock private UsuarioBffService usuarioService;
    @Mock private PlanBffService planService;
    @Mock private ProductoServicioBffService productoServicioService;
    @Mock private SucursalBffService sucursalService;
    @Mock private EmpresaBffService empresaService;
    @Mock private PlanKitBffService planKitService;
    @Mock private ComunaBffService comunaService;
    @Mock private RegionBffService regionService;
    @Mock private UnidadMedidaBffService unidadMedidaService;
    @Mock private TipoMovimientoBffService tipoMovimientoService;
    @Mock private FormaPagoBffService formaPagoService;
    @Mock private EstadoCotizacionBffService estadoCotizacionService;
    @Mock private MotivoFallecimientoBffService motivoFallecimientoService;
    @Mock private SuscripcionPlanBffService suscripcionPlanService;
    @Mock private DatabaseHealthBffService databaseHealthService;
    @Mock private ProxyService proxyService;
    @Mock private HttpServletRequest request;

    @Test
    void usuarioControllerCubreCrudCompleto() throws Exception {
        BffUsuarioController controller = new BffUsuarioController(usuarioService);
        Usuario body = null;
        when(usuarioService.listar(jwt)).thenReturn(ok("listar"));
        when(usuarioService.buscarPorId(1, jwt)).thenReturn(ok("buscar"));
        when(usuarioService.crear(body, jwt)).thenReturn(created());
        when(usuarioService.actualizar(1, body, jwt)).thenReturn(ok("actualizar"));
        when(usuarioService.eliminar(1, jwt)).thenReturn(noContent());

        assertPayload(controller.listar(jwt), "listar");
        assertPayload(controller.buscarPorId(1, jwt), "buscar");
        assertThat(controller.crear(body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertPayload(controller.actualizar(1, body, jwt), "actualizar");
        assertThat(controller.eliminar(1, jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(usuarioService).listar(jwt);
        verify(usuarioService).buscarPorId(1, jwt);
        verify(usuarioService).crear(body, jwt);
        verify(usuarioService).actualizar(1, body, jwt);
        verify(usuarioService).eliminar(1, jwt);
    }

    @Test
    void planControllerCubreCrudConSucursalYDesactivar() throws Exception {
        BffPlanController controller = new BffPlanController(planService);
        Plan body = null;
        when(planService.listar(jwt)).thenReturn(ok("listar"));
        when(planService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
        when(planService.listarPorSucursal("sucursal", jwt)).thenReturn(ok("sucursal"));
        when(planService.crear(body, jwt)).thenReturn(created());
        when(planService.actualizar("uuid", body, jwt)).thenReturn(ok("actualizar"));
        when(planService.desactivar("uuid", jwt)).thenReturn(ok("desactivar"));

        assertPayload(controller.listar(jwt), "listar");
        assertPayload(controller.buscarPorUuid("uuid", jwt), "buscar");
        assertPayload(controller.listarPorSucursal("sucursal", jwt), "sucursal");
        assertThat(controller.crear(body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertPayload(controller.actualizar("uuid", body, jwt), "actualizar");
        assertPayload(controller.desactivar("uuid", jwt), "desactivar");
    }

    @Test
    void controllersConEmpresaYDesactivarCubrenCrudCompleto() throws Exception {
        ProductoServicio producto = null;
        BffProductoServicioController productoController = new BffProductoServicioController(productoServicioService);
        when(productoServicioService.listar(jwt)).thenReturn(ok("listar"));
        when(productoServicioService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
        when(productoServicioService.listarPorEmpresa("empresa", jwt)).thenReturn(ok("empresa"));
        when(productoServicioService.crear(producto, jwt)).thenReturn(created());
        when(productoServicioService.actualizar("uuid", producto, jwt)).thenReturn(ok("actualizar"));
        when(productoServicioService.desactivar("uuid", jwt)).thenReturn(ok("desactivar"));
        assertPayload(productoController.listar(jwt), "listar");
        assertPayload(productoController.buscarPorUuid("uuid", jwt), "buscar");
        assertPayload(productoController.listarPorEmpresa("empresa", jwt), "empresa");
        assertThat(productoController.crear(producto, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertPayload(productoController.actualizar("uuid", producto, jwt), "actualizar");
        assertPayload(productoController.desactivar("uuid", jwt), "desactivar");

        Sucursal sucursal = null;
        BffSucursalController sucursalController = new BffSucursalController(sucursalService);
        when(sucursalService.listar(jwt)).thenReturn(ok("listar"));
        when(sucursalService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
        when(sucursalService.listarPorEmpresa("empresa", jwt)).thenReturn(ok("empresa"));
        when(sucursalService.crear(sucursal, jwt)).thenReturn(created());
        when(sucursalService.actualizar("uuid", sucursal, jwt)).thenReturn(ok("actualizar"));
        when(sucursalService.desactivar("uuid", jwt)).thenReturn(ok("desactivar"));
        assertPayload(sucursalController.listar(jwt), "listar");
        assertPayload(sucursalController.buscarPorUuid("uuid", jwt), "buscar");
        assertPayload(sucursalController.listarPorEmpresa("empresa", jwt), "empresa");
        assertThat(sucursalController.crear(sucursal, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertPayload(sucursalController.actualizar("uuid", sucursal, jwt), "actualizar");
        assertPayload(sucursalController.desactivar("uuid", jwt), "desactivar");
    }

    @Test
    void empresaControllerCubreCrudConUsuarioYDesactivar() throws Exception {
        BffEmpresaController controller = new BffEmpresaController(empresaService);
        Empresa body = null;
        when(empresaService.listar(jwt)).thenReturn(ok("listar"));
        when(empresaService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
        when(empresaService.listarPorUsuario("usuario", jwt)).thenReturn(ok("usuario"));
        when(empresaService.crear(body, jwt)).thenReturn(created());
        when(empresaService.actualizar("uuid", body, jwt)).thenReturn(ok("actualizar"));
        when(empresaService.desactivar("uuid", jwt)).thenReturn(ok("desactivar"));

        assertPayload(controller.listar(jwt), "listar");
        assertPayload(controller.buscarPorUuid("uuid", jwt), "buscar");
        assertPayload(controller.listarPorUsuario("usuario", jwt), "usuario");
        assertThat(controller.crear(body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertPayload(controller.actualizar("uuid", body, jwt), "actualizar");
        assertPayload(controller.desactivar("uuid", jwt), "desactivar");
    }

    @Test
    void planKitControllerCubreCrudConPlan() throws Exception {
        BffPlanKitController controller = new BffPlanKitController(planKitService);
        PlanKit body = null;
        when(planKitService.listar(jwt)).thenReturn(ok("listar"));
        when(planKitService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
        when(planKitService.listarPorPlan("plan", jwt)).thenReturn(ok("plan"));
        when(planKitService.crear(body, jwt)).thenReturn(created());
        when(planKitService.actualizar("uuid", body, jwt)).thenReturn(ok("actualizar"));
        when(planKitService.eliminar("uuid", jwt)).thenReturn(noContent());

        assertPayload(controller.listar(jwt), "listar");
        assertPayload(controller.buscarPorUuid("uuid", jwt), "buscar");
        assertPayload(controller.listarPorPlan("plan", jwt), "plan");
        assertThat(controller.crear(body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertPayload(controller.actualizar("uuid", body, jwt), "actualizar");
        assertThat(controller.eliminar("uuid", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void catalogControllersCubrenCrudBasico() throws Exception {
        assertCrudBasico(new BffComunaController(comunaService), comunaService, (Comuna) null);
        assertCrudBasico(new BffRegionController(regionService), regionService, (Region) null);
        assertCrudBasico(new BffUnidadMedidaController(unidadMedidaService), unidadMedidaService, (UnidadMedida) null);
        assertCrudBasico(new BffTipoMovimientoController(tipoMovimientoService), tipoMovimientoService, (TipoMovimiento) null);
        assertCrudBasico(new BffFormaPagoController(formaPagoService), formaPagoService, (FormaPago) null);
        assertCrudBasico(new BffEstadoCotizacionController(estadoCotizacionService), estadoCotizacionService, (EstadoCotizacion) null);
        assertCrudBasico(new BffMotivoFallecimientoController(motivoFallecimientoService), motivoFallecimientoService, (MotivoFallecimiento) null);
        assertCrudBasico(new BffSuscripcionPlanController(suscripcionPlanService), suscripcionPlanService, (SuscripcionPlan) null);
    }

    @Test
    void databaseHealthControllerConsultaBackend() {
        BffDatabaseHealthController controller = new BffDatabaseHealthController(databaseHealthService);
        when(databaseHealthService.consultar(jwt)).thenReturn(ok("up"));

        assertPayload(controller.consultar(jwt), "up");

        verify(databaseHealthService).consultar(jwt);
    }

    @Test
    void proxyControllerReenviaMetodoBodyYJwt() {
        BffProxyController controller = new BffProxyController(proxyService);
        when(request.getMethod()).thenReturn("PATCH");
        when(proxyService.forwardRequest(request, HttpMethod.PATCH, "{\"activo\":0}", jwt))
                .thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).body("ok"));

        ResponseEntity<FrontendResponse<Object>> response = controller.proxyRequest(request, "{\"activo\":0}", jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertPayload(response, "ok");
        verify(proxyService).forwardRequest(request, HttpMethod.PATCH, "{\"activo\":0}", jwt);
    }

    @Test
    void diagnosticsControllerDevuelveClaimsEsperados() {
        BffDiagnosticsController controller = new BffDiagnosticsController();
        Jwt token = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("subject-1")
                .issuer("https://issuer.test")
                .audience(List.of("audience"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claim("scp", "access_as_user")
                .claim("tid", "tenant-1")
                .claim("ver", "2.0")
                .claim("appid", "client-1")
                .claim("upn", "user@gesfun.cl")
                .build();

        Map<String, Object> response = controller.me(token);

        assertThat(response)
                .containsEntry("authenticated", true)
                .containsEntry("subject", "subject-1")
                .containsEntry("issuer", "https://issuer.test")
                .containsEntry("scope", "access_as_user")
                .containsEntry("tenant", "tenant-1")
                .containsEntry("version", "2.0")
                .containsEntry("clientId", "client-1")
                .containsEntry("username", "user@gesfun.cl");
        assertThat(response.get("audience")).isEqualTo(List.of("audience"));
    }

    private <T> void assertCrudBasico(Object controller, Object service, T body) throws Exception {
        if (controller instanceof BffComunaController typed && service instanceof ComunaBffService typedService) {
            when(typedService.listar(jwt)).thenReturn(ok("listar"));
            when(typedService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
            when(typedService.crear((Comuna) body, jwt)).thenReturn(created());
            when(typedService.actualizar("uuid", (Comuna) body, jwt)).thenReturn(ok("actualizar"));
            when(typedService.eliminar("uuid", jwt)).thenReturn(noContent());
            assertPayload(typed.listar(jwt), "listar");
            assertPayload(typed.buscarPorUuid("uuid", jwt), "buscar");
            assertThat(typed.crear((Comuna) body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertPayload(typed.actualizar("uuid", (Comuna) body, jwt), "actualizar");
            assertThat(typed.eliminar("uuid", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } else if (controller instanceof BffRegionController typed && service instanceof RegionBffService typedService) {
            when(typedService.listar(jwt)).thenReturn(ok("listar"));
            when(typedService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
            when(typedService.crear((Region) body, jwt)).thenReturn(created());
            when(typedService.actualizar("uuid", (Region) body, jwt)).thenReturn(ok("actualizar"));
            when(typedService.eliminar("uuid", jwt)).thenReturn(noContent());
            assertPayload(typed.listar(jwt), "listar");
            assertPayload(typed.buscarPorUuid("uuid", jwt), "buscar");
            assertThat(typed.crear((Region) body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertPayload(typed.actualizar("uuid", (Region) body, jwt), "actualizar");
            assertThat(typed.eliminar("uuid", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } else if (controller instanceof BffUnidadMedidaController typed && service instanceof UnidadMedidaBffService typedService) {
            when(typedService.listar(jwt)).thenReturn(ok("listar"));
            when(typedService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
            when(typedService.crear((UnidadMedida) body, jwt)).thenReturn(created());
            when(typedService.actualizar("uuid", (UnidadMedida) body, jwt)).thenReturn(ok("actualizar"));
            when(typedService.eliminar("uuid", jwt)).thenReturn(noContent());
            assertPayload(typed.listar(jwt), "listar");
            assertPayload(typed.buscarPorUuid("uuid", jwt), "buscar");
            assertThat(typed.crear((UnidadMedida) body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertPayload(typed.actualizar("uuid", (UnidadMedida) body, jwt), "actualizar");
            assertThat(typed.eliminar("uuid", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } else if (controller instanceof BffTipoMovimientoController typed && service instanceof TipoMovimientoBffService typedService) {
            when(typedService.listar(jwt)).thenReturn(ok("listar"));
            when(typedService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
            when(typedService.crear((TipoMovimiento) body, jwt)).thenReturn(created());
            when(typedService.actualizar("uuid", (TipoMovimiento) body, jwt)).thenReturn(ok("actualizar"));
            when(typedService.eliminar("uuid", jwt)).thenReturn(noContent());
            assertPayload(typed.listar(jwt), "listar");
            assertPayload(typed.buscarPorUuid("uuid", jwt), "buscar");
            assertThat(typed.crear((TipoMovimiento) body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertPayload(typed.actualizar("uuid", (TipoMovimiento) body, jwt), "actualizar");
            assertThat(typed.eliminar("uuid", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } else if (controller instanceof BffFormaPagoController typed && service instanceof FormaPagoBffService typedService) {
            when(typedService.listar(jwt)).thenReturn(ok("listar"));
            when(typedService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
            when(typedService.crear((FormaPago) body, jwt)).thenReturn(created());
            when(typedService.actualizar("uuid", (FormaPago) body, jwt)).thenReturn(ok("actualizar"));
            when(typedService.eliminar("uuid", jwt)).thenReturn(noContent());
            assertPayload(typed.listar(jwt), "listar");
            assertPayload(typed.buscarPorUuid("uuid", jwt), "buscar");
            assertThat(typed.crear((FormaPago) body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertPayload(typed.actualizar("uuid", (FormaPago) body, jwt), "actualizar");
            assertThat(typed.eliminar("uuid", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } else if (controller instanceof BffEstadoCotizacionController typed && service instanceof EstadoCotizacionBffService typedService) {
            when(typedService.listar(jwt)).thenReturn(ok("listar"));
            when(typedService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
            when(typedService.crear((EstadoCotizacion) body, jwt)).thenReturn(created());
            when(typedService.actualizar("uuid", (EstadoCotizacion) body, jwt)).thenReturn(ok("actualizar"));
            when(typedService.eliminar("uuid", jwt)).thenReturn(noContent());
            assertPayload(typed.listar(jwt), "listar");
            assertPayload(typed.buscarPorUuid("uuid", jwt), "buscar");
            assertThat(typed.crear((EstadoCotizacion) body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertPayload(typed.actualizar("uuid", (EstadoCotizacion) body, jwt), "actualizar");
            assertThat(typed.eliminar("uuid", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } else if (controller instanceof BffMotivoFallecimientoController typed && service instanceof MotivoFallecimientoBffService typedService) {
            when(typedService.listar(jwt)).thenReturn(ok("listar"));
            when(typedService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
            when(typedService.crear((MotivoFallecimiento) body, jwt)).thenReturn(created());
            when(typedService.actualizar("uuid", (MotivoFallecimiento) body, jwt)).thenReturn(ok("actualizar"));
            when(typedService.eliminar("uuid", jwt)).thenReturn(noContent());
            assertPayload(typed.listar(jwt), "listar");
            assertPayload(typed.buscarPorUuid("uuid", jwt), "buscar");
            assertThat(typed.crear((MotivoFallecimiento) body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertPayload(typed.actualizar("uuid", (MotivoFallecimiento) body, jwt), "actualizar");
            assertThat(typed.eliminar("uuid", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        } else if (controller instanceof BffSuscripcionPlanController typed && service instanceof SuscripcionPlanBffService typedService) {
            when(typedService.listar(jwt)).thenReturn(ok("listar"));
            when(typedService.buscarPorUuid("uuid", jwt)).thenReturn(ok("buscar"));
            when(typedService.crear((SuscripcionPlan) body, jwt)).thenReturn(created());
            when(typedService.actualizar("uuid", (SuscripcionPlan) body, jwt)).thenReturn(ok("actualizar"));
            when(typedService.eliminar("uuid", jwt)).thenReturn(noContent());
            assertPayload(typed.listar(jwt), "listar");
            assertPayload(typed.buscarPorUuid("uuid", jwt), "buscar");
            assertThat(typed.crear((SuscripcionPlan) body, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertPayload(typed.actualizar("uuid", (SuscripcionPlan) body, jwt), "actualizar");
            assertThat(typed.eliminar("uuid", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    private ResponseEntity<Object> ok(Object payload) {
        return ResponseEntity.ok(payload);
    }

    private ResponseEntity<Object> created() {
        return ResponseEntity.status(HttpStatus.CREATED).body("created");
    }

    private ResponseEntity<Object> noContent() {
        return ResponseEntity.noContent().build();
    }

    private void assertPayload(ResponseEntity<FrontendResponse<Object>> response, Object expectedPayload) {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getPayload()).isEqualTo(expectedPayload);
    }
}
