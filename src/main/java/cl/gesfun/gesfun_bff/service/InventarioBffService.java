package cl.gesfun.gesfun_bff.service;

import cl.gesfun.gesfun_bff.model.AnulacionMovimientoInventario;
import cl.gesfun.gesfun_bff.model.EntradaInventario;
import cl.gesfun.gesfun_bff.model.SalidaInventario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class InventarioBffService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String inventarioBaseUrl;

    public InventarioBffService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${inventario.base-url}") String inventarioBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.inventarioBaseUrl = inventarioBaseUrl;
    }

    public ResponseEntity<Object> registrarEntrada(EntradaInventario request, Jwt jwt)
            throws JsonProcessingException {
        return forward("/api/inventario/entradas", HttpMethod.POST, request, jwt);
    }

    public ResponseEntity<Object> registrarSalida(SalidaInventario request, Jwt jwt)
            throws JsonProcessingException {
        return forward("/api/inventario/salidas", HttpMethod.POST, request, jwt);
    }

    public ResponseEntity<Object> anularMovimiento(
            String movimientoUuid,
            AnulacionMovimientoInventario request,
            Jwt jwt
    ) throws JsonProcessingException {
        return forward("/api/inventario/movimientos/" + movimientoUuid + "/anular",
                HttpMethod.PATCH, request, jwt);
    }

    public ResponseEntity<Object> consultarStockSucursal(String sucursalUuid, Jwt jwt) {
        URI uri = uriBuilder("/api/inventario/stock")
                .queryParam("sucursalUuid", sucursalUuid)
                .build().encode().toUri();
        return forward(uri, HttpMethod.GET, null, jwt);
    }

    public ResponseEntity<Object> consultarStockProducto(
            String productoUuid,
            String sucursalUuid,
            Jwt jwt
    ) {
        URI uri = uriBuilder("/api/inventario/stock/productos/" + productoUuid)
                .queryParam("sucursalUuid", sucursalUuid)
                .build().encode().toUri();
        return forward(uri, HttpMethod.GET, null, jwt);
    }

    public ResponseEntity<Object> consultarKardex(
            String productoUuid,
            String sucursalUuid,
            Jwt jwt
    ) {
        URI uri = uriBuilder("/api/inventario/reportes/kardex")
                .queryParam("productoUuid", productoUuid)
                .queryParam("sucursalUuid", sucursalUuid)
                .build().encode().toUri();
        return forward(uri, HttpMethod.GET, null, jwt);
    }

    private ResponseEntity<Object> forward(
            String path,
            HttpMethod method,
            Object request,
            Jwt jwt
    ) throws JsonProcessingException {
        String body = request == null ? null : objectMapper.writeValueAsString(request);
        return forward(uriBuilder(path).build().encode().toUri(), method, body, jwt);
    }

    private ResponseEntity<Object> forward(URI uri, HttpMethod method, String body, Jwt jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (jwt != null) {
            headers.setBearerAuth(jwt.getTokenValue());
        }

        return restTemplate.exchange(uri, method, new HttpEntity<>(body, headers), Object.class);
    }

    private UriComponentsBuilder uriBuilder(String path) {
        String baseUrl = inventarioBaseUrl.replaceAll("/+$", "");
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return UriComponentsBuilder.fromUriString(baseUrl + normalizedPath);
    }
}
