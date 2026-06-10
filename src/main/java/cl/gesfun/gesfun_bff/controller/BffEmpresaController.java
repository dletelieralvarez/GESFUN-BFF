package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.Empresa;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.EmpresaBffService;
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
@RequestMapping("/api/empresas")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffEmpresaController extends BffResponseSupport {

    private final EmpresaBffService empresaBffService;

    public BffEmpresaController(EmpresaBffService empresaBffService) {
        this.empresaBffService = empresaBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) {
        return responder(empresaBffService.listar(jwt));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) {
        return responder(empresaBffService.buscarPorUuid(uuid, jwt));
    }

    @GetMapping("/usuario/{usuarioUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorUsuario(@PathVariable String usuarioUuid, @AuthenticationPrincipal Jwt jwt) {
        return responder(empresaBffService.listarPorUsuario(usuarioUuid, jwt));
    }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody Empresa empresa, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException {
        return responder(empresaBffService.crear(empresa, jwt));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody Empresa empresa, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException {
        return responder(empresaBffService.actualizar(uuid, empresa, jwt));
    }

    @PatchMapping("/{uuid}/desactivar")
    public ResponseEntity<FrontendResponse<Object>> desactivar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) {
        return responder(empresaBffService.desactivar(uuid, jwt));
    }
}
