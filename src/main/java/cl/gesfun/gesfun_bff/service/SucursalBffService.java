package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class SucursalBffService extends CrudBffService {

    public SucursalBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/sucursales", proxyService, objectMapper);
    }

    public ResponseEntity<Object> listarPorEmpresa(String empresaUuid, Jwt jwt) {
        return buscarPorRuta("/empresa/" + empresaUuid, jwt);
    }
}
