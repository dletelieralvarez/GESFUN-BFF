package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.Agenda;
import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.AgendaBffService;
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
@RequestMapping("/api/agendas")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffAgendaController extends BffResponseSupport {

    private final AgendaBffService agendaBffService;

    public BffAgendaController(AgendaBffService agendaBffService) {
        this.agendaBffService = agendaBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) {
        return responder(agendaBffService.listar(jwt));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(agendaBffService.buscarPorUuid(uuid, jwt));
    }

    @GetMapping("/sucursal/{sucursalUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorSucursal(
            @PathVariable String sucursalUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(agendaBffService.listarPorSucursal(sucursalUuid, jwt));
    }

    @GetMapping("/tipo-recurso/{tipoRecursoUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorTipoRecurso(
            @PathVariable String tipoRecursoUuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(agendaBffService.listarPorTipoRecurso(tipoRecursoUuid, jwt));
    }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(
            @Valid @RequestBody Agenda agenda,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(agendaBffService.crear(agenda, jwt));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(
            @PathVariable String uuid,
            @Valid @RequestBody Agenda agenda,
            @AuthenticationPrincipal Jwt jwt
    ) throws JsonProcessingException {
        return responder(agendaBffService.actualizar(uuid, agenda, jwt));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> eliminar(
            @PathVariable String uuid,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return responder(agendaBffService.eliminar(uuid, jwt));
    }
}
