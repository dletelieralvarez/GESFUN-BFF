package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.Tercero;
import cl.gesfun.gesfun_bff.service.TerceroRolBffService;
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
class BffTerceroRolControllerTest {

    @Mock
    private TerceroRolBffService service;

    @Mock
    private Jwt jwt;

    private BffTerceroRolController controller;

    @BeforeEach
    void setUp() {
        controller = new BffTerceroRolController(service);
    }

    @Test
    void listarUsaTipoTerceroYEnvuelveRespuesta() {
        List<Map<String, Object>> payload = List.of(Map.of("rol", "CLIENTE"));
        when(service.listar("clientes", jwt)).thenReturn(ResponseEntity.ok(payload));

        var response = controller.listar("clientes", jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPayload()).isEqualTo(payload);
        verify(service).listar("clientes", jwt);
    }

    @Test
    void listarPorEmpresaUsaTipoYEmpresa() {
        when(service.listarPorEmpresa("proveedores", "empresa-1", jwt)).thenReturn(ResponseEntity.ok(List.of()));

        controller.listarPorEmpresa("proveedores", "empresa-1", jwt);

        verify(service).listarPorEmpresa("proveedores", "empresa-1", jwt);
    }

    @Test
    void crearUsaTipoYBody() throws Exception {
        Tercero tercero = tercero();
        when(service.crear("empleados", tercero, jwt)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(Map.of()));

        var response = controller.crear("empleados", tercero, jwt);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(service).crear("empleados", tercero, jwt);
    }

    @Test
    void actualizarUsaTipoUuidYBody() throws Exception {
        Tercero tercero = tercero();
        when(service.actualizar("clientes", "tercero-1", tercero, jwt)).thenReturn(ResponseEntity.ok(Map.of()));

        controller.actualizar("clientes", "tercero-1", tercero, jwt);

        verify(service).actualizar("clientes", "tercero-1", tercero, jwt);
    }

    @Test
    void desactivarUsaUuid() {
        when(service.desactivar("tercero-1", jwt)).thenReturn(ResponseEntity.ok(Map.of()));

        controller.desactivar("clientes", "tercero-1", jwt);

        verify(service).desactivar("tercero-1", jwt);
    }

    private Tercero tercero() {
        return new Tercero("N", null, 12345678, "9", "Demo", "Demo", "Uno", "Dos",
                null, null, null, "demo@gesfun.cl", "123", 1, "empresa-1", "comuna-1");
    }
}
