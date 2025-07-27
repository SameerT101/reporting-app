package com.sam.reporting.jasper;

import com.sam.reporting.dto.ReportProductRow;
import com.sam.reporting.model.Category;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                System.err.println("ERROR: Resource not found at " + resourcePath);
                System.err.println("Classpath: " + System.getProperty("java.class.path"));
                throw new IllegalStateException("Template file not found at: " + resourcePath);
            }

            log.info("Resource found, input stream: {}", in);

            // Try to compile
            log.info("Attempting to compile report...");
            this.template = JasperCompileManager.compileReport(in);
            log.info("SUCCESS: Report compiled successfully!");

        } catch (JRException ex) {
            System.err.println("ERROR: JasperReports Exception");
            System.err.println("Message: " + ex.getMessage());
            ex.printStackTrace();
            throw new IllegalStateException("Cannot compile Jasper template: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            System.err.println("ERROR: General Exception");
            System.err.println("Type: " + ex.getClass().getName());
            System.err.println("Message: " + ex.getMessage());
            ex.printStackTrace();
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
            params.put("productDataset", ds);
            log.info("Filling report...");
            JasperPrint print = JasperFillManager.fillReport(template, params, new JREmptyDataSource());

            log.info("Exporting to PDF...");
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            log.info("PDF generated, size: {} bytes", pdf.length);
            log.info("=== buildProductReport END ===");

            return pdf;

        } catch (JRException ex) {
            System.err.println("ERROR in buildProductReport: " + ex.getMessage());
            ex.printStackTrace();
            throw new IllegalStateException("Jasper generation failed", ex);
        }
    }


}