package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.TipoMovimiento;
import cl.gesfun.gesfun_bff.service.TipoMovimientoBffService;
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
@RequestMapping("/api/tipos-movimiento")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffTipoMovimientoController extends BffResponseSupport {

    private final TipoMovimientoBffService tipoMovimientoBffService;

    public BffTipoMovimientoController(TipoMovimientoBffService tipoMovimientoBffService) {
        this.tipoMovimientoBffService = tipoMovimientoBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) { return responder(tipoMovimientoBffService.listar(jwt)); }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(tipoMovimientoBffService.buscarPorUuid(uuid, jwt)); }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody TipoMovimiento tipoMovimiento, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(tipoMovimientoBffService.crear(tipoMovimiento, jwt)); }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody TipoMovimiento tipoMovimiento, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(tipoMovimientoBffService.actualizar(uuid, tipoMovimiento, jwt)); }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> eliminar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(tipoMovimientoBffService.eliminar(uuid, jwt)); }
}
