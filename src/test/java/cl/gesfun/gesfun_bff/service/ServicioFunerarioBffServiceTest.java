package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.ServicioFunerario;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicioFunerarioBffServiceTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    private ServicioFunerarioBffService service;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        service = new ServicioFunerarioBffService(proxyService, objectMapper);
    }

    @Test
    void listarReenviaRutaBase() {
        when(proxyService.forwardToBackend("/api/servicios", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.listar(jwt);

        verify(proxyService).forwardToBackend("/api/servicios", HttpMethod.GET, null, jwt);
    }

    @Test
    void filtrosReenvianRutasEsperadas() {
        when(proxyService.forwardToBackend("/api/servicios/sucursal/sucursal-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("sucursal"));
        when(proxyService.forwardToBackend("/api/servicios/estado/PROGRAMADO", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("estado"));
        when(proxyService.forwardToBackend("/api/servicios/cotizacion/cotizacion-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("cotizacion"));
        when(proxyService.forwardToBackend("/api/servicios/cliente/cliente-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("cliente"));

        service.listarPorSucursal("sucursal-1", jwt);
        service.listarPorEstado("PROGRAMADO", jwt);
        service.buscarPorCotizacion("cotizacion-1", jwt);
        service.listarPorCliente("cliente-1", jwt);

        verify(proxyService).forwardToBackend("/api/servicios/sucursal/sucursal-1", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/servicios/estado/PROGRAMADO", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/servicios/cotizacion/cotizacion-1", HttpMethod.GET, null, jwt);
        verify(proxyService).forwardToBackend("/api/servicios/cliente/cliente-1", HttpMethod.GET, null, jwt);
    }

    @Test
    void crearSerializaFechasIsoYMontos() throws Exception {
        ServicioFunerario servicio = servicio();
        when(proxyService.forwardToBackend(eq("/api/servicios"), eq(HttpMethod.POST), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("ok"));

        service.crear(servicio, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/servicios"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode body = new ObjectMapper().readTree(bodyCaptor.getValue());
        assertThat(body.get("folio").asText()).isEqualTo("ES-2026-0001");
        assertThat(body.get("estado").asText()).isEqualTo("PROGRAMADO");
        assertThat(body.get("fechaIngreso").asText()).isEqualTo("2026-06-30T10:00:00");
        assertThat(body.get("fechaCeremonia").asText()).isEqualTo("2026-07-01T10:00:00");
        assertThat(body.get("montoTotal").decimalValue()).isEqualByComparingTo("2050000");
        assertThat(body.get("montoPagado").decimalValue()).isEqualByComparingTo("500000");
    }

    @Test
    void actualizarUsaPutConUuid() throws Exception {
        ServicioFunerario servicio = servicio();
        when(proxyService.forwardToBackend(eq("/api/servicios/servicio-1"), eq(HttpMethod.PUT), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("ok"));

        service.actualizar("servicio-1", servicio, jwt);

        verify(proxyService).forwardToBackend(eq("/api/servicios/servicio-1"), eq(HttpMethod.PUT), anyString(), eq(jwt));
    }

    @Test
    void desactivarUsaPatchConUuid() {
        when(proxyService.forwardToBackend("/api/servicios/servicio-1/desactivar", HttpMethod.PATCH, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.desactivar("servicio-1", jwt);

        verify(proxyService).forwardToBackend("/api/servicios/servicio-1/desactivar", HttpMethod.PATCH, null, jwt);
    }

    private ServicioFunerario servicio() {
        return new ServicioFunerario(
                "ES-2026-0001",
                "Nombre Apellido",
                "12345678-9",
                "PROGRAMADO",
                LocalDateTime.of(2026, 6, 30, 10, 0),
                LocalDateTime.of(2026, 6, 30, 18, 0),
                LocalDateTime.of(2026, 7, 1, 10, 0),
                LocalDateTime.of(2026, 7, 1, 12, 0),
                "Cementerio General",
                BigDecimal.valueOf(2050000),
                BigDecimal.valueOf(500000),
                "Servicio contratado por la familia",
                "cotizacion-1",
                "cliente-1",
                "plan-1",
                "sucursal-1",
                "agenda-1",
                "motivo-1",
                "usuario-1"
        );
    }
}
