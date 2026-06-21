package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.AnulacionMovimientoInventario;
import cl.gesfun.gesfun_bff.model.EntradaInventario;
import cl.gesfun.gesfun_bff.model.SalidaInventario;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
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
    void registraEntradasSalidasYAnulacionEnInventario() throws Exception {
        EntradaInventario entrada = new EntradaInventario(
                "sucursal", "tipo", null, null, null, "usuario",
                null, null, null, null, null, null, null, null
        );
        SalidaInventario salida = new SalidaInventario(
                "sucursal", "tipo", null, null, null, "usuario",
                null, null, null, null, null
        );
        AnulacionMovimientoInventario anulacion =
                new AnulacionMovimientoInventario("Error de digitacion", "usuario");

        when(jwt.getTokenValue()).thenReturn("token");
        mockExchange("http://localhost:8100/api/inventario/entradas", HttpMethod.POST);
        mockExchange("http://localhost:8100/api/inventario/salidas", HttpMethod.POST);
        mockExchange("http://localhost:8100/api/inventario/movimientos/mov-1/anular", HttpMethod.PATCH);

        service.registrarEntrada(entrada, jwt);
        service.registrarSalida(salida, jwt);
        service.anularMovimiento("mov-1", anulacion, jwt);

        ArgumentCaptor<HttpEntity<String>> entity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq(URI.create("http://localhost:8100/api/inventario/entradas")),
                eq(HttpMethod.POST),
                entity.capture(),
                eq(Object.class)
        );
        assertThat(entity.getValue().getBody()).contains("\"sucursalUuid\":\"sucursal\"");
        assertThat(entity.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer token");
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
