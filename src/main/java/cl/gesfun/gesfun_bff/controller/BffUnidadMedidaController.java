package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.UnidadMedida;
import cl.gesfun.gesfun_bff.service.UnidadMedidaBffService;
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
@RequestMapping("/api/unidades-medida")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffUnidadMedidaController extends BffResponseSupport {

    private final UnidadMedidaBffService unidadMedidaBffService;

    public BffUnidadMedidaController(UnidadMedidaBffService unidadMedidaBffService) {
        this.unidadMedidaBffService = unidadMedidaBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) { return responder(unidadMedidaBffService.listar(jwt)); }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(unidadMedidaBffService.buscarPorUuid(uuid, jwt)); }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody UnidadMedida unidadMedida, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(unidadMedidaBffService.crear(unidadMedida, jwt)); }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody UnidadMedida unidadMedida, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(unidadMedidaBffService.actualizar(uuid, unidadMedida, jwt)); }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> eliminar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(unidadMedidaBffService.eliminar(uuid, jwt)); }
}
