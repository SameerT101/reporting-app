package com.sam.reporting.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class HomeController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser principal, OAuth2AuthenticationToken authToken) {
        if (principal != null) {
            model.addAttribute("profile", principal.getClaims());
            model.addAttribute("username", principal.getName());
            model.addAttribute("email", principal.getEmail());
            model.addAttribute("authenticated", true);
            log.debug("User is authenticated");

        } else {
            model.addAttribute("authenticated", false);
            log.debug("User is unauthenticated");
        }
        return "index";
    }


    // For API endpoints - return JSON instead of HTML
    @GetMapping("/api/profile")
    @ResponseBody
    public UserProfile getProfile(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return new UserProfile("Anonymous", "Anonymous","",null,null);
        }
        return new UserProfile(
                jwt.getSubject(),
                jwt.getClaimAsString("name"),
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("picture"),
                jwt.getClaims()
        );
    }




    // Public endpoints - no authentication required
    @GetMapping("/api/public/health")
    @ResponseBody
    public HealthResponse getHealth() {
        return new HealthResponse("OK", "Application is running");
    }

    @GetMapping("/api/public/info")
    @ResponseBody
    public InfoResponse getInfo() {
        return new InfoResponse("Reporting API", "1.0.0");
    }

    // Response DTOs
    record UserProfile(String sub, String name, String email, String picture, Map<String, Object> claims) {}
    record HealthResponse(String status, String message) {}
    record InfoResponse(String name, String version) {}
}