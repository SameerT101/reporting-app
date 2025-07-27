package com.sam.reporting.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Value("${auth0.domain}")
    private String auth0Domain;

    @Value("${auth0.client-id}")
    private String clientId;

    @Value("${auth0.client-secret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;


    /**
     *  API Login using client credentials (for service-to-service authentication)
     */
    @PostMapping("/api-login-client")
    public ResponseEntity<ApiLoginResponse> apiLoginClient() {
        log.info("Client credentials login attempt");

        try {
            RestTemplate restTemplate = new RestTemplate();
            String tokenUrl = String.format("https://%s/oauth/token", auth0Domain);

            // Auth0 expects form-encoded data, not JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("grant_type", "client_credentials");
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("audience", audience);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            log.info("Making client credentials request to Auth0:");
            log.info("URL: {}", tokenUrl);
            log.info("Client ID: {}", clientId);
            log.info("Audience: {}", audience);
            log.info("Client Secret: {}", clientSecret != null ? "***" + clientSecret.substring(Math.max(0, clientSecret.length() - 4)) : "null");

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                String accessToken = (String) responseBody.get("access_token");
                String tokenType = (String) responseBody.get("token_type");
                Object expiresIn = responseBody.get("expires_in");

                log.info("Client credentials login successful. Token length: {}, Type: {}",
                        accessToken.length(), tokenType);
                log.info("Token starts with: {}", accessToken.substring(0, Math.min(50, accessToken.length())));

                Long expiresInLong = expiresIn instanceof Number ? ((Number) expiresIn).longValue() : null;

                return ResponseEntity.ok(new ApiLoginResponse(
                        true,
                        "Client credentials authentication successful",
                        accessToken,
                        tokenType != null ? tokenType : "Bearer",
                        expiresInLong,
                        clientId + "@clients" // Standard format for client credentials
                ));

            } else {
                log.error("Client credentials login failed with status: {}", response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode())
                        .body(new ApiLoginResponse(false, "Client authentication failed", null, null, null, null));
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("Client credentials login failed - HTTP {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(401)
                    .body(new ApiLoginResponse(false, "Client authentication failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), null, null, null, null));
        } catch (Exception e) {
            log.error("Client credentials login failed: {}", e.getMessage());
            return ResponseEntity.status(401)
                    .body(new ApiLoginResponse(false, "Client authentication failed: " + e.getMessage(), null, null, null, null));
        }
    }


    /**
     * Logout endpoint - supports both GET and POST
     * POST version revokes token at Auth0 and returns logout URL
     */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logoutPost(@AuthenticationPrincipal Jwt jwt,
                                                     HttpServletRequest request,
                                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        return performLogout(jwt, request, authHeader, true);
    }

    /**
     * Logout endpoint - GET version (simpler, no token revocation)
     */
    @GetMapping("/logout")
    public ResponseEntity<LogoutResponse> logoutGet(@AuthenticationPrincipal Jwt jwt,
                                                    HttpServletRequest request) {
        return performLogout(jwt, request, null, false);
    }

    /**
     * Common logout logic - enhanced with token type detection
     */
    private ResponseEntity<LogoutResponse> performLogout(Jwt jwt, HttpServletRequest request,
                                                         String authHeader, boolean revokeToken) {
        if (jwt == null) {
            return ResponseEntity.badRequest()
                    .body(new LogoutResponse(false, "No active session", null, null));
        }

        try {
            boolean revoked = false;
            String revocationMessage = "No revocation attempted";

            // Only revoke token if requested and auth header is available
            if (revokeToken && authHeader != null) {
                String token = authHeader.replace("Bearer ", "");

                // Check token type
                String tokenType = determineTokenType(jwt);

                if ("client_credentials".equals(tokenType)) {
                    log.info("Client credentials token detected - revocation not supported");
                    revocationMessage = "Client credentials tokens cannot be revoked";
                } else {
                    revoked = revokeTokenAtAuth0(token);
                    revocationMessage = revoked ? "Token revoked successfully" : "Token revocation failed";
                }
            }

            String returnTo = getBaseUrl(request);
            String logoutUrl = String.format("https://%s/v2/logout?client_id=%s&returnTo=%s",
                    auth0Domain, clientId, returnTo);

            log.info("User {} initiated logout - {}", jwt.getSubject(), revocationMessage);

            String message = revokeToken ?
                    String.format("Logout successful - %s", revocationMessage) :
                    "Logout URL generated";

            return ResponseEntity.ok(new LogoutResponse(true, message, logoutUrl, jwt.getSubject()));

        } catch (Exception e) {
            log.error("Error during logout for user {}: {}", jwt.getSubject(), e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(new LogoutResponse(false, "Logout failed: " + e.getMessage(), null, jwt.getSubject()));
        }
    }


    /**
     * Check if current session is valid
     */
    @GetMapping("/session/check")
    public SessionResponse checkSession(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return new SessionResponse(false, "No active session", null, null);
        }

        // Check if token is expired
        if (jwt.getExpiresAt() != null && jwt.getExpiresAt().isBefore(java.time.Instant.now())) {
            return new SessionResponse(false, "Session expired", jwt.getSubject(), jwt.getExpiresAt());
        }

        return new SessionResponse(true, "Session active", jwt.getSubject(), jwt.getExpiresAt());
    }


    /**
     * Determine the type of token based on claims
     */
    private String determineTokenType(Jwt jwt) {
        // Client credentials tokens typically have:
        // - sub ending with @clients
        // - no 'name' or 'email' claims
        // - gty claim set to 'client-credentials'

        String subject = jwt.getSubject();
        String grantType = jwt.getClaimAsString("gty");

        if (subject != null && subject.endsWith("@clients")) {
            return "client_credentials";
        }

        if ("client-credentials".equals(grantType)) {
            return "client_credentials";
        }

        // Check if it has typical user claims
        if (jwt.getClaimAsString("name") == null && jwt.getClaimAsString("email") == null) {
            return "client_credentials";
        }

        return "authorization_code"; // Assume it's a user token
    }

    /**
     * Revoke token at Auth0
     */
    private boolean revokeTokenAtAuth0(String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String revokeUrl = String.format("https://%s/oauth/revoke", auth0Domain);

            Map<String, String> request = Map.of(
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "token", token
            );

            ResponseEntity<String> response = restTemplate.postForEntity(revokeUrl, request, String.class);
            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            log.error("Failed to revoke token at Auth0: {}", e.getMessage());
            return false;
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() +
                (request.getServerPort() != 80 && request.getServerPort() != 443 ?
                        ":" + request.getServerPort() : "");
    }

    // Response DTOs
    record LogoutResponse(boolean success, String message, String logoutUrl, String userId) {
    }

    record SessionResponse(boolean active, String message, String userId, Object expiresAt) {
    }

    record ApiLoginResponse(boolean success, String message, String accessToken, String tokenType, Long expiresIn,
                            String userId) {
    }
}