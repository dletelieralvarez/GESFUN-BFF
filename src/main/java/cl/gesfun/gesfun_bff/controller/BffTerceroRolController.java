package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.Tercero;
import cl.gesfun.gesfun_bff.service.TerceroRolBffService;
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
@RequestMapping("/api")
@PreAuthorize("hasAuthority('SCOPE_access_as_user') or hasAuthority('SCOPE_https://duocactividadazure.onmicrosoft.com/daead1c3-a4cc-4647-9423-e1fc626d8003/access_as_user')")
public class BffTerceroRolController {

    private final TerceroRolBffService terceroRolBffService;

    public BffTerceroRolController(TerceroRolBffService terceroRolBffService) {
        this.terceroRolBffService = terceroRolBffService;
    }

    @GetMapping("/{tipoTercero:clientes|proveedores|empleados}")
    public ResponseEntity<FrontendResponse<Object>> listar(
            @PathVariable String tipoTercero,
            @AuthenticationPrincipal Jwt jwt) {

        return responder(terceroRolBffService.listar(tipoTercero, jwt));
    }

    @GetMapping("/terceros")
    public ResponseEntity<FrontendResponse<Object>> listarTerceros(
            @AuthenticationPrincipal Jwt jwt) {

        return responder(terceroRolBffService.listarTodos(jwt));
    }

    @GetMapping("/{tipoTercero:clientes|proveedores|empleados}/empresa/{empresaUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorEmpresa(
            @PathVariable String tipoTercero,
            @PathVariable String empresaUuid,
            @AuthenticationPrincipal Jwt jwt) {

        return responder(terceroRolBffService.listarPorEmpresa(tipoTercero, empresaUuid, jwt));
    }

    @GetMapping("/{tipoTercero:clientes|proveedores|empleados}/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(
            @PathVariable String tipoTercero,
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt) {

        return responder(terceroRolBffService.buscarPorUuid(uuid, jwt));
    }

    @PostMapping("/{tipoTercero:clientes|proveedores|empleados}")
    public ResponseEntity<FrontendResponse<Object>> crear(
            @PathVariable String tipoTercero,
            @Valid @RequestBody Tercero tercero,
            @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException {

        return responder(terceroRolBffService.crear(tipoTercero, tercero, jwt));
    }

    @PutMapping("/{tipoTercero:clientes|proveedores|empleados}/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(
            @PathVariable String tipoTercero,
            @PathVariable String uuid,
            @Valid @RequestBody Tercero tercero,
            @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException {

        return responder(terceroRolBffService.actualizar(tipoTercero, uuid, tercero, jwt));
    }

    @PatchMapping("/{tipoTercero:clientes|proveedores|empleados}/{uuid}/desactivar")
    public ResponseEntity<FrontendResponse<Object>> desactivar(
            @PathVariable String tipoTercero,
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt) {

        return responder(terceroRolBffService.desactivar(uuid, jwt));
    }

    private ResponseEntity<FrontendResponse<Object>> responder(ResponseEntity<Object> backendResponse) {
        return ResponseEntity.status(backendResponse.getStatusCode())
                .body(FrontendResponse.success(backendResponse.getBody()));
    }
}
