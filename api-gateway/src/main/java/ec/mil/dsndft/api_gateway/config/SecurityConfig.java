package ec.mil.dsndft.api_gateway.config;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import ec.mil.dsndft.api_gateway.security.BearerTokenServerAuthenticationConverter;
import ec.mil.dsndft.api_gateway.security.JwtAuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(java.util.List.of(
            "http://localhost:4200",
            "http://127.0.0.1:4200",
            "http://localhost:8080",
            "http://127.0.0.1:8080"
        ));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        config.setExposedHeaders(java.util.List.of("Authorization"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private final JwtAuthenticationManager authenticationManager;
    private final BearerTokenServerAuthenticationConverter authenticationConverter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(authenticationConverter);
        ServerWebExchangeMatcher publicEndpoints = ServerWebExchangeMatchers.matchers(
              ServerWebExchangeMatchers.pathMatchers("/auth/**"),
              ServerWebExchangeMatchers.pathMatchers("/catalogos/api/auth/**"),
              ServerWebExchangeMatchers.pathMatchers("/catalogos/api/password-change/**"),
              ServerWebExchangeMatchers.pathMatchers("/catalogos/password-change/**"),
              ServerWebExchangeMatchers.pathMatchers("/gestion/api/auth/**"),
              ServerWebExchangeMatchers.pathMatchers("/documentos/api/auth/**"),
              ServerWebExchangeMatchers.pathMatchers("/actuator/**")
        );
        authenticationWebFilter.setRequiresAuthenticationMatcher(new NegatedServerWebExchangeMatcher(publicEndpoints));

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .exceptionHandling(spec -> spec
                .authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() ->
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((exchange, ex) -> Mono.fromRunnable(() ->
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
            )
            .authorizeExchange(exchange -> exchange
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers(
                    "/auth/**",
                    "/catalogos/api/auth/**",
                    "/catalogos/api/password-change/**",
                    "/catalogos/password-change/**",
                    "/gestion/api/auth/**",
                    "/documentos/api/auth/**",
                    "/actuator/health",
                    "/actuator/info"
                ).permitAll()
                .anyExchange().authenticated()
            )
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}
