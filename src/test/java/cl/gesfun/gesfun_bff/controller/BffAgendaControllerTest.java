package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.Agenda;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.AgendaBffService;
import java.time.LocalDateTime;
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
class BffAgendaControllerTest {

    @Mock
    private AgendaBffService service;

    @Mock
    private Jwt jwt;

    private BffAgendaController controller;

    @BeforeEach
    void setUp() {
        controller = new BffAgendaController(service);
    }

    @Test
    void listarRespondePayloadDelService() {
        List<Map<String, Object>> payload = List.of(Map.of("uuid", "agenda-1"));
        when(service.listar(jwt)).thenReturn(ResponseEntity.ok(payload));

        ResponseEntity<FrontendResponse<Object>> response = controller.listar(jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getPayload()).isEqualTo(payload);
        verify(service).listar(jwt);
    }

    @Test
    void buscarPorUuidUsaUuid() {
        when(service.buscarPorUuid("agenda-1", jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "agenda-1")));

        controller.buscarPorUuid("agenda-1", jwt);

        verify(service).buscarPorUuid("agenda-1", jwt);
    }

    @Test
    void listarPorSucursalUsaSucursalUuid() {
        when(service.listarPorSucursal("sucursal-1", jwt)).thenReturn(ResponseEntity.ok(List.of()));

        controller.listarPorSucursal("sucursal-1", jwt);

        verify(service).listarPorSucursal("sucursal-1", jwt);
    }

    @Test
    void listarPorTipoRecursoUsaTipoRecursoUuid() {
        when(service.listarPorTipoRecurso("tipo-1", jwt)).thenReturn(ResponseEntity.ok(List.of()));

        controller.listarPorTipoRecurso("tipo-1", jwt);

        verify(service).listarPorTipoRecurso("tipo-1", jwt);
    }

    @Test
    void crearEnviaBodyAlService() throws Exception {
        Agenda agenda = agenda();
        when(service.crear(agenda, jwt)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uuid", "agenda-1")));

        ResponseEntity<FrontendResponse<Object>> response = controller.crear(agenda, jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service).crear(agenda, jwt);
    }

    @Test
    void actualizarUsaUuidYBody() throws Exception {
        Agenda agenda = agenda();
        when(service.actualizar("agenda-1", agenda, jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "agenda-1")));

        controller.actualizar("agenda-1", agenda, jwt);

        verify(service).actualizar("agenda-1", agenda, jwt);
    }

    @Test
    void eliminarUsaUuid() {
        when(service.eliminar("agenda-1", jwt)).thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<FrontendResponse<Object>> response = controller.eliminar("agenda-1", jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).eliminar("agenda-1", jwt);
    }

    private Agenda agenda() {
        return new Agenda(
                null,
                LocalDateTime.of(2026, 6, 25, 10, 0),
                LocalDateTime.of(2026, 6, 25, 12, 0),
                "OCUPADO",
                "Sala de velatorio para servicio",
                "tipo-1",
                null,
                "sucursal-1",
                null,
                "cotizacion-1",
                null
        );
    }
}
