package com.johnnynguyen.honkaiwebsite.config;

import com.johnnynguyen.honkaiwebsite.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.function.Supplier;

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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Authenticate and authorize specific API usages to users.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())   // Enable protection against CSRF.
                .ignoringRequestMatchers("/api/users/createUser", "/api/auth/login"))   // Set API points allowing usage for non-user.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)   // Allow a maximum session of one, with the same user on different devices.
                .maxSessionsPreventsLogin(true)
                .expiredUrl("/"))   // True or false functions differently. Essentially, allows multiple device but the previous device session is invalid.
//            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                .requestMatchers("/api/users/createUser", "/api/auth/**").permitAll()
//                .requestMatchers("/api/files/{fileName:.+}", "/api/users/{userId}/profile-pic").hasAnyRole("ADMIN", "CONSUMER")
//                .requestMatchers("/api/posts/**", "/api/follow/**", "/api/likes/**", "/api/comments/**").hasAnyRole("ADMIN", "CONSUMER")
//                .requestMatchers("/api/users/createAdmin","/api/users/{userId}/roles", "/api/users/{userId}/roles/{role}").hasRole("ADMIN")
//                .requestMatchers("/api/users/createAdmin","/api/users/{userId}/roles", "/api/users/{userId}/roles/{role}").hasAuthority("ROLE_ADMIN")
//                .requestMatchers(HttpMethod.PUT, "/api/users/{userId}").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}").hasRole("ADMIN")
//                .requestMatchers("/api/posts").hasRole("ADMIN")
//                .requestMatchers("/api/users/**", "/api/files/**", "/api/posts/**", "/api/follow/**").authenticated()
//                .anyRequest().permitAll())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/users/createUser", "/api/auth/**").permitAll()
                        // Admin only endpoints
                        .requestMatchers("/api/users/createAdmin").hasRole("ADMIN")
                        .requestMatchers("/api/users/{userId}/roles").hasRole("ADMIN")
                        .requestMatchers("/api/users/{userId}/roles/{role}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/{userId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}").hasRole("ADMIN")
                        // Both admin and consumer endpoints
                        .requestMatchers("/api/files/{fileName:.+}", "/api/users/{userId}/profile-pic").hasAnyRole("ADMIN", "CONSUMER")
                        .requestMatchers("/api/posts/**", "/api/follow/**", "/api/likes/**", "/api/comments/**").hasAnyRole("ADMIN", "CONSUMER")
                        .requestMatchers("/api/users/**", "/api/files/**", "/api/posts/**", "/api/follow/**").authenticated()
                        .anyRequest().permitAll())
            .rememberMe(remember -> remember
                .key("temporarykey")   // Hard-coded security key, changes normally in production.
                .tokenValiditySeconds(86400)   // With "remember me" on, session last 24 hours. Default is 30 mins.
                .rememberMeParameter("remember-me")
                .useSecureCookie(true))
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .deleteCookies("JSESSIONID", "remember-me")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll())
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // Allows cross-origin, which communicate with other framework of frontend by the given permissions.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001")); // Add your React app's URL.
        //config.addAllowedOrigin("http://localhost:3001");  // Set it individually.
        config.setAllowedHeaders(List.of("Content-Type", "Authorization", "Accept", "X-XSRF-TOKEN"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setExposedHeaders(List.of("X-XSRF-TOKEN"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
