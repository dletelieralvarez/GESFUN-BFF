package cl.gesfun.gesfun_bff.service;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClientResponseException;

@Service
public class ProxyService {

    private static final Logger log = LoggerFactory.getLogger(ProxyService.class);

    /*
        Implementa la lógica de reenvío de peticiones hacia el backend real.
        Construye la URL del backend usando backend.base-url y la ruta de la petición /api/....
        Copia headers importantes como Authorization y Content-Type.
        Envía la petición al backend con RestTemplate y devuelve la respuesta original.
    */
   
    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    public ProxyService(RestTemplate restTemplate,
                        @Value("${backend.base-url}") String backendBaseUrl) {
        this.restTemplate = restTemplate;
        this.backendBaseUrl = backendBaseUrl;
    }

    public ResponseEntity<Object> forwardRequest(HttpServletRequest request, HttpMethod method, String body, Jwt jwt) {
        URI backendUri = buildBackendUri(request);
        HttpHeaders headers = buildForwardHeaders(request, jwt);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return exchange(backendUri, method, entity, jwt);
    }

    public ResponseEntity<Object> forwardToBackend(String backendPath, HttpMethod method, String body, Jwt jwt) {
        URI backendUri = buildBackendUri(backendPath);
        HttpHeaders headers = buildForwardHeaders(MediaType.APPLICATION_JSON_VALUE, jwt);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        return exchange(backendUri, method, entity, jwt);
    }

    public ResponseEntity<Object> forwardToBackend(String backendPath, String queryString, HttpMethod method, String body, Jwt jwt) {
        String pathWithQuery = backendPath;

        if (StringUtils.hasText(queryString)) {
            pathWithQuery += "?" + queryString;
        }

        return forwardToBackend(pathWithQuery, method, body, jwt);
    }

    private URI buildBackendUri(HttpServletRequest request) {
        String targetUrl = normalizeBackendBaseUrl() + extractRequestPath(request);

        if (StringUtils.hasText(request.getQueryString())) {
            targetUrl += "?" + request.getQueryString();
        }

        return URI.create(targetUrl);
    }

    private URI buildBackendUri(String backendPath) {
        String normalizedPath = backendPath.startsWith("/") ? backendPath : "/" + backendPath;
        return URI.create(normalizeBackendBaseUrl() + normalizedPath);
    }

    private String normalizeBackendBaseUrl() {
        return backendBaseUrl.replaceAll("/+$", "");
    }

    private String extractRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (StringUtils.hasText(contextPath) && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }

        return requestUri;
    }

    private HttpHeaders buildForwardHeaders(HttpServletRequest request, Jwt jwt) {
        return buildForwardHeaders(request.getContentType(), jwt);
    }

    private HttpHeaders buildForwardHeaders(String contentType, Jwt jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        if (StringUtils.hasText(contentType)) {
            headers.setContentType(MediaType.parseMediaType(contentType));
        }

        if (jwt != null) {
            headers.setBearerAuth(jwt.getTokenValue());
        }

        return headers;
    }

    private ResponseEntity<Object> exchange(URI backendUri, HttpMethod method, HttpEntity<String> entity, Jwt jwt) {
        log.info(
                "BFF reenviando request al backend. method={} backendUri={} jwtPresent={} clientId={} scopes={}",
                method,
                backendUri,
                jwt != null,
                jwt != null ? firstPresentClaim(jwt, "azp", "appid", "client_id") : null,
                jwt != null ? jwt.getClaimAsString("scp") : null
        );

        try {
            ResponseEntity<Object> response = restTemplate.exchange(backendUri, method, entity, Object.class);
            log.info(
                    "Backend respondio al BFF. method={} backendUri={} status={}",
                    method,
                    backendUri,
                    response.getStatusCode()
            );
            return response;
        } catch (RestClientResponseException ex) {
            log.warn(
                    "Backend respondio error al BFF. method={} backendUri={} status={} responseBody={}",
                    method,
                    backendUri,
                    ex.getRawStatusCode(),
                    ex.getResponseBodyAsString()
            );
            throw ex;
        }
    }

    private String firstPresentClaim(Jwt jwt, String... claimNames) {
        for (String claimName : claimNames) {
            String value = jwt.getClaimAsString(claimName);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }

        return null;
    }
}
