package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class PlanKitBffService extends CrudBffService {

    public PlanKitBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/plan-kit", proxyService, objectMapper);
    }

    public ResponseEntity<Object> listarPorPlan(String planUuid, Jwt jwt) {
        return buscarPorRuta("/plan/" + planUuid, jwt);
    }
}
