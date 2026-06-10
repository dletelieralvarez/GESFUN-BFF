package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.Sucursal;
import cl.gesfun.gesfun_bff.service.SucursalBffService;
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
@RequestMapping("/api/sucursales")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffSucursalController extends BffResponseSupport {

    private final SucursalBffService sucursalBffService;

    public BffSucursalController(SucursalBffService sucursalBffService) {
        this.sucursalBffService = sucursalBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) { return responder(sucursalBffService.listar(jwt)); }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(sucursalBffService.buscarPorUuid(uuid, jwt)); }

    @GetMapping("/empresa/{empresaUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorEmpresa(@PathVariable String empresaUuid, @AuthenticationPrincipal Jwt jwt) { return responder(sucursalBffService.listarPorEmpresa(empresaUuid, jwt)); }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody Sucursal sucursal, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(sucursalBffService.crear(sucursal, jwt)); }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody Sucursal sucursal, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(sucursalBffService.actualizar(uuid, sucursal, jwt)); }

    @PatchMapping("/{uuid}/desactivar")
    public ResponseEntity<FrontendResponse<Object>> desactivar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(sucursalBffService.desactivar(uuid, jwt)); }
}
