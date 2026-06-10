package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FormaPago;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.FormaPagoBffService;
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
@RequestMapping("/api/formas-pago")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffFormaPagoController extends BffResponseSupport {

    private final FormaPagoBffService formaPagoBffService;

    public BffFormaPagoController(FormaPagoBffService formaPagoBffService) {
        this.formaPagoBffService = formaPagoBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) { return responder(formaPagoBffService.listar(jwt)); }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(formaPagoBffService.buscarPorUuid(uuid, jwt)); }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody FormaPago formaPago, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(formaPagoBffService.crear(formaPago, jwt)); }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody FormaPago formaPago, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(formaPagoBffService.actualizar(uuid, formaPago, jwt)); }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> eliminar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(formaPagoBffService.eliminar(uuid, jwt)); }
}
