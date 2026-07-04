package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.AnulacionMovimientoInventario;
import cl.gesfun.gesfun_bff.model.EntradaInventario;
import cl.gesfun.gesfun_bff.model.MovimientoInventarioDetalle;
import cl.gesfun.gesfun_bff.model.SalidaInventario;
import cl.gesfun.gesfun_bff.model.SalidaInventarioFacturacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class InventarioBffServiceTest {

    @Mock private RestTemplate restTemplate;
    @Mock private Jwt jwt;

    private InventarioBffService service;

    @BeforeEach
    void setUp() {
        service = new InventarioBffService(
                restTemplate,
                new ObjectMapper().findAndRegisterModules(),
                "http://localhost:8100/"
        );
    }

    @Test
    void registraEntradaCompletaComoUnSoloJsonConDosProductos() throws Exception {
        EntradaInventario entrada = new EntradaInventario(
                "sucursal-1",
                "tipo-entrada-1",
                "forma-pago-1",
                "proveedor-1",
                null,
                "usuario-1",
                LocalDate.of(2026, 6, 20),
                LocalDate.of(2026, 6, 20),
                LocalDate.of(2026, 6, 20),
                "OC-POSTMAN-001",
                "GUIA-POSTMAN-001",
                "FACT-POSTMAN-001",
                "Entrada creada desde Postman",
                List.of(
                        new MovimientoInventarioDetalle(
                                "producto-1",
                                new BigDecimal("3"),
                                new BigDecimal("180000"),
                                BigDecimal.ZERO,
                                "Primer producto"
                        ),
                        new MovimientoInventarioDetalle(
                                "producto-2",
                                new BigDecimal("4"),
                                new BigDecimal("90000"),
                                BigDecimal.ZERO,
                                "Segundo producto"
                        )
                )
        );

        when(jwt.getTokenValue()).thenReturn("token");
        mockExchange("http://localhost:8100/api/inventario/entradas", HttpMethod.POST);

        service.registrarEntrada(entrada, jwt);

        ArgumentCaptor<HttpEntity<String>> entity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq(URI.create("http://localhost:8100/api/inventario/entradas")),
                eq(HttpMethod.POST),
                entity.capture(),
                eq(Object.class)
        );
        assertThat(entity.getValue().getBody())
                .contains("\"sucursalUuid\":\"sucursal-1\"")
                .contains("\"tipoMovimientoUuid\":\"tipo-entrada-1\"")
                .contains("\"formaPagoUuid\":\"forma-pago-1\"")
                .contains("\"terceroUuid\":\"proveedor-1\"")
                .contains("\"usuarioUuid\":\"usuario-1\"")
                .contains("\"fechaDocumento\":\"2026-06-20\"")
                .contains("\"numeroOc\":\"OC-POSTMAN-001\"")
                .contains("\"productoUuid\":\"producto-1\"")
                .contains("\"cantidad\":3")
                .contains("\"costoUnitario\":180000")
                .contains("\"productoUuid\":\"producto-2\"")
                .contains("\"cantidad\":4")
                .contains("\"costoUnitario\":90000");
        assertThat(entity.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer token");
    }

    @Test
    void registraSalidasYAnulacionEnInventario() throws Exception {
        SalidaInventario salida = new SalidaInventario(
                "sucursal", "tipo", null, null, null, "usuario",
                null, null, null, null, null
        );
        AnulacionMovimientoInventario anulacion =
                new AnulacionMovimientoInventario("Error de digitacion", "usuario");

        mockExchange("http://localhost:8100/api/inventario/salidas", HttpMethod.POST);
        mockExchange("http://localhost:8100/api/inventario/salidas/facturacion", HttpMethod.POST);
        mockExchange("http://localhost:8100/api/inventario/movimientos/mov-1/anular", HttpMethod.PATCH);

        service.registrarSalida(salida, null);
        service.registrarSalidaPorFacturacion(
                new SalidaInventarioFacturacion(
                        "doc-1",
                        "cotizacion-1",
                        "usuario-1",
                        LocalDate.of(2026, 7, 3),
                        "F12345",
                        "Salida generada desde facturacion"
                ),
                null
        );
        service.anularMovimiento("mov-1", anulacion, null);
    }

    @Test
    void consultaStockYKardexConParametros() {
        mockExchange(
                "http://localhost:8100/api/inventario/stock?sucursalUuid=sucursal-1",
                HttpMethod.GET
        );
        mockExchange(
                "http://localhost:8100/api/inventario/stock/productos/producto-1?sucursalUuid=sucursal-1",
                HttpMethod.GET
        );
        mockExchange(
                "http://localhost:8100/api/inventario/reportes/kardex?productoUuid=producto-1&sucursalUuid=sucursal-1",
                HttpMethod.GET
        );

        service.consultarStockSucursal("sucursal-1", null);
        service.consultarStockProducto("producto-1", "sucursal-1", null);
        service.consultarKardex("producto-1", "sucursal-1", null);
    }

    private void mockExchange(String uri, HttpMethod method) {
        when(restTemplate.exchange(
                eq(URI.create(uri)),
                eq(method),
                org.mockito.ArgumentMatchers.<HttpEntity<String>>any(),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok("ok"));
    }
}
