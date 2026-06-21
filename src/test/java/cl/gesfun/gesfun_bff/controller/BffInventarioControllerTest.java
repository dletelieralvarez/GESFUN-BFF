package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.AnulacionMovimientoInventario;
import cl.gesfun.gesfun_bff.model.EntradaInventario;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.SalidaInventario;
import cl.gesfun.gesfun_bff.service.InventarioBffService;
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
class BffInventarioControllerTest {

    @Mock private InventarioBffService service;
    @Mock private Jwt jwt;

    private BffInventarioController controller;

    @BeforeEach
    void setUp() {
        controller = new BffInventarioController(service);
    }

    @Test
    void reenviaOperacionesDeMovimiento() throws Exception {
        EntradaInventario entrada = null;
        SalidaInventario salida = null;
        AnulacionMovimientoInventario anulacion = null;
        when(service.registrarEntrada(entrada, jwt)).thenReturn(created());
        when(service.registrarSalida(salida, jwt)).thenReturn(created());
        when(service.anularMovimiento("mov-1", anulacion, jwt)).thenReturn(ResponseEntity.ok("anulado"));

        assertSuccess(controller.registrarEntrada(entrada, jwt), HttpStatus.CREATED);
        assertSuccess(controller.registrarSalida(salida, jwt), HttpStatus.CREATED);
        assertSuccess(controller.anularMovimiento("mov-1", anulacion, jwt), HttpStatus.OK);

        verify(service).registrarEntrada(entrada, jwt);
        verify(service).registrarSalida(salida, jwt);
        verify(service).anularMovimiento("mov-1", anulacion, jwt);
    }

    @Test
    void reenviaConsultasDeStockYKardex() {
        when(service.consultarStockSucursal("sucursal", jwt)).thenReturn(ResponseEntity.ok("stock"));
        when(service.consultarStockProducto("producto", "sucursal", jwt))
                .thenReturn(ResponseEntity.ok("producto"));
        when(service.consultarKardex("producto", "sucursal", jwt))
                .thenReturn(ResponseEntity.ok("kardex"));

        assertSuccess(controller.consultarStockSucursal("sucursal", jwt), HttpStatus.OK);
        assertSuccess(
                controller.consultarStockProducto("producto", "sucursal", jwt),
                HttpStatus.OK
        );
        assertSuccess(controller.consultarKardex("producto", "sucursal", jwt), HttpStatus.OK);

        verify(service).consultarStockSucursal("sucursal", jwt);
        verify(service).consultarStockProducto("producto", "sucursal", jwt);
        verify(service).consultarKardex("producto", "sucursal", jwt);
    }

    private ResponseEntity<Object> created() {
        return ResponseEntity.status(HttpStatus.CREATED).body("created");
    }

    private void assertSuccess(
            ResponseEntity<FrontendResponse<Object>> response,
            HttpStatus status
    ) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
    }
}
