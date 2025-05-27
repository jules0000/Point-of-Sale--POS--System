package com.pos.utils;

import com.pos.db.DatabaseConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

// Requires JasperReports library (net.sf.jasperreports:jasperreports)
public class ReportGenerator {
    private static final String REPORTS_PATH = "/reports/";
    private static final DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    private static final ConfigLoader config = ConfigLoader.getInstance();

    public static void generateSalesReport(String startDate, String endDate) throws Exception {
        String reportTemplate = REPORTS_PATH + "sales_report.jrxml";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("START_DATE", startDate);
        parameters.put("END_DATE", endDate);
        parameters.put("COMPANY_NAME", config.getCompanyName());
        
        generateReport(reportTemplate, parameters);
    }

    public static void generateInventoryReport() throws Exception {
        String reportTemplate = REPORTS_PATH + "inventory_report.jrxml";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("LOW_STOCK_THRESHOLD", config.getLowStockThreshold());
        parameters.put("COMPANY_NAME", config.getCompanyName());
        
        generateReport(reportTemplate, parameters);
    }

    public static void generateDailySalesReport() throws Exception {
        String reportTemplate = REPORTS_PATH + "daily_sales_report.jrxml";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("COMPANY_NAME", config.getCompanyName());
        
        generateReport(reportTemplate, parameters);
    }

    public static void generateCashierReport(Long userId, String startDate, String endDate) throws Exception {
        String reportTemplate = REPORTS_PATH + "cashier_report.jrxml";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("USER_ID", userId);
        parameters.put("START_DATE", startDate);
        parameters.put("END_DATE", endDate);
        parameters.put("COMPANY_NAME", config.getCompanyName());
        
        generateReport(reportTemplate, parameters);
    }

    public static void generateProductReport(Long productId) throws Exception {
        String reportTemplate = REPORTS_PATH + "product_report.jrxml";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("PRODUCT_ID", productId);
        parameters.put("COMPANY_NAME", config.getCompanyName());
        
        generateReport(reportTemplate, parameters);
    }

    private static void generateReport(String templatePath, Map<String, Object> parameters) throws Exception {
        try (Connection conn = dbConnection.getConnection()) {
            // Load report template
            InputStream reportStream = ReportGenerator.class.getResourceAsStream(templatePath);
            if (reportStream == null) {
                throw new Exception("Report template not found: " + templatePath);
            }

            // Compile report
            JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            // Add common parameters
            parameters.put("REPORT_CONNECTION", conn);
            parameters.put("LOGO_PATH", REPORTS_PATH + "logo.png");

            // Fill report
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                parameters,
                conn
            );

            // Show report viewer
            JasperViewer.viewReport(jasperPrint, false);

            // Export to PDF (optional)
            String pdfPath = "reports/output/" + 
                           templatePath.substring(templatePath.lastIndexOf('/') + 1)
                                    .replace(".jrxml", ".pdf");
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);
        }
    }
} 