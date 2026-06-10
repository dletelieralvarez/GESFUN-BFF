package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.DatabaseHealthBffService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@PreAuthorize(BffResponseSupport.ACCESS_AS_USER)
public class BffDatabaseHealthController extends BffResponseSupport {

    private final DatabaseHealthBffService databaseHealthBffService;

    public BffDatabaseHealthController(DatabaseHealthBffService databaseHealthBffService) {
        this.databaseHealthBffService = databaseHealthBffService;
    }

    @GetMapping("/database")
    public ResponseEntity<FrontendResponse<Object>> consultar(@AuthenticationPrincipal Jwt jwt) {
        return responder(databaseHealthBffService.consultar(jwt));
    }
}
