package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.DocumentoTributarioEmitir;
import cl.gesfun.gesfun_bff.model.DocumentoTributarioUpdate;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.DocumentoTributarioBffService;
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
@RequestMapping("/api/documentos-tributarios")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffDocumentoTributarioController extends BffResponseSupport {

    private final DocumentoTributarioBffService documentoTributarioBffService;

    public BffDocumentoTributarioController(DocumentoTributarioBffService documentoTributarioBffService) {
        this.documentoTributarioBffService = documentoTributarioBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) {
        return responder(documentoTributarioBffService.listar(jwt));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(documentoTributarioBffService.buscarPorUuid(uuid, jwt));
    }

    @GetMapping("/pago/{pagoUuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorPago(
            @PathVariable String pagoUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(documentoTributarioBffService.buscarPorPago(pagoUuid, jwt));
    }

    @GetMapping("/cotizacion/{cotizacionUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorCotizacion(
            @PathVariable String cotizacionUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(documentoTributarioBffService.listarPorCotizacion(cotizacionUuid, jwt));
    }

    @PostMapping("/emitir")
    public ResponseEntity<FrontendResponse<Object>> emitir(
            @Valid @RequestBody DocumentoTributarioEmitir request,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(documentoTributarioBffService.emitir(request, jwt));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(
            @PathVariable String uuid,
            @Valid @RequestBody DocumentoTributarioUpdate request,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(documentoTributarioBffService.actualizar(uuid, request, jwt));
    }

    @PostMapping("/{uuid}/reenviar")
    public ResponseEntity<FrontendResponse<Object>> reenviar(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(documentoTributarioBffService.reenviar(uuid, jwt));
    }

    @PatchMapping("/{uuid}/anular")
    public ResponseEntity<FrontendResponse<Object>> anular(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(documentoTributarioBffService.anular(uuid, jwt));
    }
}
