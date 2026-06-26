package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.Agenda;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaBffServiceTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    private AgendaBffService service;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        service = new AgendaBffService(proxyService, objectMapper);
    }

    @Test
    void listarReenviaAAgendas() {
        when(proxyService.forwardToBackend("/api/agendas", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.listar(jwt);

        verify(proxyService).forwardToBackend("/api/agendas", HttpMethod.GET, null, jwt);
    }

    @Test
    void listarPorSucursalReenviaRutaEsperada() {
        when(proxyService.forwardToBackend("/api/agendas/sucursal/sucursal-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.listarPorSucursal("sucursal-1", jwt);

        verify(proxyService).forwardToBackend("/api/agendas/sucursal/sucursal-1", HttpMethod.GET, null, jwt);
    }

    @Test
    void listarPorTipoRecursoReenviaRutaEsperada() {
        when(proxyService.forwardToBackend("/api/agendas/tipo-recurso/tipo-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.listarPorTipoRecurso("tipo-1", jwt);

        verify(proxyService).forwardToBackend("/api/agendas/tipo-recurso/tipo-1", HttpMethod.GET, null, jwt);
    }

    @Test
    void crearSerializaFechasIsoYReenviaBody() throws Exception {
        Agenda agenda = new Agenda(
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
        when(proxyService.forwardToBackend(eq("/api/agendas"), eq(HttpMethod.POST), org.mockito.ArgumentMatchers.anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("ok"));

        service.crear(agenda, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/agendas"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode body = new ObjectMapper().readTree(bodyCaptor.getValue());
        assertThat(body.get("fechaHoraInicio").asText()).isEqualTo("2026-06-25T10:00:00");
        assertThat(body.get("fechaHoraFin").asText()).isEqualTo("2026-06-25T12:00:00");
        assertThat(body.get("cotizacionUuid").asText()).isEqualTo("cotizacion-1");
    }

    @Test
    void actualizarUsaPutConUuid() throws Exception {
        Agenda agenda = new Agenda(null, null, null, "DISPONIBLE", null, null, null, null, null, null, null);
        when(proxyService.forwardToBackend(eq("/api/agendas/agenda-1"), eq(HttpMethod.PUT), org.mockito.ArgumentMatchers.anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("ok"));

        service.actualizar("agenda-1", agenda, jwt);

        verify(proxyService).forwardToBackend(eq("/api/agendas/agenda-1"), eq(HttpMethod.PUT), org.mockito.ArgumentMatchers.anyString(), eq(jwt));
    }

    @Test
    void eliminarUsaDeleteConUuid() {
        when(proxyService.forwardToBackend("/api/agendas/agenda-1", HttpMethod.DELETE, null, jwt))
                .thenReturn(ResponseEntity.noContent().build());

        service.eliminar("agenda-1", jwt);

        verify(proxyService).forwardToBackend("/api/agendas/agenda-1", HttpMethod.DELETE, null, jwt);
    }
}
