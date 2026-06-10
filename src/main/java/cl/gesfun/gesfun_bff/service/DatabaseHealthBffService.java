package cl.gesfun.gesfun_bff.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class DatabaseHealthBffService {

    private final ProxyService proxyService;

    public DatabaseHealthBffService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    public ResponseEntity<Object> consultar(Jwt jwt) {
        return proxyService.forwardToBackend("/api/health/database", HttpMethod.GET, null, jwt);
    }
}
