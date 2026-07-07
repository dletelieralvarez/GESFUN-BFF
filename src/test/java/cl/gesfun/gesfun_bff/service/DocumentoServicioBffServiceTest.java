package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentoServicioBffServiceTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    private DocumentoServicioBffService service;

    @BeforeEach
    void setUp() {
        service = new DocumentoServicioBffService(proxyService, new ObjectMapper());
    }

    @Test
    void listarPorCotizacionReenviaRutaEsperada() {
        when(proxyService.forwardToBackend(
                "/api/documentos-servicio/cotizacion/cotizacion-1",
                HttpMethod.GET,
                null,
                jwt
        )).thenReturn(ResponseEntity.ok("ok"));

        service.listarPorCotizacion("cotizacion-1", jwt);

        verify(proxyService).forwardToBackend(
                "/api/documentos-servicio/cotizacion/cotizacion-1",
                HttpMethod.GET,
                null,
                jwt
        );
    }

    @Test
    void crearYActualizarReenvianPayloadJson() throws Exception {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("cotizacionUuid", "cotizacion-1");
        request.put("usuarioUuid", "usuario-1");
        request.put("tipoDocumentoUuid", "tipo-documento-1");
        request.put("estadoDocumento", "PENDIENTE");
        String expectedJson = "{\"cotizacionUuid\":\"cotizacion-1\",\"usuarioUuid\":\"usuario-1\",\"tipoDocumentoUuid\":\"tipo-documento-1\",\"estadoDocumento\":\"PENDIENTE\"}";
        when(proxyService.forwardToBackend(
                "/api/documentos-servicio",
                HttpMethod.POST,
                expectedJson,
                jwt
        )).thenReturn(ResponseEntity.ok("created"));
        when(proxyService.forwardToBackend(
                "/api/documentos-servicio/doc-1",
                HttpMethod.PUT,
                expectedJson,
                jwt
        )).thenReturn(ResponseEntity.ok("updated"));

        service.crear(request, jwt);
        service.actualizar("doc-1", request, jwt);

        verify(proxyService).forwardToBackend("/api/documentos-servicio", HttpMethod.POST, expectedJson, jwt);
        verify(proxyService).forwardToBackend("/api/documentos-servicio/doc-1", HttpMethod.PUT, expectedJson, jwt);
    }
}
