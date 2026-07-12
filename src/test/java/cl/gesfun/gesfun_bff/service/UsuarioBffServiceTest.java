package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.Usuario;
import com.fasterxml.jackson.databind.JsonNode;
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
class UsuarioBffServiceTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    private ObjectMapper objectMapper;
    private UsuarioBffService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        service = new UsuarioBffService(proxyService, objectMapper);
    }

    @Test
    void listarBuscarYEliminarReenvianRutasEsperadas() {
        when(proxyService.forwardToBackend("/api/usuarios", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("listar"));
        when(proxyService.forwardToBackend("/api/usuarios/7", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("buscar"));
        when(proxyService.forwardToBackend("/api/usuarios/7", HttpMethod.DELETE, null, jwt))
                .thenReturn(ResponseEntity.noContent().build());

        service.listar(jwt);
        service.buscarPorId(7, jwt);
        service.eliminar(7, jwt);

        verify(proxyService).forwardToBackend("/api/usuarios", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/usuarios/7", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/usuarios/7", HttpMethod.DELETE, null, jwt);
    }

    @Test
    void crearYActualizarSerializanUsuario() throws Exception {
        Usuario usuario = new Usuario(
                "admin@gesfun.cl",
                "secret",
                "Admin",
                "Perez",
                "Soto",
                1,
                "USER",
                "INTERNO"
        );
        when(proxyService.forwardToBackend(eq("/api/usuarios"), eq(HttpMethod.POST), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("crear"));
        when(proxyService.forwardToBackend(eq("/api/usuarios/7"), eq(HttpMethod.PUT), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("actualizar"));

        service.crear(usuario, jwt);
        service.actualizar(7, usuario, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/usuarios"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode crearBody = objectMapper.readTree(bodyCaptor.getValue());
        assertThat(crearBody.path("email").asText()).isEqualTo("admin@gesfun.cl");
        assertThat(crearBody.path("activo").asInt()).isEqualTo(1);
        assertThat(crearBody.path("roles").asText()).isEqualTo("USER");

        verify(proxyService).forwardToBackend(eq("/api/usuarios/7"), eq(HttpMethod.PUT), bodyCaptor.capture(), eq(jwt));
        JsonNode actualizarBody = objectMapper.readTree(bodyCaptor.getValue());
        assertThat(actualizarBody.path("nombre").asText()).isEqualTo("Admin");
        assertThat(actualizarBody.path("tipoUsuario").asText()).isEqualTo("INTERNO");
    }
}
