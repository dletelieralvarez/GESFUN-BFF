package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.DocumentoTributarioEmitir;
import cl.gesfun.gesfun_bff.model.DocumentoTributarioUpdate;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.DocumentoTributarioBffService;
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
class BffDocumentoTributarioControllerTest {

    @Mock
    private DocumentoTributarioBffService service;

    @Mock
    private Jwt jwt;

    private BffDocumentoTributarioController controller;

    @BeforeEach
    void setUp() {
        controller = new BffDocumentoTributarioController(service);
    }

    @Test
    void listarUsaServicio() {
        when(service.listar(jwt)).thenReturn(ResponseEntity.ok(List.of()));

        controller.listar(jwt);

        verify(service).listar(jwt);
    }

    @Test
    void emitirUsaServicio() throws Exception {
        DocumentoTributarioEmitir request = new DocumentoTributarioEmitir("pago-1", "BOLETA", null);
        when(service.emitir(request, jwt)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("folio", "1")));

        ResponseEntity<FrontendResponse<Object>> response = controller.emitir(request, jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service).emitir(request, jwt);
    }

    @Test
    void actualizarUsaUuidYBody() throws Exception {
        DocumentoTributarioUpdate request = new DocumentoTributarioUpdate("EMITIDO", "1", "track-1", null, null, null);
        when(service.actualizar("doc-1", request, jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "doc-1")));

        controller.actualizar("doc-1", request, jwt);

        verify(service).actualizar("doc-1", request, jwt);
    }

    @Test
    void buscarPorPagoUsaPagoUuid() {
        when(service.buscarPorPago("pago-1", jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "doc-1")));

        controller.buscarPorPago("pago-1", jwt);

        verify(service).buscarPorPago("pago-1", jwt);
    }

    @Test
    void listarPorCotizacionUsaCotizacionUuid() {
        when(service.listarPorCotizacion("cotizacion-1", jwt)).thenReturn(ResponseEntity.ok(List.of()));

        controller.listarPorCotizacion("cotizacion-1", jwt);

        verify(service).listarPorCotizacion("cotizacion-1", jwt);
    }

    @Test
    void reenviarUsaUuid() {
        when(service.reenviar("doc-1", jwt)).thenReturn(ResponseEntity.ok(Map.of("estado", "ENVIADO")));

        controller.reenviar("doc-1", jwt);

        verify(service).reenviar("doc-1", jwt);
    }

    @Test
    void anularUsaUuid() {
        when(service.anular("doc-1", jwt)).thenReturn(ResponseEntity.ok(Map.of("estado", "ANULADO")));

        controller.anular("doc-1", jwt);

        verify(service).anular("doc-1", jwt);
    }
}
