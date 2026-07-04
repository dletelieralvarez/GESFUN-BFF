package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.SalidaInventarioFacturacion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DocumentoTributarioBffService extends CrudBffService {

    private static final String OBSERVACION_SALIDA_FACTURACION =
            "Salida generada desde facturacion";

    private final InventarioBffService inventarioBffService;
    private final ObjectMapper objectMapper;

    public DocumentoTributarioBffService(
            ProxyService proxyService,
            ObjectMapper objectMapper,
            InventarioBffService inventarioBffService
    ) {
        super("/api/documentos-tributarios", proxyService, objectMapper);
        this.objectMapper = objectMapper;
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

        if (numeroFactura != null && numeroFactura.length() > 30) {
            numeroFactura = null;
        }

        return new SalidaInventarioFacturacion(
                documentoTributarioUuid,
                cotizacionUuid,
                usuarioUuid,
                null,
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

    private String usuarioUuid(Jwt jwt) {
        if (jwt == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pudo determinar el usuario que factura"
            );
        }

        String value = firstPresentClaim(jwt, "usuarioUuid", "user_uuid", "oid", "sub");
        if (value == null || value.isBlank()) {
            value = jwt.getSubject();
        }

        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El JWT no incluye usuarioUuid, user_uuid, oid ni sub"
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
}
