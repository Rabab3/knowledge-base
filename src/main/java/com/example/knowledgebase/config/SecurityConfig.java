package com.example.knowledgebase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Configuration CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // si tu as besoin de cookies / auth headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ✅ Security Filter Chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        // Routes accessibles sans authentification
                        .requestMatchers("/api/auth/**").permitAll()

                        // Routes accessibles uniquement par des utilisateurs ayant le rôle "ADMIN"
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Routes accessibles par les rôles "MODERATEUR" ou "ADMIN"
                        .requestMatchers("/api/moderation/**").hasAnyRole("MODERATEUR", "ADMIN")

                        // Routes accessibles par les rôles "CONTRIBUTEUR", "MODERATEUR", ou "ADMIN"
                        .requestMatchers("/api/contribute/**").hasAnyRole("CONTRIBUTEUR", "MODERATEUR", "ADMIN")

                        // Routes accessibles par les rôles "LECTEUR", "CONTRIBUTEUR", "MODERATEUR", ou "ADMIN"
                        .requestMatchers("/api/view/**").hasAnyRole("LECTEUR", "CONTRIBUTEUR", "MODERATEUR", "ADMIN")

                        // Toute autre requête doit être authentifiée
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
