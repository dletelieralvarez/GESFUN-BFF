package cl.gesfun.gesfun_bff.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bff")
public class BffDiagnosticsController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("authenticated", true);
        response.put("subject", jwt.getSubject());
        response.put("issuer", jwt.getIssuer() != null ? jwt.getIssuer().toString() : null);
        response.put("audience", jwt.getAudience());
        response.put("scope", jwt.getClaimAsString("scp"));
        response.put("tenant", jwt.getClaimAsString("tid"));
        response.put("version", jwt.getClaimAsString("ver"));
        response.put("clientId", firstPresentClaim(jwt, "azp", "appid", "client_id"));
        response.put("username", firstPresentClaim(jwt, "preferred_username", "upn", "unique_name"));
        return response;
    }

    private String firstPresentClaim(Jwt jwt, String... claimNames) {
        for (String claimName : claimNames) {
            String value = jwt.getClaimAsString(claimName);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return null;
    }
}
