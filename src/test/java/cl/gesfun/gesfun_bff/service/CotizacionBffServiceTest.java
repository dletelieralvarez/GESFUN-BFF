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
class CotizacionBffServiceTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    private CotizacionBffService service;

    @BeforeEach
    void setUp() {
        service = new CotizacionBffService(proxyService, new ObjectMapper());
    }

    @Test
    void listarPorSucursalReenviaRutaDelBackend() {
        when(proxyService.forwardToBackend(
                "/api/cotizaciones/sucursal/sucursal-1",
                HttpMethod.GET,
                null,
                jwt
        )).thenReturn(ResponseEntity.ok("ok"));

        service.listarPorSucursal("sucursal-1", jwt);

        verify(proxyService).forwardToBackend(
                "/api/cotizaciones/sucursal/sucursal-1",
                HttpMethod.GET,
                null,
                jwt
        );
    }
}
