package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.DocumentoTributarioEmitir;
import cl.gesfun.gesfun_bff.model.SalidaInventarioFacturacion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
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
class DocumentoTributarioBffServiceTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    @Mock
    private InventarioBffService inventarioBffService;

    private DocumentoTributarioBffService service;

    @BeforeEach
    void setUp() {
        service = new DocumentoTributarioBffService(proxyService, new ObjectMapper(), inventarioBffService);
    }

    @Test
    void buscarPorPagoReenviaRutaEsperada() {
        when(proxyService.forwardToBackend("/api/documentos-tributarios/pago/pago-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.buscarPorPago("pago-1", jwt);

        verify(proxyService).forwardToBackend("/api/documentos-tributarios/pago/pago-1", HttpMethod.GET, null, jwt);
    }

    @Test
    void listarPorCotizacionReenviaRutaEsperada() {
        when(proxyService.forwardToBackend("/api/documentos-tributarios/cotizacion/cotizacion-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.listarPorCotizacion("cotizacion-1", jwt);

        verify(proxyService).forwardToBackend("/api/documentos-tributarios/cotizacion/cotizacion-1", HttpMethod.GET, null, jwt);
    }

    @Test
    void emitirReenviaPostConBody() throws Exception {
        DocumentoTributarioEmitir request = new DocumentoTributarioEmitir(
                "pago-1",
                "BOLETA",
                "Emision por pago de servicio funerario"
        );
        when(proxyService.forwardToBackend(eq("/api/documentos-tributarios/emitir"), eq(HttpMethod.POST), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok(documentoResponse("BOLETA")));
        when(jwt.getClaimAsString("usuarioUuid")).thenReturn("usuario-1");

        service.emitir(request, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/documentos-tributarios/emitir"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode body = new ObjectMapper().readTree(bodyCaptor.getValue());
        assertThat(body.get("pagoUuid").asText()).isEqualTo("pago-1");
        assertThat(body.get("tipoDocumentoCodigo").asText()).isEqualTo("BOLETA");

        ArgumentCaptor<SalidaInventarioFacturacion> salidaCaptor =
                ArgumentCaptor.forClass(SalidaInventarioFacturacion.class);
        verify(inventarioBffService).registrarSalidaPorFacturacion(salidaCaptor.capture(), eq(jwt));
        assertThat(salidaCaptor.getValue().documentoTributarioUuid()).isEqualTo("doc-1");
        assertThat(salidaCaptor.getValue().cotizacionUuid()).isEqualTo("cotizacion-1");
        assertThat(salidaCaptor.getValue().usuarioUuid()).isEqualTo("usuario-1");
        assertThat(salidaCaptor.getValue().numeroFactura()).isEqualTo("12345");
    }

    @Test
    void emitirReenviaFacturaComoCodigoTributario() throws Exception {
        DocumentoTributarioEmitir request = new DocumentoTributarioEmitir(
                "pago-1",
                "FACTURA",
                "Emision por pago de servicio funerario"
        );
        when(proxyService.forwardToBackend(eq("/api/documentos-tributarios/emitir"), eq(HttpMethod.POST), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok(documentoResponse("FACTURA")));
        when(jwt.getClaimAsString("usuarioUuid")).thenReturn("usuario-1");

        service.emitir(request, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/documentos-tributarios/emitir"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode body = new ObjectMapper().readTree(bodyCaptor.getValue());
        assertThat(body.get("tipoDocumentoCodigo").asText()).isEqualTo("FACTURA");
    }

    @Test
    void reenviarUsaPostConUuid() {
        when(proxyService.forwardToBackend("/api/documentos-tributarios/doc-1/reenviar", HttpMethod.POST, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.reenviar("doc-1", jwt);

        verify(proxyService).forwardToBackend("/api/documentos-tributarios/doc-1/reenviar", HttpMethod.POST, null, jwt);
    }

    @Test
    void anularUsaPatchConUuid() {
        when(proxyService.forwardToBackend("/api/documentos-tributarios/doc-1/anular", HttpMethod.PATCH, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.anular("doc-1", jwt);

        verify(proxyService).forwardToBackend("/api/documentos-tributarios/doc-1/anular", HttpMethod.PATCH, null, jwt);
    }

    private Map<String, Object> documentoResponse(String tipoDocumentoCodigo) {
        return Map.of(
                "uuid", "doc-1",
                "pagoUuid", "pago-1",
                "cotizacionUuid", "cotizacion-1",
                "tipoDocumentoCodigo", tipoDocumentoCodigo,
                "folio", "12345"
        );
    }
}
