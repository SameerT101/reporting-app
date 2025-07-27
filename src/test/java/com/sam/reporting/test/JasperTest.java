package com.sam.reporting.test;

import net.sf.jasperreports.engine.*;

import java.io.ByteArrayInputStream;

public class JasperTest {

    public static void main(String[] args) {
        System.out.println("JasperReports Version: " + JasperCompileManager.class.getPackage().getImplementationVersion());

        String simpleXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "                <jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\" name=\"products\" pageWidth=\"595\" pageHeight=\"842\" columnWidth=\"555\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"20\" bottomMargin=\"20\">\n" +
                "                    <field name=\"name\" class=\"java.lang.String\"/>\n" +
                "                    <title>\n" +
                "                        <band height=\"50\">\n" +
                "                            <staticText>\n" +
                "                                <reportElement x=\"0\" y=\"0\" width=\"555\" height=\"30\"/>\n" +
                "                                <textElement textAlignment=\"Center\">\n" +
                "                                    <font size=\"20\"/>\n" +
                "                                </textElement>\n" +
                "                                <text><![CDATA[Products Report]]></text>\n" +
                "                            </staticText>\n" +
                "                        </band>\n" +
                "                    </title>\n" +
                "                    <detail>\n" +
                "                        <band height=\"20\">\n" +
                "                            <textField>\n" +
                "                                <reportElement x=\"0\" y=\"0\" width=\"555\" height=\"20\"/>\n" +
                "                                <textFieldExpression><![CDATA[$F{name}]]></textFieldExpression>\n" +
                "                            </textField>\n" +
                "                        </band>\n" +
                "                    </detail>\n" +
                "                </jasperReport>";

        try {
            System.out.println("Compiling report...\n" + simpleXml);
            JasperReport report = JasperCompileManager.compileReport(
                    new ByteArrayInputStream(simpleXml.getBytes())
            );
            System.out.println("SUCCESS! Report compiled: " + report.getName());

            // Try to generate empty report
            JasperPrint print = JasperFillManager.fillReport(report, null, new JREmptyDataSource());
            System.out.println("Report filled successfully");

            byte[] pdf = JasperExportManager.exportReportToPdf(print);
            System.out.println("PDF generated, size: " + pdf.length + " bytes");

        } catch (JRException e) {
            System.out.println("Error Message " + e.getMessage());
            e.printStackTrace();
            Throwable cause = e.getCause();
            while (cause != null) {
                System.err.println("Caused by: " + cause.getClass().getName() + " - " + cause.getMessage());
                cause = cause.getCause();
            }

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();

            Throwable cause = e.getCause();
            while (cause != null) {
                System.err.println("Caused by: " + cause.getClass().getName() + " - " + cause.getMessage());
                cause = cause.getCause();
            }
        }
    }
}