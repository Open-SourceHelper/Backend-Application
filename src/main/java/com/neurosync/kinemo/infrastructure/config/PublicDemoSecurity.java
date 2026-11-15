package com.neurosync.kinemo.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Profile("public-demo")
class PublicDemoSecurity {
    @Bean
    SecurityFilterChain publicDemoSecurityChain(HttpSecurity http,
            @Value("${kinemo.public-demo.frontend-origin}") String origin) throws Exception {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(origin));
        cors.setAllowedMethods(List.of("GET"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/public-demo/**", cors);
        http.cors(options -> options.configurationSource(source))
                .sessionManagement(options -> options.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/public-demo/health", "/api/v1/public-demo/snapshot")
                        .permitAll()
                        .anyRequest().denyAll());
        return http.build();
    }
}
