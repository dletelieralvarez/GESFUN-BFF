package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.ServicioFunerario;
import cl.gesfun.gesfun_bff.model.ServicioFunerarioCreate;
import cl.gesfun.gesfun_bff.service.ServicioFunerarioBffService;
import java.math.BigDecimal;
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
class BffServicioFunerarioControllerTest {

    @Mock
    private ServicioFunerarioBffService service;

    @Mock
    private Jwt jwt;

    private BffServicioFunerarioController controller;

    @BeforeEach
    void setUp() {
        controller = new BffServicioFunerarioController(service);
    }

    @Test
    void listarRespondePayloadDelService() {
        List<Map<String, Object>> payload = List.of(Map.of("uuid", "servicio-1"));
        when(service.listar(jwt)).thenReturn(ResponseEntity.ok(payload));

        ResponseEntity<FrontendResponse<Object>> response = controller.listar(jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPayload()).isEqualTo(payload);
        verify(service).listar(jwt);
    }

    @Test
    void buscarPorUuidUsaUuid() {
        when(service.buscarPorUuid("servicio-1", jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "servicio-1")));

        controller.buscarPorUuid("servicio-1", jwt);

        verify(service).buscarPorUuid("servicio-1", jwt);
    }

    @Test
    void filtrosUsanParametrosEsperados() {
        when(service.listarPorSucursal("sucursal-1", jwt)).thenReturn(ResponseEntity.ok(List.of()));
        when(service.listarPorEstado("PROGRAMADO", jwt)).thenReturn(ResponseEntity.ok(List.of()));
        when(service.buscarPorCotizacion("cotizacion-1", jwt)).thenReturn(ResponseEntity.ok(Map.of()));
        when(service.listarPorCliente("cliente-1", jwt)).thenReturn(ResponseEntity.ok(List.of()));

        controller.listarPorSucursal("sucursal-1", jwt);
        controller.listarPorEstado("PROGRAMADO", jwt);
        controller.buscarPorCotizacion("cotizacion-1", jwt);
        controller.listarPorCliente("cliente-1", jwt);

        verify(service).listarPorSucursal("sucursal-1", jwt);
        verify(service).listarPorEstado("PROGRAMADO", jwt);
        verify(service).buscarPorCotizacion("cotizacion-1", jwt);
        verify(service).listarPorCliente("cliente-1", jwt);
    }

    @Test
    void crearEnviaBodyAlService() throws Exception {
        ServicioFunerarioCreate servicio = servicioCreate();
        when(service.crear(servicio, jwt)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of("uuid", "servicio-1")));

        ResponseEntity<FrontendResponse<Object>> response = controller.crear(servicio, jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service).crear(servicio, jwt);
    }

    @Test
    void actualizarUsaUuidYBody() throws Exception {
        ServicioFunerario servicio = servicio();
        when(service.actualizar("servicio-1", servicio, jwt)).thenReturn(ResponseEntity.ok(Map.of("uuid", "servicio-1")));

        controller.actualizar("servicio-1", servicio, jwt);

        verify(service).actualizar("servicio-1", servicio, jwt);
    }

    @Test
    void desactivarUsaUuid() {
        when(service.desactivar("servicio-1", jwt)).thenReturn(ResponseEntity.ok(Map.of("estado", "ANULADO")));

        controller.desactivar("servicio-1", jwt);

        verify(service).desactivar("servicio-1", jwt);
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

    private ServicioFunerarioCreate servicioCreate() {
        return new ServicioFunerarioCreate(
                "ES-2026-0001",
                "PENDIENTE",
                "cotizacion-1",
                null,
                LocalDateTime.of(2026, 6, 30, 0, 0),
                null,
                null,
                null,
                "Cementerio General",
                "Servicio creado desde cotizacion"
        );
    }
}
