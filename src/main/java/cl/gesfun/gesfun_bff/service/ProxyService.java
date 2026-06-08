package cl.gesfun.gesfun_bff.service;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Collections;

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

@Service
public class ProxyService {

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
        return restTemplate.exchange(backendUri, method, entity, Object.class);
    }

    private URI buildBackendUri(HttpServletRequest request) {
        String targetUrl = normalizeBackendBaseUrl() + extractRequestPath(request);

        if (StringUtils.hasText(request.getQueryString())) {
            targetUrl += "?" + request.getQueryString();
        }

        return URI.create(targetUrl);
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
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String contentType = request.getContentType();
        if (StringUtils.hasText(contentType)) {
            headers.setContentType(MediaType.parseMediaType(contentType));
        }

        if (jwt != null) {
            headers.setBearerAuth(jwt.getTokenValue());
        }

        return headers;
    }
}
