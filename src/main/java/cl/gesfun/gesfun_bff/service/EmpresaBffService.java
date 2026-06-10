package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class EmpresaBffService extends CrudBffService {

    public EmpresaBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        super("/api/empresas", proxyService, objectMapper);
    }

    public ResponseEntity<Object> listarPorUsuario(String usuarioUuid, Jwt jwt) {
        return buscarPorRuta("/usuario/" + usuarioUuid, jwt);
    }
}
