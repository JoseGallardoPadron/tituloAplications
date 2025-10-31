package pe.edu.vallegrande.VaccineAplication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    /* ============================ CONFIG BÁSICA ============================ */

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    private static final List<String> STATIC_ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:4200");

    private static final Pattern GITPOD_REGEX = Pattern.compile(
            "^https://4200-[a-z0-9\\-]+\\.ws-[a-z0-9]+\\.gitpod\\.io$");

    /* ===================== CADENA DE FILTROS DE SEGURIDAD ================== */

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                /* --------- MANEJO CENTRALIZADO DE ERRORES 401 / 403 --------- */
                .exceptionHandling(handlers -> handlers
                        .authenticationEntryPoint(unauthorizedHandler()) // 401
                        .accessDeniedHandler(forbiddenHandler()) // 403
                )
                /* ---------------------------------------------------------------- */

                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        /* Aplicaciones de Vacunas */
                        .pathMatchers(HttpMethod.POST, "/vaccine-applications/create").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/vaccine-applications").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/vaccine-applications/{id}").hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/vaccine-applications/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/vaccine-applications/{id}").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/vaccine-applications/activate/{id}").hasRole("ADMIN")

                        .anyExchange().authenticated())

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder())
                                .jwtAuthenticationConverter(this::convertJwt)))

                .cors(cors -> cors.configurationSource(dynamicCorsConfigurationSource()))
                .build();
    }

    /* ======================== JWT DECODER & CONVERTER ====================== */

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    private Mono<CustomAuthenticationToken> convertJwt(Jwt jwt) {
        String role = jwt.getClaimAsString("role");
        Collection<GrantedAuthority> authorities = role != null
                ? List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                : List.of();
        return Mono.just(new CustomAuthenticationToken(jwt, authorities));
    }

    /* ============================== CORS DINÁMICO =========================== */

    private CorsConfigurationSource dynamicCorsConfigurationSource() {
        return new UrlBasedCorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
                String origin = exchange.getRequest().getHeaders().getOrigin();
                if (isAllowedOrigin(origin)) {
                    CorsConfiguration cfg = new CorsConfiguration();
                    cfg.setAllowedOrigins(List.of(origin));
                    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(List.of("*"));
                    cfg.setAllowCredentials(true);
                    cfg.setMaxAge(3600L);
                    return cfg;
                }
                return null;
            }
        };
    }

    private boolean isAllowedOrigin(String origin) {
        if (origin == null)
            return false;
        return STATIC_ALLOWED_ORIGINS.contains(origin) || GITPOD_REGEX.matcher(origin).matches();
    }

    /* ================== HANDLERS PARA 401 & 403 PERSONALIZADOS ============== */

    /** Respuesta 401 – usuario no autenticado (token faltante/inválido) */
    private ServerAuthenticationEntryPoint unauthorizedHandler() {
        return (exchange, ex) -> buildJson(
                exchange,
                HttpStatus.UNAUTHORIZED,
                "{\"error\":\"No autorizado – token inválido o vencido\"}");

        /* --- Si prefieres redirigir al login en lugar de JSON: --- */
        /*
         * return (exchange, ex) -> {
         * exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER); // 303
         * exchange.getResponse().getHeaders()
         * .setLocation(URI.create("https://tu-frontend.com/login"));
         * return exchange.getResponse().setComplete();
         * };
         */
    }

    /** Respuesta 403 – usuario autenticado sin permisos suficientes */
    private ServerAccessDeniedHandler forbiddenHandler() {
        return (exchange, ex) -> buildJson(
                exchange,
                HttpStatus.FORBIDDEN,
                "{\"error\":\"Acceso denegado – rol insuficiente\"}");
    }

    /* ================ UTILIDAD PARA CONSTRUIR RESPUESTA JSON ================= */

    private Mono<Void> buildJson(ServerWebExchange exchange,
            HttpStatus status,
            String jsonBody) {

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBufferFactory factory = exchange.getResponse().bufferFactory();
        DataBuffer buffer = factory.wrap(jsonBody.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
