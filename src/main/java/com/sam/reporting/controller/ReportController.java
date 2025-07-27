package com.sam.reporting.controller;

import com.sam.reporting.config.RabbitConfig;
import com.sam.reporting.dto.ReportRequest;
import com.sam.reporting.event.ReportRequestEvent;
import com.sam.reporting.jasper.JasperReportFactory;
import com.sam.reporting.model.Category;
import com.sam.reporting.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final AmqpTemplate amqp;
    private final ApplicationEventPublisher publisher;
    @Autowired
    private JasperReportFactory reportFactory;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getProductReport(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String userInfo = extractUserInfo(authentication);
        log.info("Generating PDF for user: {}", userInfo);

        List<Category> categories = categoryRepository.findAll();
        byte[] pdfBytes = reportFactory.buildProductReport(categories);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "products-report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/email")
    public ResponseEntity<?> submit(@RequestBody ReportRequest reportRequest, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication required");
        }

        String userInfo = extractUserInfo(authentication);
        log.info("Email report requested by: {}", userInfo);

        amqp.convertAndSend(RabbitConfig.QUEUE, reportRequest);
        publisher.publishEvent(new ReportRequestEvent(this, reportRequest.getUserEmail()));
        return ResponseEntity.accepted().build();
    }

    /**
     * Extract user information from different authentication types
     */
    private String extractUserInfo(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            // Session-based authentication
            return oidcUser.getEmail() != null ? oidcUser.getEmail() : oidcUser.getSubject();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            // JWT Bearer token authentication
            String email = jwt.getClaimAsString("email");
            if (email != null) {
                return email;
            }
            String subject = jwt.getSubject();
            return subject != null ? subject : "unknown-jwt-user";
        } else {
            // Fallback
            return authentication.getName();
        }
    }
}