package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.Tercero;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class TerceroRolBffService {

    private static final String TERCEROS_PATH = "/api/terceros";

    private final ProxyService proxyService;
    private final ObjectMapper objectMapper;

    public TerceroRolBffService(ProxyService proxyService, ObjectMapper objectMapper) {
        this.proxyService = proxyService;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<Object> listar(String tipoTercero, Jwt jwt) {
        String rol = rolDesdeTipoTercero(tipoTercero);
        ResponseEntity<Object> backendResponse = proxyService.forwardToBackend(TERCEROS_PATH, HttpMethod.GET, null, jwt);
        Object payloadFiltrado = filtrarPorRol(backendResponse.getBody(), rol);

        return ResponseEntity.status(backendResponse.getStatusCode()).body(payloadFiltrado);
    }

    public ResponseEntity<Object> listarTodos(Jwt jwt) {
        return proxyService.forwardToBackend(TERCEROS_PATH, HttpMethod.GET, null, jwt);
    }

    public ResponseEntity<Object> listarPorEmpresa(String tipoTercero, String empresaUuid, Jwt jwt) {
        String rol = rolDesdeTipoTercero(tipoTercero);
        return proxyService.forwardToBackend(
                TERCEROS_PATH + "/empresa/" + empresaUuid,
                "rol=" + rol,
                HttpMethod.GET,
                null,
                jwt
        );
    }

    public ResponseEntity<Object> buscarPorUuid(String uuid, Jwt jwt) {
        return proxyService.forwardToBackend(TERCEROS_PATH + "/" + uuid, HttpMethod.GET, null, jwt);
    }

    public ResponseEntity<Object> crear(String tipoTercero, Tercero tercero, Jwt jwt) throws JsonProcessingException {
        String bodyConRol = aplicarRol(tercero, rolDesdeTipoTercero(tipoTercero));
        return proxyService.forwardToBackend(TERCEROS_PATH, HttpMethod.POST, bodyConRol, jwt);
    }

    public ResponseEntity<Object> actualizar(String tipoTercero, String uuid, Tercero tercero, Jwt jwt) throws JsonProcessingException {
        String bodyConRol = aplicarRol(tercero, rolDesdeTipoTercero(tipoTercero));
        return proxyService.forwardToBackend(TERCEROS_PATH + "/" + uuid, HttpMethod.PUT, bodyConRol, jwt);
    }

    public ResponseEntity<Object> desactivar(String uuid, Jwt jwt) {
        return proxyService.forwardToBackend(TERCEROS_PATH + "/" + uuid + "/desactivar", HttpMethod.PATCH, null, jwt);
    }

    private Object filtrarPorRol(Object backendBody, String rol) {
        JsonNode payload = objectMapper.valueToTree(backendBody);
        if (!payload.isArray()) {
            return backendBody;
        }

        ArrayNode filtrados = objectMapper.createArrayNode();
        for (JsonNode item : payload) {
            JsonNode rolNode = item.get("rol");
            if (rolNode != null && rol.equalsIgnoreCase(rolNode.asText())) {
                filtrados.add(item);
            }
        }

        return filtrados;
    }

    private String aplicarRol(Object request, String rol) throws JsonProcessingException {
        ObjectNode payload = request == null
                ? objectMapper.createObjectNode()
                : objectMapper.valueToTree(request);

        payload.put("rol", rol);
        return objectMapper.writeValueAsString(payload);
    }

    private String rolDesdeTipoTercero(String tipoTercero) {
        return switch (tipoTercero) {
            case "clientes" -> "CLIENTE";
            case "proveedores" -> "PROVEEDOR";
            case "empleados" -> "EMPLEADO";
            default -> throw new IllegalArgumentException("Tipo de tercero no soportado: " + tipoTercero);
        };
    }
}
