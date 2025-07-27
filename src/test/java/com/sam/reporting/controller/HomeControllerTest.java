package com.sam.reporting.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HomeControllerTest {

    @Mock
    private OAuth2AuthorizedClientService authorizedClientService;
    
    @Mock
    private Model model;
    
    @Mock
    private OidcUser oidcUser;
    
    @Mock
    private Jwt jwt;
    
    @Mock
    private OAuth2AuthenticationToken authToken;
    
    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHome_WithAuthenticatedUser() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("email", "test@example.com");
        
        when(oidcUser.getClaims()).thenReturn(claims);
        when(oidcUser.getName()).thenReturn("test@example.com");
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(authToken.getAuthorizedClientRegistrationId()).thenReturn("auth0");
        when(authToken.getName()).thenReturn("test@example.com");
        
        // When
        String result = homeController.home(model, oidcUser, authToken);
        
        // Then
        assertEquals("index", result);
        verify(model).addAttribute("profile", claims);
        verify(model).addAttribute("username", "test@example.com");
        verify(model).addAttribute("email", "test@example.com");
        verify(model).addAttribute("authenticated", true);
    }

    @Test
    public void testHome_WithoutAuthentication() {
        // When
        String result = homeController.home(model, null, null);
        
        // Then
        assertEquals("index", result);
        verify(model).addAttribute("authenticated", false);
    }

    @Test
    public void testGetProfile_WithJwt() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user123");
        claims.put("name", "Test User");
        claims.put("email", "test@example.com");
        claims.put("picture", "http://example.com/picture.jpg");
        
        when(jwt.getSubject()).thenReturn("user123");
        when(jwt.getClaimAsString("name")).thenReturn("Test User");
        when(jwt.getClaimAsString("email")).thenReturn("test@example.com");
        when(jwt.getClaimAsString("picture")).thenReturn("http://example.com/picture.jpg");
        when(jwt.getClaims()).thenReturn(claims);
        
        // When
        HomeController.UserProfile profile = homeController.getProfile(jwt);
        
        // Then
        assertNotNull(profile);
        assertEquals("user123", profile.sub());
        assertEquals("Test User", profile.name());
        assertEquals("test@example.com", profile.email());
        assertEquals("http://example.com/picture.jpg", profile.picture());
        assertEquals(claims, profile.claims());
    }

    @Test
    public void testGetProfile_WithoutJwt() {
        // When
        HomeController.UserProfile profile = homeController.getProfile(null);
        
        // Then
        assertNotNull(profile);
        assertEquals("Anonymous", profile.sub());
        assertEquals("Anonymous", profile.name());
        assertEquals("", profile.email());
        assertNull(profile.picture());
        assertNull(profile.claims());
    }

    @Test
    public void testGetHealth() {
        // When
        HomeController.HealthResponse health = homeController.getHealth();
        
        // Then
        assertNotNull(health);
        assertEquals("OK", health.status());
        assertEquals("Application is running", health.message());
    }

    @Test
    public void testGetInfo() {
        // When
        HomeController.InfoResponse info = homeController.getInfo();
        
        // Then
        assertNotNull(info);
        assertEquals("Reporting API", info.name());
        assertEquals("1.0.0", info.version());
    }
}