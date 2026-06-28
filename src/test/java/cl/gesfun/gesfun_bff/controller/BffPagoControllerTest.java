package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.PagoCreate;
import cl.gesfun.gesfun_bff.model.PagoUpdate;
import cl.gesfun.gesfun_bff.service.PagoBffService;
import java.math.BigDecimal;
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
class BffPagoControllerTest {

    @Mock
    private PagoBffService service;

    @Mock
    private Jwt jwt;

    private BffPagoController controller;

    @BeforeEach
    void setUp() {
        controller = new BffPagoController(service);
    }

    @Test
    void listarUsaServicio() {
        when(service.listar(jwt)).thenReturn(ResponseEntity.ok(List.of()));

        controller.listar(jwt);

        verify(service).listar(jwt);
    }

    @Test
    void crearUsaServicio() throws Exception {
        PagoCreate request = new PagoCreate("cotizacion-1", "forma-pago-1", BigDecimal.TEN, null, null);
        when(service.crear(request, jwt)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uuid", "pago-1")));

        ResponseEntity<FrontendResponse<Object>> response = controller.crear(request, jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service).crear(request, jwt);
    }

    @Test
    void actualizarUsaUuidYBody() throws Exception {
        PagoUpdate request = new PagoUpdate("forma-pago-1", BigDecimal.TEN, null, "REGISTRADO", null);
        when(service.actualizar("pago-1", request, jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "pago-1")));

        controller.actualizar("pago-1", request, jwt);

        verify(service).actualizar("pago-1", request, jwt);
    }

    @Test
    void listarPorCotizacionUsaCotizacionUuid() {
        when(service.listarPorCotizacion("cotizacion-1", jwt)).thenReturn(ResponseEntity.ok(List.of()));

        controller.listarPorCotizacion("cotizacion-1", jwt);

        verify(service).listarPorCotizacion("cotizacion-1", jwt);
    }

    @Test
    void anularUsaUuid() {
        when(service.anular("pago-1", jwt)).thenReturn(ResponseEntity.ok(Map.of("estado", "ANULADO")));

        controller.anular("pago-1", jwt);

        verify(service).anular("pago-1", jwt);
    }
}
