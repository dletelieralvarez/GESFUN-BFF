package cl.gesfun.gesfun_bff.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

public abstract class CrudBffService {

    private final String backendPath;
    private final ProxyService proxyService;
    private final ObjectMapper objectMapper;

    protected CrudBffService(String backendPath, ProxyService proxyService, ObjectMapper objectMapper) {
        this.backendPath = backendPath;
        this.proxyService = proxyService;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<Object> listar(Jwt jwt) {
        return forward("", HttpMethod.GET, null, jwt);
    }

    public ResponseEntity<Object> buscarPorUuid(String uuid, Jwt jwt) {
        return forward("/" + uuid, HttpMethod.GET, null, jwt);
    }

    public ResponseEntity<Object> crear(Object request, Jwt jwt) throws JsonProcessingException {
        return forward("", HttpMethod.POST, toJson(request), jwt);
    }

    public ResponseEntity<Object> actualizar(String uuid, Object request, Jwt jwt) throws JsonProcessingException {
        return forward("/" + uuid, HttpMethod.PUT, toJson(request), jwt);
    }

    public ResponseEntity<Object> eliminar(String uuid, Jwt jwt) {
        return forward("/" + uuid, HttpMethod.DELETE, null, jwt);
    }

    public ResponseEntity<Object> desactivar(String uuid, Jwt jwt) {
        return forward("/" + uuid + "/desactivar", HttpMethod.PATCH, null, jwt);
    }

    protected ResponseEntity<Object> buscarPorRuta(String path, Jwt jwt) {
        return forward(path, HttpMethod.GET, null, jwt);
    }

    protected ResponseEntity<Object> forward(String path, HttpMethod method, String body, Jwt jwt) {
        return proxyService.forwardToBackend(backendPath + path, method, body, jwt);
    }

    private String toJson(Object request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }
}
