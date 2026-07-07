package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.DocumentoServicioBffService;
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
class BffDocumentoServicioControllerTest {

    @Mock
    private DocumentoServicioBffService service;

    @Mock
    private Jwt jwt;

    private BffDocumentoServicioController controller;

    @BeforeEach
    void setUp() {
        controller = new BffDocumentoServicioController(service);
    }

    @Test
    void consultasUsanRutasEsperadas() {
        when(service.listar(jwt)).thenReturn(ResponseEntity.ok(List.of()));
        when(service.listarPorCotizacion("cotizacion-1", jwt)).thenReturn(ResponseEntity.ok(List.of()));
        when(service.buscarPorUuid("doc-1", jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "doc-1")));

        assertPayload(controller.listar(jwt), List.of());
        assertPayload(controller.listarPorCotizacion("cotizacion-1", jwt), List.of());
        assertPayload(controller.buscarPorUuid("doc-1", jwt), Map.of("uuid", "doc-1"));

        verify(service).listar(jwt);
        verify(service).listarPorCotizacion("cotizacion-1", jwt);
        verify(service).buscarPorUuid("doc-1", jwt);
    }

    @Test
    void comandosUsanPayloadYUuid() throws Exception {
        Map<String, Object> request = Map.of("estadoDocumento", "REALIZADO");
        when(service.crear(request, jwt)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uuid", "doc-1")));
        when(service.actualizar("doc-1", request, jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "doc-1")));
        when(service.eliminar("doc-1", jwt)).thenReturn(ResponseEntity.noContent().build());

        assertThat(controller.crear(request, jwt).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertPayload(controller.actualizar("doc-1", request, jwt), Map.of("uuid", "doc-1"));
        assertThat(controller.eliminar("doc-1", jwt).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(service).crear(request, jwt);
        verify(service).actualizar("doc-1", request, jwt);
        verify(service).eliminar("doc-1", jwt);
    }

    private void assertPayload(ResponseEntity<FrontendResponse<Object>> response, Object expectedPayload) {
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPayload()).isEqualTo(expectedPayload);
    }
}
