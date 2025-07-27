package com.sam.reporting.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;
    
    @Mock
    private MimeMessage mimeMessage;
    
    @InjectMocks
    private MailService mailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendPlain_Success() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        
        // When
        mailService.sendPlain(to, subject, body);
        
        // Then
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendWithAttachment_Success() throws MessagingException {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        byte[] attachment = "test-attachment".getBytes();
        String filename = "test.txt";
        
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When
        mailService.sendWithAttachment(to, subject, body, attachment, filename);
        
        // Then
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mimeMessage);
    }

    @Test
    public void testSendWithAttachment_RuntimeException() {
        // Given
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        byte[] attachment = "test-attachment".getBytes();
        String filename = "test.txt";
        
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Test exception")).when(javaMailSender).send(mimeMessage);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
            mailService.sendWithAttachment(to, subject, body, attachment, filename)
        );
        
        verify(javaMailSender, times(1)).createMimeMessage();
        verify(javaMailSender, times(1)).send(mimeMessage);
    }
}