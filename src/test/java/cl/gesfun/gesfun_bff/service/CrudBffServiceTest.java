package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrudBffServiceTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    private ObjectMapper objectMapper;
    private TestCrudBffService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new TestCrudBffService(proxyService, objectMapper);
    }

    @Test
    void listarReenviaGetAlPathBase() {
        ResponseEntity<Object> expected = ResponseEntity.ok("ok");
        when(proxyService.forwardToBackend("/api/test", HttpMethod.GET, null, jwt)).thenReturn(expected);

        ResponseEntity<Object> response = service.listar(jwt);

        assertThat(response).isSameAs(expected);
        verify(proxyService).forwardToBackend("/api/test", HttpMethod.GET, null, jwt);
    }

    @Test
    void buscarPorUuidReenviaGetConUuid() {
        when(proxyService.forwardToBackend("/api/test/uuid-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.buscarPorUuid("uuid-1", jwt);

        verify(proxyService).forwardToBackend("/api/test/uuid-1", HttpMethod.GET, null, jwt);
    }

    @Test
    void crearSerializaBodyYReenviaPost() throws Exception {
        Map<String, Object> request = Map.of("nombre", "Demo", "activo", 1);
        when(proxyService.forwardToBackend(eq("/api/test"), eq(HttpMethod.POST), org.mockito.ArgumentMatchers.anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("created"));

        service.crear(request, jwt);

        org.mockito.ArgumentCaptor<String> bodyCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/test"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode body = objectMapper.readTree(bodyCaptor.getValue());
        assertThat(body.get("nombre").asText()).isEqualTo("Demo");
        assertThat(body.get("activo").asInt()).isEqualTo(1);
    }

    @Test
    void actualizarSerializaBodyYReenviaPutConUuid() throws Exception {
        Map<String, Object> request = Map.of("nombre", "Editado");
        when(proxyService.forwardToBackend(eq("/api/test/uuid-1"), eq(HttpMethod.PUT), org.mockito.ArgumentMatchers.anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("updated"));

        service.actualizar("uuid-1", request, jwt);

        org.mockito.ArgumentCaptor<String> bodyCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/test/uuid-1"), eq(HttpMethod.PUT), bodyCaptor.capture(), eq(jwt));
        assertThat(objectMapper.readTree(bodyCaptor.getValue()).get("nombre").asText()).isEqualTo("Editado");
    }

    @Test
    void eliminarReenviaDeleteConUuid() {
        when(proxyService.forwardToBackend("/api/test/uuid-1", HttpMethod.DELETE, null, jwt))
                .thenReturn(ResponseEntity.noContent().build());

        service.eliminar("uuid-1", jwt);

        verify(proxyService).forwardToBackend("/api/test/uuid-1", HttpMethod.DELETE, null, jwt);
    }

    @Test
    void desactivarReenviaPatchADesactivar() {
        when(proxyService.forwardToBackend("/api/test/uuid-1/desactivar", HttpMethod.PATCH, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.desactivar("uuid-1", jwt);

        verify(proxyService).forwardToBackend("/api/test/uuid-1/desactivar", HttpMethod.PATCH, null, jwt);
    }

    @Test
    void buscarPorRutaReenviaGetConRutaPersonalizada() {
        when(proxyService.forwardToBackend("/api/test/sucursal/sucursal-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.buscarPorRutaPublica("/sucursal/sucursal-1", jwt);

        verify(proxyService).forwardToBackend("/api/test/sucursal/sucursal-1", HttpMethod.GET, null, jwt);
    }

    private static final class TestCrudBffService extends CrudBffService {

        private TestCrudBffService(ProxyService proxyService, ObjectMapper objectMapper) {
            super("/api/test", proxyService, objectMapper);
        }

        private ResponseEntity<Object> buscarPorRutaPublica(String path, Jwt jwt) {
            return buscarPorRuta(path, jwt);
        }
    }
}
