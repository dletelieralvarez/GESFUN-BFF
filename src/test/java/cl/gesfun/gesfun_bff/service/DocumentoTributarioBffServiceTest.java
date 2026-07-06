package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.DocumentoTributarioEmitir;
import cl.gesfun.gesfun_bff.model.SalidaInventarioFacturacion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        when(jwt.getClaimAsString(anyString())).thenAnswer(invocation ->
                "usuarioUuid".equals(invocation.getArgument(0)) ? "usuario-1" : null
        );

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
        assertThat(salidaCaptor.getValue().fechaDocumento()).isEqualTo(LocalDate.parse("2026-07-02"));
        assertThat(salidaCaptor.getValue().numeroFactura()).isEqualTo("12345");
        assertThat(salidaCaptor.getValue().observacion()).isEqualTo("Salida generada por facturacion");
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
        when(jwt.getClaimAsString(anyString())).thenAnswer(invocation ->
                "usuarioUuid".equals(invocation.getArgument(0)) ? "usuario-1" : null
        );

        service.emitir(request, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/documentos-tributarios/emitir"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode body = new ObjectMapper().readTree(bodyCaptor.getValue());
        assertThat(body.get("tipoDocumentoCodigo").asText()).isEqualTo("FACTURA");
    }

    @Test
    void emitirResuelveUsuarioInternoPorEmailCuandoJwtNoTraeUsuarioUuid() throws Exception {
        DocumentoTributarioEmitir request = new DocumentoTributarioEmitir(
                "pago-1",
                "BOLETA",
                "Emision por pago de servicio funerario"
        );
        when(proxyService.forwardToBackend(eq("/api/documentos-tributarios/emitir"), eq(HttpMethod.POST), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok(documentoResponse("BOLETA")));
        when(jwt.getClaimAsString(anyString())).thenAnswer(invocation ->
                "preferred_username".equals(invocation.getArgument(0)) ? "admin@gesfun.cl" : null
        );
        when(proxyService.forwardToBackend("/api/usuarios", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok(Map.of(
                        "payload", java.util.List.of(
                                Map.of("uuid", "usuario-interno-1", "email", "admin@gesfun.cl", "activo", 1)
                        )
                )));

        service.emitir(request, jwt);

        ArgumentCaptor<SalidaInventarioFacturacion> salidaCaptor =
                ArgumentCaptor.forClass(SalidaInventarioFacturacion.class);
        verify(inventarioBffService).registrarSalidaPorFacturacion(salidaCaptor.capture(), eq(jwt));
        assertThat(salidaCaptor.getValue().usuarioUuid()).isEqualTo("usuario-interno-1");
    }

    @Test
    void emitirRechazaCuandoNoPuedeResolverUsuarioInterno() {
        DocumentoTributarioEmitir request = new DocumentoTributarioEmitir(
                "pago-1",
                "BOLETA",
                "Emision por pago de servicio funerario"
        );
        when(proxyService.forwardToBackend(eq("/api/documentos-tributarios/emitir"), eq(HttpMethod.POST), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok(documentoResponse("BOLETA")));

        assertThatThrownBy(() -> service.emitir(request, jwt))
                .hasMessageContaining("El JWT no incluye usuarioUuid ni email");
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
                "fechaDocumento", "2026-07-02",
                "folio", "12345"
        );
    }
}
