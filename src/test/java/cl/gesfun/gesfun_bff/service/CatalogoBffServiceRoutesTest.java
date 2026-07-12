package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoBffServiceRoutesTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void serviciosConFiltroReenvianRutasEsperadas() {
        when(proxyService.forwardToBackend("/api/planes/sucursal/sucursal-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("planes"));
        when(proxyService.forwardToBackend("/api/productos-servicios/empresa/empresa-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("productos"));
        when(proxyService.forwardToBackend("/api/plan-kit/plan/plan-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("kit"));
        when(proxyService.forwardToBackend("/api/sucursales/empresa/empresa-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("sucursales"));
        when(proxyService.forwardToBackend("/api/empresas/usuario/usuario-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("empresas"));
        when(proxyService.forwardToBackend("/api/tipos-recurso/sucursal/sucursal-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("tipos"));
        when(proxyService.forwardToBackend("/api/health/database", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("up"));

        new PlanBffService(proxyService, objectMapper).listarPorSucursal("sucursal-1", jwt);
        new ProductoServicioBffService(proxyService, objectMapper).listarPorEmpresa("empresa-1", jwt);
        new PlanKitBffService(proxyService, objectMapper).listarPorPlan("plan-1", jwt);
        new SucursalBffService(proxyService, objectMapper).listarPorEmpresa("empresa-1", jwt);
        new EmpresaBffService(proxyService, objectMapper).listarPorUsuario("usuario-1", jwt);
        new TipoRecursoBffService(proxyService, objectMapper).listarPorSucursal("sucursal-1", jwt);
        new DatabaseHealthBffService(proxyService).consultar(jwt);

        verify(proxyService).forwardToBackend("/api/planes/sucursal/sucursal-1", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/productos-servicios/empresa/empresa-1", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/plan-kit/plan/plan-1", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/sucursales/empresa/empresa-1", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/empresas/usuario/usuario-1", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/tipos-recurso/sucursal/sucursal-1", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/health/database", HttpMethod.GET, null, jwt);
    }
}
