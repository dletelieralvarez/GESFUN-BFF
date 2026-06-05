package cl.gesfun.gesfun_bff.controller;

import cl.gesfun.gesfun_bff.model.FrontendResponse;
import cl.gesfun.gesfun_bff.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BffProxyController {

    /*
        Expone el endpoint BFF principal en /api/**.
        Recibe llamadas desde Angular y las manda al backend a través de ProxyService.
        Autorización por permisos usando @PreAuthorize("hasAuthority('SCOPE_api.read') or hasAuthority('SCOPE_api.write')").
        Envuelve la respuesta del backend en un formato uniforme para el frontend con FrontendResponse.
    */

    private final ProxyService proxyService;

    public BffProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @PreAuthorize("hasAuthority('SCOPE_https://duocactividadazure.onmicrosoft.com/daead1c3-a4cc-4647-9423-e1fc626d8003/access_as_user')")
    @RequestMapping(value = "/**", method = {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.PATCH,
            RequestMethod.DELETE
    })
    public ResponseEntity<FrontendResponse<Object>> proxyRequest(
            HttpServletRequest request,
            @RequestBody(required = false) String body,
            @AuthenticationPrincipal Jwt jwt) {

        ResponseEntity<Object> backendResponse = proxyService.forwardRequest(request, HttpMethod.valueOf(request.getMethod()), body, jwt);
        return ResponseEntity.status(backendResponse.getStatusCode())
                .body(FrontendResponse.success(backendResponse.getBody()));
    }
}
