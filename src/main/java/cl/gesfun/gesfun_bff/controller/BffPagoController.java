package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.PagoCreate;
import cl.gesfun.gesfun_bff.model.PagoUpdate;
import cl.gesfun.gesfun_bff.service.PagoBffService;
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
@RequestMapping("/api/pagos")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffPagoController extends BffResponseSupport {

    private final PagoBffService pagoBffService;

    public BffPagoController(PagoBffService pagoBffService) {
        this.pagoBffService = pagoBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) {
        return responder(pagoBffService.listar(jwt));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(pagoBffService.buscarPorUuid(uuid, jwt));
    }

    @GetMapping("/cotizacion/{cotizacionUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorCotizacion(
            @PathVariable String cotizacionUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(pagoBffService.listarPorCotizacion(cotizacionUuid, jwt));
    }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(
            @Valid @RequestBody PagoCreate pago,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(pagoBffService.crear(pago, jwt));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(
            @PathVariable String uuid,
            @Valid @RequestBody PagoUpdate pago,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(pagoBffService.actualizar(uuid, pago, jwt));
    }

    @PatchMapping("/{uuid}/anular")
    public ResponseEntity<FrontendResponse<Object>> anular(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(pagoBffService.anular(uuid, jwt));
    }
}
