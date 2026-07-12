package cl.gesfun.gesfun_bff.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    private SecurityConfig config;

    @BeforeEach
    void setUp() {
        config = new SecurityConfig();
        ReflectionTestUtils.setField(config, "allowedOrigins", "http://localhost:4200, https://gesfun.duckdns.org");
        ReflectionTestUtils.setField(config, "audience", "api-audience");
        ReflectionTestUtils.setField(config, "allowedClientIds", "client-1,client-2");
    }

    @Test
    void corsConfigurationUsaOriginsMetodosHeadersYCredenciales() {
        CorsConfigurationSource source = config.corsConfigurationSource();

        CorsConfiguration cors = source.getCorsConfiguration(new org.springframework.mock.web.MockHttpServletRequest("GET", "/api/test"));

        assertThat(cors).isNotNull();
        assertThat(cors.getAllowedOrigins()).containsExactly("http://localhost:4200", "https://gesfun.duckdns.org");
        assertThat(cors.getAllowedMethods()).containsExactly("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        assertThat(cors.getAllowedHeaders()).containsExactly("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With");
        assertThat(cors.getAllowCredentials()).isTrue();
    }

    @Test
    void audienceValidatorAceptaAudienceEsperadaYRechazaOtra() throws Exception {
        OAuth2TokenValidator<Jwt> validator = validator("audienceValidator");

        assertThat(validator.validate(jwt(Map.of(), List.of("api-audience"))).hasErrors()).isFalse();
        assertThat(validator.validate(jwt(Map.of(), List.of("otra-audience"))).hasErrors()).isTrue();
    }

    @Test
    void allowedClientValidatorAceptaClaimsPermitidosYRechazaNoPermitidos() throws Exception {
        OAuth2TokenValidator<Jwt> validator = validator("allowedClientValidator");

        assertThat(validator.validate(jwt(Map.of("azp", "client-1"), List.of("api-audience"))).hasErrors()).isFalse();
        assertThat(validator.validate(jwt(Map.of("appid", "client-2"), List.of("api-audience"))).hasErrors()).isFalse();
        assertThat(validator.validate(jwt(Map.of("client_id", "client-3"), List.of("api-audience"))).hasErrors()).isTrue();
    }

    @Test
    void allowedClientValidatorAceptaTodosCuandoListaEstaVacia() throws Exception {
        ReflectionTestUtils.setField(config, "allowedClientIds", "");
        OAuth2TokenValidator<Jwt> validator = validator("allowedClientValidator");

        assertThat(validator.validate(jwt(Map.of("azp", "cualquiera"), List.of("api-audience"))).hasErrors()).isFalse();
    }

    @SuppressWarnings("unchecked")
    private OAuth2TokenValidator<Jwt> validator(String methodName) throws Exception {
        Method method = SecurityConfig.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return (OAuth2TokenValidator<Jwt>) method.invoke(config);
    }

    private Jwt jwt(Map<String, Object> claims, List<String> audience) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuer("https://issuer")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .audience(audience)
                .claims(jwtClaims -> jwtClaims.putAll(claims))
                .build();
    }
}
