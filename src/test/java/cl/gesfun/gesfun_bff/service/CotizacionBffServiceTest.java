package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.Cotizacion;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    void listarReenviaRutaBaseDelBackend() {
        when(proxyService.forwardToBackend(
                "/api/cotizaciones",
                HttpMethod.GET,
                null,
                jwt
        )).thenReturn(ResponseEntity.ok("ok"));

        service.listar(jwt);

        verify(proxyService).forwardToBackend(
                "/api/cotizaciones",
                HttpMethod.GET,
                null,
                jwt
        );
    }

    @Test
    void actualizarEstadoReenviaPatchAlBackend() throws Exception {
        var request = new cl.gesfun.gesfun_bff.model.CotizacionEstadoUpdate("estado-1");
        when(proxyService.forwardToBackend(
                "/api/cotizaciones/cotizacion-1/estado",
                HttpMethod.PATCH,
                "{\"estadoUuid\":\"estado-1\"}",
                jwt
        )).thenReturn(ResponseEntity.ok("ok"));

        service.actualizarEstado("cotizacion-1", request, jwt);

        verify(proxyService).forwardToBackend(
                "/api/cotizaciones/cotizacion-1/estado",
                HttpMethod.PATCH,
                "{\"estadoUuid\":\"estado-1\"}",
                jwt
        );
    }

    @Test
    void crearCotizacionFuerzaPagadorClienteYFallecidoFallecido() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        service = new CotizacionBffService(proxyService, objectMapper);
        Cotizacion request = objectMapper.readValue("""
                {
                  "sucursalUuid": "sucursal-1",
                  "planUuid": "plan-1",
                  "formaPagoUuid": "forma-pago-1",
                  "motivoFallecimientoUuid": "motivo-1",
                  "pagador": {
                    "uuid": "tercero-1",
                    "terceroUuid": "tercero-1",
                    "tipoPersona": "N",
                    "rol": "PAGADOR",
                    "rut": 12345678,
                    "dv": "9",
                    "nombreCompleto": "Nombre Cliente",
                    "email": "cliente@correo.cl",
                    "comunaUuid": "comuna-1"
                  },
                  "fallecido": {
                    "tipoPersona": "N",
                    "rut": 11111111,
                    "dv": "1",
                    "nombreCompleto": "Nombre Fallecido",
                    "comunaUuid": "comuna-1"
                  },
                  "detalles": [
                    {
                      "productoServicioUuid": "producto-1",
                      "cantidad": 1,
                      "descuento": 0
                    }
                  ]
                }
                """, Cotizacion.class);
        when(proxyService.forwardToBackend(eq("/api/cotizaciones"), eq(HttpMethod.POST), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("ok"));

        service.crear(request, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(
                eq("/api/cotizaciones"),
                eq(HttpMethod.POST),
                bodyCaptor.capture(),
                eq(jwt)
        );
        var body = objectMapper.readTree(bodyCaptor.getValue());
        assertThat(body.path("pagador").path("uuid").asText()).isEqualTo("tercero-1");
        assertThat(body.path("pagador").path("terceroUuid").asText()).isEqualTo("tercero-1");
        assertThat(body.path("pagador").path("rol").asText()).isEqualTo("CLIENTE");
        assertThat(body.path("fallecido").path("rol").asText()).isEqualTo("FALLECIDO");
    }
}
