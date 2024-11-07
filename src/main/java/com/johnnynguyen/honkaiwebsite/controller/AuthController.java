package com.johnnynguyen.honkaiwebsite.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication;
            CsrfToken csrf = null;

            if (loginRequest != null) {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(),
                                loginRequest.getPassword())
                );
            }
            else {
                authentication = SecurityContextHolder.getContext().getAuthentication();
                csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            }

            // Get the CsrfToken from the request attribute
            // TODO: Figure out how to proceed with CRSFToken on the frontend side.
            if (csrf != null) {
                System.out.println("CSRF Token: " + csrf.getToken());
                System.out.println("Header Name: " + csrf.getHeaderName());
                System.out.println("Parameter Name: " + csrf.getParameterName());
            }

            if (authentication != null && authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful",
                        "username", authentication.getName(),
                        "roles", authentication.getAuthorities(),
                        "csrfToken", csrf != null ? csrf.getToken() : "No token"));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Authentication failed"));
        }
        catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid username or password"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of(
                "message", "Logout successful"
        ));
    }

    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(Map.of(
                    "message", "You are authenticated",
                    "username", authentication.getName(),
                    "roles", authentication.getAuthorities()
            ));
        }
        return ResponseEntity.ok(Map.of(
                "message", "Not authenticated"
        ));
    }

    static class LoginRequest {
        private String username;
        private String password;
        private boolean rememberMe;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public boolean isRememberMe() { return rememberMe; }
        public void setRememberMe(boolean rememberMe) { this.rememberMe = rememberMe; }
    }
}


