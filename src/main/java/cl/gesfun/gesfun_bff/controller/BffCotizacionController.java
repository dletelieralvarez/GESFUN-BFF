package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.Cotizacion;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.CotizacionBffService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cotizaciones")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffCotizacionController extends BffResponseSupport {

    private final CotizacionBffService cotizacionBffService;

    public BffCotizacionController(CotizacionBffService cotizacionBffService) {
        this.cotizacionBffService = cotizacionBffService;
    }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(
            @Valid @RequestBody Cotizacion cotizacion,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(cotizacionBffService.crear(cotizacion, jwt));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(cotizacionBffService.buscarPorUuid(uuid, jwt));
    }

    @GetMapping("/sucursal/{sucursalUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorSucursal(
            @PathVariable String sucursalUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(cotizacionBffService.listarPorSucursal(sucursalUuid, jwt));
    }
}
