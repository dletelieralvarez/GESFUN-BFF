package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.ServicioFunerario;
import cl.gesfun.gesfun_bff.model.ServicioFunerarioCreate;
import cl.gesfun.gesfun_bff.service.ServicioFunerarioBffService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/servicios")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffServicioFunerarioController extends BffResponseSupport {

    private final ServicioFunerarioBffService servicioFunerarioBffService;

    public BffServicioFunerarioController(ServicioFunerarioBffService servicioFunerarioBffService) {
        this.servicioFunerarioBffService = servicioFunerarioBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) {
        return responder(servicioFunerarioBffService.listar(jwt));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(servicioFunerarioBffService.buscarPorUuid(uuid, jwt));
    }

    @GetMapping("/sucursal/{sucursalUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorSucursal(
            @PathVariable String sucursalUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(servicioFunerarioBffService.listarPorSucursal(sucursalUuid, jwt));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<FrontendResponse<Object>> listarPorEstado(
            @PathVariable String estado,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(servicioFunerarioBffService.listarPorEstado(estado, jwt));
    }

    @GetMapping("/cotizacion/{cotizacionUuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorCotizacion(
            @PathVariable String cotizacionUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(servicioFunerarioBffService.buscarPorCotizacion(cotizacionUuid, jwt));
    }

    @GetMapping("/cliente/{terceroUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorCliente(
            @PathVariable String terceroUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(servicioFunerarioBffService.listarPorCliente(terceroUuid, jwt));
    }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(
            @Valid @RequestBody ServicioFunerarioCreate servicio,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(servicioFunerarioBffService.crear(servicio, jwt));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(
            @PathVariable String uuid,
            @Valid @RequestBody ServicioFunerario servicio,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(servicioFunerarioBffService.actualizar(uuid, servicio, jwt));
    }

    @PatchMapping("/{uuid}/desactivar")
    public ResponseEntity<FrontendResponse<Object>> desactivar(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(servicioFunerarioBffService.desactivar(uuid, jwt));
    }
}
