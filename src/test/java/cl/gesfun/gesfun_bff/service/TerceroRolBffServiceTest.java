package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.Tercero;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TerceroRolBffServiceTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    private ObjectMapper objectMapper;
    private TerceroRolBffService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new TerceroRolBffService(proxyService, objectMapper);
    }

    @Test
    void listarFiltraClientesDesdeRespuestaDelBackend() {
        List<Map<String, Object>> terceros = List.of(
                Map.of("uuid", "1", "rol", "CLIENTE"),
                Map.of("uuid", "2", "rol", "PROVEEDOR"),
                Map.of("uuid", "3", "rol", "cliente")
        );
        when(proxyService.forwardToBackend("/api/terceros", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).body(terceros));

        ResponseEntity<Object> response = service.listar("clientes", jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isInstanceOf(ArrayNode.class);
        ArrayNode payload = (ArrayNode) response.getBody();
        assertThat(payload).hasSize(2);
        assertThat(payload.get(0).get("uuid").asText()).isEqualTo("1");
        assertThat(payload.get(1).get("uuid").asText()).isEqualTo("3");
    }

    @Test
    void listarDevuelveBodyOriginalCuandoBackendNoRetornaArray() {
        Map<String, Object> body = Map.of("message", "ok");
        when(proxyService.forwardToBackend("/api/terceros", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok(body));

        ResponseEntity<Object> response = service.listar("proveedores", jwt);

        assertThat(response.getBody()).isSameAs(body);
    }

    @Test
    void listarPorEmpresaReenviaQueryConRol() {
        when(proxyService.forwardToBackend("/api/terceros/empresa/empresa-1", "rol=EMPLEADO", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.listarPorEmpresa("empleados", "empresa-1", jwt);

        verify(proxyService).forwardToBackend("/api/terceros/empresa/empresa-1", "rol=EMPLEADO", HttpMethod.GET, null, jwt);
    }

    @Test
    void crearFuerzaRolSegunTipoTercero() throws Exception {
        Tercero tercero = terceroConRol("PROVEEDOR");
        when(proxyService.forwardToBackend(eq("/api/terceros"), eq(HttpMethod.POST), org.mockito.ArgumentMatchers.anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("ok"));

        service.crear("clientes", tercero, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/terceros"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode body = objectMapper.readTree(bodyCaptor.getValue());
        assertThat(body.get("rol").asText()).isEqualTo("CLIENTE");
        assertThat(body.get("rut").asInt()).isEqualTo(12345678);
    }

    @Test
    void actualizarFuerzaRolYUsaUuid() throws Exception {
        Tercero tercero = terceroConRol(null);
        when(proxyService.forwardToBackend(eq("/api/terceros/tercero-1"), eq(HttpMethod.PUT), org.mockito.ArgumentMatchers.anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("ok"));

        service.actualizar("proveedores", "tercero-1", tercero, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/terceros/tercero-1"), eq(HttpMethod.PUT), bodyCaptor.capture(), eq(jwt));
        assertThat(objectMapper.readTree(bodyCaptor.getValue()).get("rol").asText()).isEqualTo("PROVEEDOR");
    }

    @Test
    void crearConRequestNuloEnviaSoloRol() throws Exception {
        when(proxyService.forwardToBackend(eq("/api/terceros"), eq(HttpMethod.POST), org.mockito.ArgumentMatchers.anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("ok"));

        service.crear("empleados", null, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/terceros"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode body = objectMapper.readTree(bodyCaptor.getValue());
        assertThat(body.size()).isEqualTo(1);
        assertThat(body.get("rol").asText()).isEqualTo("EMPLEADO");
    }

    @Test
    void rechazaTipoTerceroNoSoportado() {
        assertThatThrownBy(() -> service.listar("socios", jwt))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo de tercero no soportado");
    }

    private Tercero terceroConRol(String rol) {
        return new Tercero(
                "N",
                rol,
                12345678,
                "9",
                "Nombre Completo",
                "Nombre",
                "Paterno",
                "Materno",
                null,
                null,
                null,
                "demo@gesfun.cl",
                "123",
                1,
                "empresa-1",
                "comuna-1"
        );
    }
}
