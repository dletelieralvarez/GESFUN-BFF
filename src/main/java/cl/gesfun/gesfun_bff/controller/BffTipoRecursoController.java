package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.TipoRecurso;
import cl.gesfun.gesfun_bff.service.TipoRecursoBffService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tipos-recurso")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffTipoRecursoController extends BffResponseSupport {

    private final TipoRecursoBffService tipoRecursoBffService;

    public BffTipoRecursoController(TipoRecursoBffService tipoRecursoBffService) {
        this.tipoRecursoBffService = tipoRecursoBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) { return responder(tipoRecursoBffService.listar(jwt)); }

    @GetMapping("/sucursal/{sucursalUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorSucursal(@PathVariable String sucursalUuid, @AuthenticationPrincipal Jwt jwt) { return responder(tipoRecursoBffService.listarPorSucursal(sucursalUuid, jwt)); }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(tipoRecursoBffService.buscarPorUuid(uuid, jwt)); }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody TipoRecurso tipoRecurso, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(tipoRecursoBffService.crear(tipoRecurso, jwt)); }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody TipoRecurso tipoRecurso, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(tipoRecursoBffService.actualizar(uuid, tipoRecurso, jwt)); }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> eliminar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(tipoRecursoBffService.eliminar(uuid, jwt)); }
}
