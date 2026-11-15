package com.neurosync.kinemo.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Set;

@Configuration
@Profile("evaluation")
class EvaluationSecurity {
    static final String INTENT_HEADER = "X-Kinemo-Intent";

    @Bean
    SecurityFilterChain evaluationSecurityFilterChain(HttpSecurity http,
            @Value("${evaluation.frontend-origin}") String origin) throws Exception {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(origin));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type", INTENT_HEADER));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", cors);

        // Browsers from other origins cannot attach this non-simple header without passing CORS.
        RequestMatcher writeWithoutIntent = request -> Set.of("POST", "PUT", "PATCH", "DELETE")
                .contains(request.getMethod()) && !"evaluation".equals(request.getHeader(INTENT_HEADER));
        return http.cors(options -> options.configurationSource(source))
                .csrf(options -> options.disable())
                .sessionManagement(options -> options.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health").permitAll()
                        .requestMatchers(writeWithoutIntent).denyAll()
                        .requestMatchers("/api/v1/children", "/api/v1/children/**", "/api/v1/crisis-alerts",
                                "/api/v1/crisis-alerts/**", "/api/v1/routines", "/api/v1/routines/**",
                                "/api/v1/clinical-guidelines", "/api/v1/clinical-guidelines/**",
                                "/api/v1/practical-guides", "/api/v1/practical-guides/**", "/api/v1/patients",
                                "/api/v1/local-demo/session", "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**").authenticated()
                        .anyRequest().denyAll())
                .httpBasic(Customizer.withDefaults()).build();
    }

    @Bean
    UserDetailsService evaluationUsers(
            @Value("${evaluation.family-password}") String family,
            @Value("${evaluation.caregiver-password}") String caregiver,
            @Value("${evaluation.psychologist-password}") String psychologist) {
        if (java.util.stream.Stream.of(family, caregiver, psychologist)
                .anyMatch(password -> password.length() < 16 || password.length() > 72 || !password.matches("[!-~]+"))
                || java.util.stream.Stream.of(family, caregiver, psychologist).distinct().count() != 3) {
            throw new IllegalArgumentException("Set three distinct evaluation passwords of 16–72 printable ASCII characters.");
        }
        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new InMemoryUserDetailsManager(
                User.withUsername(Long.toString(LocalIntegrationDemo.FAMILY))
                        .password(encoder.encode(family)).roles("PARENT").build(),
                User.withUsername(Long.toString(LocalIntegrationDemo.CAREGIVER))
                        .password(encoder.encode(caregiver)).roles("CAREGIVER").build(),
                User.withUsername(Long.toString(LocalIntegrationDemo.PSYCHOLOGIST))
                        .password(encoder.encode(psychologist)).roles("PSYCHOLOGIST").build());
    }
}
