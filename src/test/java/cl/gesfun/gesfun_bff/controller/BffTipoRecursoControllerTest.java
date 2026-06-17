package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.TipoRecurso;
import cl.gesfun.gesfun_bff.service.TipoRecursoBffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BffTipoRecursoControllerTest {

    @Mock
    private TipoRecursoBffService service;

    @Mock
    private Jwt jwt;

    private BffTipoRecursoController controller;

    @BeforeEach
    void setUp() {
        controller = new BffTipoRecursoController(service);
    }

    @Test
    void listarRespondePayloadDelService() {
        List<Map<String, Object>> payload = List.of(Map.of("codigo", "CAPILLA"));
        when(service.listar(jwt)).thenReturn(ResponseEntity.ok(payload));

        ResponseEntity<FrontendResponse<Object>> response = controller.listar(jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getPayload()).isEqualTo(payload);
        verify(service).listar(jwt);
    }

    @Test
    void listarPorSucursalUsaSucursalUuid() {
        when(service.listarPorSucursal("sucursal-1", jwt)).thenReturn(ResponseEntity.ok(List.of()));

        controller.listarPorSucursal("sucursal-1", jwt);

        verify(service).listarPorSucursal("sucursal-1", jwt);
    }

    @Test
    void buscarPorUuidUsaUuid() {
        when(service.buscarPorUuid("tipo-1", jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "tipo-1")));

        controller.buscarPorUuid("tipo-1", jwt);

        verify(service).buscarPorUuid("tipo-1", jwt);
    }

    @Test
    void crearEnviaBodyAlService() throws Exception {
        TipoRecurso tipoRecurso = new TipoRecurso("CAPILLA", "Capilla", 1, "sucursal-1");
        when(service.crear(tipoRecurso, jwt)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uuid", "tipo-1")));

        ResponseEntity<FrontendResponse<Object>> response = controller.crear(tipoRecurso, jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service).crear(tipoRecurso, jwt);
    }

    @Test
    void actualizarUsaUuidYBody() throws Exception {
        TipoRecurso tipoRecurso = new TipoRecurso("CAPILLA", "Capilla editada", 1, "sucursal-1");
        when(service.actualizar("tipo-1", tipoRecurso, jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "tipo-1")));

        controller.actualizar("tipo-1", tipoRecurso, jwt);

        verify(service).actualizar("tipo-1", tipoRecurso, jwt);
    }

    @Test
    void eliminarUsaUuid() {
        when(service.eliminar("tipo-1", jwt)).thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<FrontendResponse<Object>> response = controller.eliminar("tipo-1", jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).eliminar("tipo-1", jwt);
    }
}
