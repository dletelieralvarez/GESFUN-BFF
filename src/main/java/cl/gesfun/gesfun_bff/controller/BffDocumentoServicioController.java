package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.DocumentoServicioBffService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
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
@RequestMapping("/api/documentos-servicio")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffDocumentoServicioController extends BffResponseSupport {

    private final DocumentoServicioBffService documentoServicioBffService;

    public BffDocumentoServicioController(DocumentoServicioBffService documentoServicioBffService) {
        this.documentoServicioBffService = documentoServicioBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) {
        return responder(documentoServicioBffService.listar(jwt));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(documentoServicioBffService.buscarPorUuid(uuid, jwt));
    }

    @GetMapping("/cotizacion/{cotizacionUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorCotizacion(
            @PathVariable String cotizacionUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(documentoServicioBffService.listarPorCotizacion(cotizacionUuid, jwt));
    }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(documentoServicioBffService.crear(request, jwt));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(
            @PathVariable String uuid,
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(documentoServicioBffService.actualizar(uuid, request, jwt));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> eliminar(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(documentoServicioBffService.eliminar(uuid, jwt));
    }
}
