package cl.gesfun.gesfun_bff.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ProxyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Jwt jwt;

    private ProxyService proxyService;

    @BeforeEach
    void setUp() {
        proxyService = new ProxyService(restTemplate, "http://localhost:8080/");
    }

    @Test
    void forwardRequestConstruyeUrlConContextPathQueryYHeaders() {
        when(request.getRequestURI()).thenReturn("/bff/api/usuarios");
        when(request.getContextPath()).thenReturn("/bff");
        when(request.getQueryString()).thenReturn("activo=1");
        when(request.getContentType()).thenReturn(MediaType.APPLICATION_JSON_VALUE);
        when(jwt.getTokenValue()).thenReturn("token-123");
        when(restTemplate.exchange(
                eq(URI.create("http://localhost:8080/api/usuarios?activo=1")),
                eq(HttpMethod.POST),
                org.mockito.ArgumentMatchers.<HttpEntity<String>>any(),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok("ok"));

        proxyService.forwardRequest(request, HttpMethod.POST, "{\"x\":1}", jwt);

        ArgumentCaptor<HttpEntity<String>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq(URI.create("http://localhost:8080/api/usuarios?activo=1")),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<String> entity = entityCaptor.getValue();
        assertThat(entity.getBody()).isEqualTo("{\"x\":1}");
        assertThat(entity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(entity.getHeaders().getAccept()).containsExactly(MediaType.APPLICATION_JSON);
        assertThat(entity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer token-123");
    }

    @Test
    void forwardToBackendNormalizaPathYUsaJsonPorDefecto() {
        when(jwt.getTokenValue()).thenReturn("token-123");
        when(restTemplate.exchange(
                eq(URI.create("http://localhost:8080/api/planes")),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.<HttpEntity<String>>any(),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok("ok"));

        proxyService.forwardToBackend("api/planes", HttpMethod.GET, null, jwt);

        ArgumentCaptor<HttpEntity<String>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq(URI.create("http://localhost:8080/api/planes")),
                eq(HttpMethod.GET),
                entityCaptor.capture(),
                eq(Object.class)
        );

        assertThat(entityCaptor.getValue().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(entityCaptor.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer token-123");
    }

    @Test
    void forwardToBackendAgregaQueryStringCuandoVieneInformado() {
        when(restTemplate.exchange(
                eq(URI.create("http://localhost:8080/api/terceros?rol=CLIENTE")),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.<HttpEntity<String>>any(),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok("ok"));

        proxyService.forwardToBackend("/api/terceros", "rol=CLIENTE", HttpMethod.GET, null, null);

        verify(restTemplate).exchange(
                eq(URI.create("http://localhost:8080/api/terceros?rol=CLIENTE")),
                eq(HttpMethod.GET),
                org.mockito.ArgumentMatchers.<HttpEntity<String>>any(),
                eq(Object.class)
        );
    }
}
