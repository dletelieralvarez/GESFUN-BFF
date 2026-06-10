package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.Plan;
import cl.gesfun.gesfun_bff.service.PlanBffService;
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
@RequestMapping("/api/planes")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffPlanController extends BffResponseSupport {

    private final PlanBffService planBffService;

    public BffPlanController(PlanBffService planBffService) {
        this.planBffService = planBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) { return responder(planBffService.listar(jwt)); }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(planBffService.buscarPorUuid(uuid, jwt)); }

    @GetMapping("/sucursal/{sucursalUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorSucursal(@PathVariable String sucursalUuid, @AuthenticationPrincipal Jwt jwt) { return responder(planBffService.listarPorSucursal(sucursalUuid, jwt)); }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody Plan plan, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(planBffService.crear(plan, jwt)); }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody Plan plan, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(planBffService.actualizar(uuid, plan, jwt)); }

    @PatchMapping("/{uuid}/desactivar")
    public ResponseEntity<FrontendResponse<Object>> desactivar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(planBffService.desactivar(uuid, jwt)); }
}
