package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.SalidaInventarioFacturacion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DocumentoTributarioBffService extends CrudBffService {

    private static final String OBSERVACION_SALIDA_FACTURACION =
            "Salida generada por facturacion";

    private final InventarioBffService inventarioBffService;
    private final ObjectMapper objectMapper;
    private final ProxyService proxyService;

    public DocumentoTributarioBffService(
            ProxyService proxyService,
            ObjectMapper objectMapper,
            InventarioBffService inventarioBffService
    ) {
        super("/api/documentos-tributarios", proxyService, objectMapper);
        this.objectMapper = objectMapper;
        this.proxyService = proxyService;
        this.inventarioBffService = inventarioBffService;
    }

    public ResponseEntity<Object> listarPorCotizacion(String cotizacionUuid, Jwt jwt) {
        return buscarPorRuta("/cotizacion/" + cotizacionUuid, jwt);
    }

    public ResponseEntity<Object> buscarPorPago(String pagoUuid, Jwt jwt) {
        return buscarPorRuta("/pago/" + pagoUuid, jwt);
    }

    public ResponseEntity<Object> emitir(Object request, Jwt jwt) throws JsonProcessingException {
        ResponseEntity<Object> response = forward("/emitir", HttpMethod.POST, toJson(request), jwt);

        if (response.getStatusCode().is2xxSuccessful()) {
            inventarioBffService.registrarSalidaPorFacturacion(
                    salidaPorFacturacionDesdeDocumento(response.getBody(), jwt),
                    jwt
            );
        }

        return response;
    }

    public ResponseEntity<Object> reenviar(String uuid, Jwt jwt) {
        return forward("/" + uuid + "/reenviar", HttpMethod.POST, null, jwt);
    }

    public ResponseEntity<Object> anular(String uuid, Jwt jwt) {
        return forward("/" + uuid + "/anular", HttpMethod.PATCH, null, jwt);
    }

    private SalidaInventarioFacturacion salidaPorFacturacionDesdeDocumento(Object responseBody, Jwt jwt) {
        JsonNode documento = documentoNode(responseBody);
        String documentoTributarioUuid = requiredText(documento, "uuid", "documentoTributarioUuid");
        String cotizacionUuid = requiredText(documento, "cotizacionUuid");
        String usuarioUuid = usuarioUuid(jwt);
        String numeroFactura = optionalText(documento, "folio");
        LocalDate fechaDocumento = fechaDocumento(documento);

        if (numeroFactura != null && numeroFactura.length() > 30) {
            numeroFactura = null;
        }

        return new SalidaInventarioFacturacion(
                documentoTributarioUuid,
                cotizacionUuid,
                usuarioUuid,
                fechaDocumento,
                numeroFactura,
                OBSERVACION_SALIDA_FACTURACION
        );
    }

    private JsonNode documentoNode(Object responseBody) {
        JsonNode root = objectMapper.valueToTree(responseBody);

        for (String wrapperField : List.of("payload", "data", "body")) {
            JsonNode wrapped = root.path(wrapperField);
            if (wrapped.isObject()) {
                return wrapped;
            }
        }

        return root;
    }

    private String requiredText(JsonNode node, String... fieldNames) {
        String value = optionalText(node, fieldNames);
        if (value == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La respuesta de facturacion no incluye " + String.join(" o ", fieldNames)
            );
        }

        return value;
    }

    private String optionalText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode value = node.path(fieldName);
            if (value.isTextual() && !value.asText().isBlank()) {
                return value.asText();
            }
        }

        return null;
    }

    private LocalDate fechaDocumento(JsonNode documento) {
        String value = optionalText(documento, "fechaDocumento", "fechaEmision", "fecha");
        if (value == null) {
            return LocalDate.now();
        }

        try {
            return LocalDate.parse(value.length() >= 10 ? value.substring(0, 10) : value);
        } catch (RuntimeException ex) {
            return LocalDate.now();
        }
    }

    private String usuarioUuid(Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pudo determinar el usuario que factura"
            );
        }

        String usuarioUuid = usuarioUuidDesdeBackend(jwt);
        if (usuarioUuid != null) {
            return usuarioUuid;
        }

        String value = firstPresentClaim(jwt, "usuarioUuid", "user_uuid");
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El JWT no incluye usuarioUuid ni email para buscar el usuario interno"
            );
        }

        return value;
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

    private String usuarioUuidDesdeBackend(Jwt jwt) {
        String email = firstPresentClaim(jwt, "email", "preferred_username", "upn", "unique_name");
        if (email == null || email.isBlank()) {
            return null;
        }

        ResponseEntity<Object> usuariosResponse =
                proxyService.forwardToBackend("/api/usuarios", HttpMethod.GET, null, jwt);
        JsonNode usuarios = usuariosNode(usuariosResponse.getBody());
        String usuarioUuid = buscarUsuarioActivoPorEmail(usuarios, email);
        if (usuarioUuid == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se encontro un usuario activo para el email del JWT"
            );
        }

        return usuarioUuid;
    }

    private JsonNode usuariosNode(Object responseBody) {
        JsonNode root = objectMapper.valueToTree(responseBody);
        if (root.isArray()) {
            return root;
        }

        for (String wrapperField : List.of("payload", "data", "body", "content")) {
            JsonNode wrapped = root.path(wrapperField);
            if (wrapped.isArray()) {
                return wrapped;
            }
        }

        return objectMapper.createArrayNode();
    }

    private String buscarUsuarioActivoPorEmail(JsonNode usuarios, String email) {
        String normalizedEmail = email.toLowerCase(Locale.ROOT);
        for (JsonNode usuario : usuarios) {
            String usuarioEmail = optionalText(usuario, "email", "correo");
            if (usuarioEmail == null || !usuarioEmail.toLowerCase(Locale.ROOT).equals(normalizedEmail)) {
                continue;
            }

            if (!usuarioActivo(usuario)) {
                continue;
            }

            return optionalText(usuario, "uuid", "usuarioUuid");
        }

        return null;
    }

    private boolean usuarioActivo(JsonNode usuario) {
        JsonNode activo = usuario.path("activo");
        if (activo.isMissingNode() || activo.isNull()) {
            return true;
        }

        if (activo.isBoolean()) {
            return activo.asBoolean();
        }

        if (activo.isNumber()) {
            return activo.asInt() == 1;
        }

        if (activo.isTextual()) {
            String value = activo.asText();
            return "1".equals(value) || "true".equalsIgnoreCase(value) || "ACTIVO".equalsIgnoreCase(value);
        }

        return false;
    }
}
