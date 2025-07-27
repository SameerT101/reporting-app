package com.sam.reporting.jasper;

import com.sam.reporting.dto.ReportProductRow;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Component;
import com.sam.reporting.model.Category;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JasperReportFactory {
    private static final Logger log = LoggerFactory.getLogger(JasperReportFactory.class);

    private JasperReport template;

    public JasperReportFactory() {
        log.info("=== JasperReportFactory Constructor START ===");

        try {
            // Debug: Check if resource exists
            String resourcePath = "/templates/products.jrxml";
            log.info("Looking for resource at: {}", resourcePath);

            InputStream in = getClass().getResourceAsStream(resourcePath);

            if (in == null) {
                log.error("ERROR: Resource not found at " + resourcePath);
                log.error("Classpath: " + System.getProperty("java.class.path"));
                throw new IllegalStateException("Template file not found at: " + resourcePath);
            }

            log.info("Resource found, input stream: {}", in);

            // Try to compile
            log.info("Attempting to compile report...");
            this.template = JasperCompileManager.compileReport(in);
            log.info("SUCCESS: Report compiled successfully!");

        } catch (JRException ex) {
            log.error("ERROR: JasperReports Exception");
            log.error("Message: " + ex.getMessage());
            throw new IllegalStateException("Cannot compile Jasper template: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("ERROR: General Exception");
            log.error("Type: " + ex.getClass().getName());
            log.error("Message: " + ex.getMessage());
            throw new IllegalStateException("Cannot load Jasper template: " + ex.getMessage(), ex);
        }

        log.info("=== JasperReportFactory Constructor END ===");
    }

    public byte[] buildProductReport(List<Category> cats) {
        log.info("=== buildProductReport START ===");
        log.info("Number of categories: {}", cats.size());

        try {
            List<ReportProductRow> rows =
                    cats.stream()
                            .flatMap(cat -> cat.getProducts().stream()
                                    .map(p -> new ReportProductRow(cat.getName(),
                                            p.getName(),
                                            p.getPrice().doubleValue())))
                            .toList();   // Java 21

            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(rows);

            // No parameters for now
            Map<String, Object> params = new HashMap<>();
            params.put("productDataset",ds);
            log.info("Filling report...");
            JasperPrint print = JasperFillManager.fillReport(template, params, new JREmptyDataSource());

            log.info("Exporting to PDF...");
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            log.info("PDF generated, size: {} bytes", pdf.length);
            log.info("=== buildProductReport END ===");

            return pdf;

        } catch (JRException ex) {
            log.error("ERROR in buildProductReport: " + ex.getMessage());
            throw new IllegalStateException("Jasper generation failed", ex);
        }
    }


}