package com.sam.reporting.controller;

import com.sam.reporting.config.RabbitConfig;
import com.sam.reporting.dto.ReportRequest;
import com.sam.reporting.event.ReportRequestEvent;
import com.sam.reporting.jasper.JasperReportFactory;
import com.sam.reporting.model.Category;
import com.sam.reporting.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReportControllerTest {

    @Mock
    private AmqpTemplate amqpTemplate;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private JasperReportFactory reportFactory;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private OidcUser oidcUser;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Manually inject the mocked dependencies since @Autowired fields aren't handled by @InjectMocks
        ReflectionTestUtils.setField(reportController, "reportFactory", reportFactory);
        ReflectionTestUtils.setField(reportController, "categoryRepository", categoryRepository);
    }

    @Test
    public void testGetProductReport_Success() {
        // Given
        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setName("Electronics");
        categories.add(category);

        byte[] fakePdf = "fake-pdf-data".getBytes();

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn("test@example.com");
        when(categoryRepository.findAll()).thenReturn(categories);
        when(reportFactory.buildProductReport(categories)).thenReturn(fakePdf);

        // When
        ResponseEntity<byte[]> response = reportController.getProductReport(authentication);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertArrayEquals(fakePdf, response.getBody());
        verify(categoryRepository, times(1)).findAll();
        verify(reportFactory, times(1)).buildProductReport(categories);
    }

    @Test
    public void testGetProductReport_NoAuthentication() {
        // When
        ResponseEntity<byte[]> response = reportController.getProductReport(null);

        // Then
        assertEquals(401, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testSubmitEmailReport_Success() {
        // Given
        ReportRequest request = new ReportRequest();
        request.setUserEmail("test@example.com");

        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(oidcUser.getEmail()).thenReturn("test@example.com");

        // When
        ResponseEntity<?> response = reportController.submit(request, authentication);

        // Then
        assertEquals(202, response.getStatusCodeValue());
        verify(amqpTemplate, times(1)).convertAndSend(RabbitConfig.QUEUE, request);
        verify(eventPublisher, times(1)).publishEvent(any(ReportRequestEvent.class));
    }

    @Test
    public void testSubmitEmailReport_NoAuthentication() {
        // Given
        ReportRequest request = new ReportRequest();
        request.setUserEmail("test@example.com");

        // When
        ResponseEntity<?> response = reportController.submit(request, null);

        // Then
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Authentication required", response.getBody());
        verify(amqpTemplate, never()).convertAndSend(eq(RabbitConfig.QUEUE), any(ReportRequest.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
}