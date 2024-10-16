package com.johnnynguyen.honkaiwebsite.config;

import com.johnnynguyen.honkaiwebsite.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    // Password encoder.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authenticate and authorize specific API usages to users.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/api/users/createUser").permitAll()
                .requestMatchers("/api/files/{fileName:.+}", "/api/users/{userId}/profile-pic").hasAnyRole("ADMIN", "CONSUMER")
                .requestMatchers("/api/posts/**").hasAnyRole("ADMIN", "CONSUMER")
                .requestMatchers("/api/users/createAdmin","/api/users/{userId}/roles", "/api/users/{userId}/roles/{role}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/users/{userId}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}").hasRole("ADMIN")
                .requestMatchers("/api/posts").hasRole("ADMIN")
                .requestMatchers("/api/users/**", "/api/files/**", "/api/posts/**").authenticated()
                .anyRequest().permitAll()
                )
                // Enable Basic Auth.
                // TODO: Changes to JWT or OAuth 2.0.
                .httpBasic(Customizer.withDefaults())
                // Authorize roles to users.
                .userDetailsService(userDetailsService);
        return http.build();
    }

    // Allows cross-origin, which communicate with other framework of frontend by the given permissions.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000"); // Add your React app's URL
        config.setAllowedHeaders(List.of("Content-Type", "Authorization", "Accept"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
