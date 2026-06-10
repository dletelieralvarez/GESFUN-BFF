package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class PlanBffService extends CrudBffService {

    public PlanBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/planes", proxyService, objectMapper);
    }

    public ResponseEntity<Object> listarPorSucursal(String sucursalUuid, Jwt jwt) {
        return buscarPorRuta("/sucursal/" + sucursalUuid, jwt);
    }
}
