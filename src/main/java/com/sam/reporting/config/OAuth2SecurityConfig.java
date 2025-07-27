package com.sam.reporting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(OAuth2SecurityConfig.class);

    @Value("${auth0.issuer}")
    private String issuer;

    @Value("${auth0.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.provider.auth0.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API testing
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/public/**", "/health", "/error").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/debug").permitAll() // Allow debug endpoint
                        .requestMatchers("/api/auth/**").permitAll() // Broader matcher - allow all auth endpoints
                        .anyRequest().authenticated()  // Everything else requires authentication
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/?error=login_failed")
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwkSetUri(jwkSetUri)
                        )
                )
                .logout(logout -> logout
                        .addLogoutHandler(logoutHandler())
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }


    private LogoutHandler logoutHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            try {
                String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                // Extract domain from issuer URL
                String domain = issuer.replace("https://", "").replace("/", "");
                String logoutUrl = "https://" + domain + "/v2/logout?client_id=" + clientId + "&returnTo=" + baseUrl;
                log.info("Logout URL: {}", logoutUrl);
                response.sendRedirect(logoutUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}