package com.sam.reporting.service;

import com.sam.reporting.dto.ReportRequest;
import com.sam.reporting.jasper.JasperReportFactory;
import com.sam.reporting.model.Category;
import com.sam.reporting.model.Product;
import com.sam.reporting.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReportServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private JasperReportFactory jasperReportFactory;

    @Mock
    private MailService mailService;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateAndMail_Success() {
        // Given
        ReportRequest request = new ReportRequest();
        request.setUserEmail("test@example.com");

        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new java.math.BigDecimal("999.99"));
        product.setCategory(category);

        List<Product> products = new ArrayList<>();
        products.add(product);
        category.setProducts(products);
        categories.add(category);

        byte[] fakePdf = "fake-pdf-content".getBytes();

        // When
        when(categoryRepository.findAllWithProducts()).thenReturn(categories);
        when(jasperReportFactory.buildProductReport(categories)).thenReturn(fakePdf);

        // Then
        reportService.generateAndMail(request);

        // Verify
        verify(categoryRepository, times(1)).findAllWithProducts();
        verify(jasperReportFactory, times(1)).buildProductReport(categories);
        verify(mailService, times(1)).sendWithAttachment(
                eq("test@example.com"),
                eq("Your report"),
                eq("Please find the report attached."),
                eq(fakePdf),
                eq("products.pdf")
        );
    }

    @Test
    public void testGenerateAndMail_EmptyCategories() {
        // Given
        ReportRequest request = new ReportRequest();
        request.setUserEmail("test@example.com");

        List<Category> emptyCategories = new ArrayList<>();
        byte[] fakePdf = "empty-report".getBytes();

        // When
        when(categoryRepository.findAllWithProducts()).thenReturn(emptyCategories);
        when(jasperReportFactory.buildProductReport(emptyCategories)).thenReturn(fakePdf);

        // Then
        reportService.generateAndMail(request);

        // Verify
        verify(categoryRepository, times(1)).findAllWithProducts();
        verify(jasperReportFactory, times(1)).buildProductReport(emptyCategories);
        verify(mailService, times(1)).sendWithAttachment(
                anyString(), anyString(), anyString(), any(byte[].class), anyString()
        );
    }
}