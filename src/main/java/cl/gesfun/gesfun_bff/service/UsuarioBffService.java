package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UsuarioBffService {

    private static final String USUARIOS_PATH = "/api/usuarios";

    private final ProxyService proxyService;
    private final ObjectMapper objectMapper;

    public UsuarioBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        this.proxyService = proxyService;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<Object> listar(Jwt jwt) {
        return proxyService.forwardToBackend(USUARIOS_PATH, HttpMethod.GET, null, jwt);
    }

    public ResponseEntity<Object> buscarPorId(Integer id, Jwt jwt) {
        return proxyService.forwardToBackend(USUARIOS_PATH + "/" + id, HttpMethod.GET, null, jwt);
    }

    public ResponseEntity<Object> crear(Usuario usuario, Jwt jwt) throws JsonProcessingException {
        return proxyService.forwardToBackend(USUARIOS_PATH, HttpMethod.POST, toJson(usuario), jwt);
    }

    public ResponseEntity<Object> actualizar(Integer id, Usuario usuario, Jwt jwt) throws JsonProcessingException {
        return proxyService.forwardToBackend(USUARIOS_PATH + "/" + id, HttpMethod.PUT, toJson(usuario), jwt);
    }

    public ResponseEntity<Object> eliminar(Integer id, Jwt jwt) {
        return proxyService.forwardToBackend(USUARIOS_PATH + "/" + id, HttpMethod.DELETE, null, jwt);
    }

    private String toJson(Object request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }
}
