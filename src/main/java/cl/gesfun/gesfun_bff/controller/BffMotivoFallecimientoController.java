package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.MotivoFallecimiento;
import cl.gesfun.gesfun_bff.service.MotivoFallecimientoBffService;
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
@RequestMapping("/api/motivos-fallecimiento")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffMotivoFallecimientoController extends BffResponseSupport {

    private final MotivoFallecimientoBffService motivoFallecimientoBffService;

    public BffMotivoFallecimientoController(MotivoFallecimientoBffService motivoFallecimientoBffService) {
        this.motivoFallecimientoBffService = motivoFallecimientoBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) { return responder(motivoFallecimientoBffService.listar(jwt)); }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(motivoFallecimientoBffService.buscarPorUuid(uuid, jwt)); }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody MotivoFallecimiento motivoFallecimiento, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(motivoFallecimientoBffService.crear(motivoFallecimiento, jwt)); }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody MotivoFallecimiento motivoFallecimiento, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(motivoFallecimientoBffService.actualizar(uuid, motivoFallecimiento, jwt)); }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> eliminar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(motivoFallecimientoBffService.eliminar(uuid, jwt)); }
}
