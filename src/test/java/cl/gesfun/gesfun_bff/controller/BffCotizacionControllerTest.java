package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.Cotizacion;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.CotizacionBffService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BffCotizacionControllerTest {

    @Mock
    private CotizacionBffService service;

    @Mock
    private Jwt jwt;

    private BffCotizacionController controller;

    @BeforeEach
    void setUp() {
        controller = new BffCotizacionController(service);
    }

    @Test
    void crearEnviaCotizacionAlService() throws Exception {
        Cotizacion cotizacion = cotizacion();
        when(service.crear(cotizacion, jwt))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uuid", "cotizacion-1")));

        ResponseEntity<FrontendResponse<Object>> response = controller.crear(cotizacion, jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        verify(service).crear(cotizacion, jwt);
    }

    @Test
    void buscarPorUuidUsaUuid() {
        when(service.buscarPorUuid("cotizacion-1", jwt))
                .thenReturn(ResponseEntity.ok(Map.of("uuid", "cotizacion-1")));

        controller.buscarPorUuid("cotizacion-1", jwt);

        verify(service).buscarPorUuid("cotizacion-1", jwt);
    }

    @Test
    void listarPorSucursalUsaSucursalUuid() {
        when(service.listarPorSucursal("sucursal-1", jwt))
                .thenReturn(ResponseEntity.ok(List.of()));

        controller.listarPorSucursal("sucursal-1", jwt);

        verify(service).listarPorSucursal("sucursal-1", jwt);
    }

    private Cotizacion cotizacion() {
        return new Cotizacion(
                "sucursal-1",
                "plan-1",
                "forma-pago-1",
                "motivo-1",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
    }
}
