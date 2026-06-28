package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.PagoCreate;
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
class PagoBffServiceTest {

    @Mock
    private ProxyService proxyService;

    @Mock
    private Jwt jwt;

    private PagoBffService service;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        service = new PagoBffService(proxyService, objectMapper);
    }

    @Test
    void listarPorCotizacionReenviaRutaEsperada() {
        when(proxyService.forwardToBackend("/api/pagos/cotizacion/cotizacion-1", HttpMethod.GET, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.listarPorCotizacion("cotizacion-1", jwt);

        verify(proxyService).forwardToBackend("/api/pagos/cotizacion/cotizacion-1", HttpMethod.GET, null, jwt);
    }

    @Test
    void crearSerializaPagoConFechaIso() throws Exception {
        PagoCreate pago = new PagoCreate(
                "cotizacion-1",
                "forma-pago-1",
                BigDecimal.valueOf(150000),
                LocalDateTime.of(2026, 6, 27, 10, 30),
                "Abono inicial"
        );
        when(proxyService.forwardToBackend(eq("/api/pagos"), eq(HttpMethod.POST), anyString(), eq(jwt)))
                .thenReturn(ResponseEntity.ok("ok"));

        service.crear(pago, jwt);

        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(proxyService).forwardToBackend(eq("/api/pagos"), eq(HttpMethod.POST), bodyCaptor.capture(), eq(jwt));
        JsonNode body = new ObjectMapper().readTree(bodyCaptor.getValue());
        assertThat(body.get("cotizacionUuid").asText()).isEqualTo("cotizacion-1");
        assertThat(body.get("formaPagoUuid").asText()).isEqualTo("forma-pago-1");
        assertThat(body.get("monto").decimalValue()).isEqualByComparingTo("150000");
        assertThat(body.get("fechaPago").asText()).isEqualTo("2026-06-27T10:30:00");
    }

    @Test
    void anularUsaPatchConUuid() {
        when(proxyService.forwardToBackend("/api/pagos/pago-1/anular", HttpMethod.PATCH, null, jwt))
                .thenReturn(ResponseEntity.ok("ok"));

        service.anular("pago-1", jwt);

        verify(proxyService).forwardToBackend("/api/pagos/pago-1/anular", HttpMethod.PATCH, null, jwt);
    }
}
