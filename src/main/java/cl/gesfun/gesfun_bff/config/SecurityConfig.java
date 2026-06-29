package cl.gesfun.gesfun_bff.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    /*
        Configura Spring Security para el BFF.
        Activa validación JWT como recurso protegido (oauth2ResourceServer).
        Habilita CORS para que Angular pueda llamar desde http://localhost:4200 u orígenes configurados.
        Define las rutas protegidas: /api/** requiere autenticación.
        Convierte claims scp de Azure AD en authorities con prefijo SCOPE_.
    */
   
    @Value("${cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.audience}")
    private String audience;

    @Value("${security.jwt.allowed-client-ids:}")
    private String allowedClientIds;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/bff/**").authenticated()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn(
                            "BFF rechazo request antes del controller. method={} path={} status=401 reason={}",
                            request.getMethod(),
                            request.getRequestURI(),
                            authException.getMessage()
                    );
                    response.sendError(org.springframework.http.HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
                })
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn(
                            "BFF denego request autenticado antes del controller. method={} path={} status=403 reason={}",
                            request.getMethod(),
                            request.getRequestURI(),
                            accessDeniedException.getMessage()
                    );
                    response.sendError(org.springframework.http.HttpStatus.FORBIDDEN.value(), "Forbidden");
                })
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("SCOPE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scp");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuerUri),
                audienceValidator(),
                allowedClientValidator()
        );

        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }

    private OAuth2TokenValidator<Jwt> audienceValidator() {
        return jwt -> {
            if (jwt.getAudience().contains(audience)) {
                return OAuth2TokenValidatorResult.success();
            }

            log.warn("JWT rechazado por audience invalida. expectedAudience={} tokenAudience={}", audience, jwt.getAudience());
            OAuth2Error error = new OAuth2Error("invalid_token", "El token no fue emitido para este BFF.", null);
            return OAuth2TokenValidatorResult.failure(error);
        };
    }

    private OAuth2TokenValidator<Jwt> allowedClientValidator() {
        List<String> allowedClients = Arrays.stream(allowedClientIds.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toList());

        return jwt -> {
            if (allowedClients.isEmpty()) {
                return OAuth2TokenValidatorResult.success();
            }

            String clientId = firstPresentClaim(jwt, "azp", "appid", "client_id");
            if (clientId != null && allowedClients.contains(clientId)) {
                return OAuth2TokenValidatorResult.success();
            }

            log.warn("JWT rechazado por client id no permitido. clientId={} allowedClientIds={}", clientId, allowedClients);
            OAuth2Error error = new OAuth2Error("invalid_token", "El token fue emitido por un cliente no permitido.", null);
            return OAuth2TokenValidatorResult.failure(error);
        };
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toList());

        corsConfiguration.setAllowedOrigins(origins);
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }
}
