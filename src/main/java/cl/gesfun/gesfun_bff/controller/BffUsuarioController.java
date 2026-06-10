package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.Usuario;
import cl.gesfun.gesfun_bff.service.UsuarioBffService;
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
@RequestMapping("/api/usuarios")
@PreAuthorize("hasAuthority('SCOPE_access_as_user') or hasAuthority('SCOPE_https://duocactividadazure.onmicrosoft.com/daead1c3-a4cc-4647-9423-e1fc626d8003/access_as_user')")
public class BffUsuarioController {

    private final UsuarioBffService usuarioBffService;

    public BffUsuarioController(UsuarioBffService usuarioBffService) {
        this.usuarioBffService = usuarioBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) {
        return responder(usuarioBffService.listar(jwt));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {

        return responder(usuarioBffService.buscarPorId(id, jwt));
    }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(
            @Valid @RequestBody Usuario usuario,
            @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException {

        return responder(usuarioBffService.crear(usuario, jwt));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody Usuario usuario,
            @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException {

        return responder(usuarioBffService.actualizar(id, usuario, jwt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FrontendResponse<Object>> eliminar(
            @PathVariable Integer id,
            @AuthenticationPrincipal Jwt jwt) {

        return responder(usuarioBffService.eliminar(id, jwt));
    }

    private ResponseEntity<FrontendResponse<Object>> responder(ResponseEntity<Object> backendResponse) {
        return ResponseEntity.status(backendResponse.getStatusCode())
                .body(FrontendResponse.success(backendResponse.getBody()));
    }
}
