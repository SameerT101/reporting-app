package com.sam.reporting.service;

import com.sam.reporting.dto.ReportRequest;
import com.sam.reporting.jasper.JasperReportFactory;
import com.sam.reporting.model.Category;
import com.sam.reporting.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final CategoryRepository repo;
    private final JasperReportFactory jasper;   // was BIRT
    private final MailService mail;

    @Transactional(readOnly = true)
    public void generateAndMail(ReportRequest req) {
        List<Category> data = repo.findAllWithProducts();
        byte[] pdf = jasper.buildProductReport(data);
        mail.sendWithAttachment(
                req.getUserEmail(),
                "Your report",
                "Please find the report attached.",
                pdf,
                "products.pdf");
    }
}
