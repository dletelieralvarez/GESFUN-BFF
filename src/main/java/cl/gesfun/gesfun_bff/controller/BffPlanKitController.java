package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.model.PlanKit;
import cl.gesfun.gesfun_bff.service.PlanKitBffService;
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
@RequestMapping("/api/plan-kit")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffPlanKitController extends BffResponseSupport {

    private final PlanKitBffService planKitBffService;

    public BffPlanKitController(PlanKitBffService planKitBffService) {
        this.planKitBffService = planKitBffService;
    }

    @GetMapping
    public ResponseEntity<FrontendResponse<Object>> listar(@AuthenticationPrincipal Jwt jwt) { return responder(planKitBffService.listar(jwt)); }

    @GetMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> buscarPorUuid(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(planKitBffService.buscarPorUuid(uuid, jwt)); }

    @GetMapping("/plan/{planUuid}")
    public ResponseEntity<FrontendResponse<Object>> listarPorPlan(@PathVariable String planUuid, @AuthenticationPrincipal Jwt jwt) { return responder(planKitBffService.listarPorPlan(planUuid, jwt)); }

    @PostMapping
    public ResponseEntity<FrontendResponse<Object>> crear(@Valid @RequestBody PlanKit planKit, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(planKitBffService.crear(planKit, jwt)); }

    @PutMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> actualizar(@PathVariable String uuid, @Valid @RequestBody PlanKit planKit, @AuthenticationPrincipal Jwt jwt) throws JsonProcessingException { return responder(planKitBffService.actualizar(uuid, planKit, jwt)); }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FrontendResponse<Object>> eliminar(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) { return responder(planKitBffService.eliminar(uuid, jwt)); }
}
