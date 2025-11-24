package com.acme.workshop.util;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestReportGenerator implements TestWatcher, AfterAllCallback {
    
    private static final String REPORT_DIR = "target/test-reports";
    private static final ConcurrentMap<String, List<TestResult>> testResultsMap = new ConcurrentHashMap<>();
    
    static class TestResult {
        String testClass;
        String testMethod;
        String status;
        String message;
        LocalDateTime timestamp;
        
        TestResult(String testClass, String testMethod, String status, String message) {
            this.testClass = testClass;
            this.testMethod = testMethod;
            this.status = status;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }
    }
    
    @Override
    public void testSuccessful(ExtensionContext context) {
        recordTestResult(context, "SUCCESS", "Teste executado com sucesso");
    }
    
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        recordTestResult(context, "FAILED", cause.getMessage());
    }
    
    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        recordTestResult(context, "ABORTED", cause.getMessage());
    }
    
    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        recordTestResult(context, "DISABLED", reason.orElse("Teste desabilitado"));
    }
    
    private void recordTestResult(ExtensionContext context, String status, String message) {
        String testClass = context.getRequiredTestClass().getSimpleName();
        String testMethod = context.getRequiredTestMethod().getName();
        
        testResultsMap.computeIfAbsent(testClass, k -> new ArrayList<>())
                     .add(new TestResult(testClass, testMethod, status, message));
    }
    
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // Gera relatório após todos os testes da classe
        String testClass = context.getRequiredTestClass().getSimpleName();
        List<TestResult> results = testResultsMap.get(testClass);
        if (results != null && !results.isEmpty()) {
            generateReportForClass(testClass, results);
            testResultsMap.remove(testClass);
        }
    }
    
    private void generateReportForClass(String testClass, List<TestResult> results) {
        try {
            // Criar diretório de relatórios
            Path reportDir = Paths.get(REPORT_DIR);
            if (!Files.exists(reportDir)) {
                Files.createDirectories(reportDir);
            }
            
            if (results.isEmpty()) {
                return;
            }
            
            // Gerar relatório HTML
            String htmlReport = generateHTMLReport(testClass, results);
            String fileName = String.format("%s/%s-report.html", REPORT_DIR, testClass);
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println(htmlReport);
            }
            
            // Gerar relatório texto
            String textReport = generateTextReport(testClass, results);
            String textFileName = String.format("%s/%s-report.txt", REPORT_DIR, testClass);
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(textFileName))) {
                writer.println(textReport);
            }
            
            System.out.println("Relatório gerado: " + fileName);
            System.out.println("Relatório texto gerado: " + textFileName);
            
            // Gerar índice de relatórios
            ReportIndexGenerator.generateIndex();
            
        } catch (IOException e) {
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String generateHTMLReport(String testClass, List<TestResult> results) {
        StringBuilder html = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        int total = results.size();
        long success = results.stream().filter(r -> r.status.equals("SUCCESS")).count();
        long failed = results.stream().filter(r -> r.status.equals("FAILED")).count();
        long aborted = results.stream().filter(r -> r.status.equals("ABORTED")).count();
        long disabled = results.stream().filter(r -> r.status.equals("DISABLED")).count();
        
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>Relatório de Testes - ").append(testClass).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
        html.append(".header { background-color: #2c3e50; color: white; padding: 20px; border-radius: 5px; }\n");
        html.append(".summary { background-color: white; padding: 20px; margin: 20px 0; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
        html.append(".summary-item { display: inline-block; margin: 10px 20px; padding: 10px 20px; border-radius: 5px; }\n");
        html.append(".success { background-color: #27ae60; color: white; }\n");
        html.append(".failed { background-color: #e74c3c; color: white; }\n");
        html.append(".aborted { background-color: #f39c12; color: white; }\n");
        html.append(".disabled { background-color: #95a5a6; color: white; }\n");
        html.append(".total { background-color: #3498db; color: white; }\n");
        html.append("table { width: 100%; border-collapse: collapse; background-color: white; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
        html.append("th { background-color: #34495e; color: white; padding: 12px; text-align: left; }\n");
        html.append("td { padding: 10px; border-bottom: 1px solid #ddd; }\n");
        html.append("tr:hover { background-color: #f5f5f5; }\n");
        html.append(".status-success { color: #27ae60; font-weight: bold; }\n");
        html.append(".status-failed { color: #e74c3c; font-weight: bold; }\n");
        html.append(".status-aborted { color: #f39c12; font-weight: bold; }\n");
        html.append(".status-disabled { color: #95a5a6; font-weight: bold; }\n");
        html.append(".timestamp { color: #7f8c8d; font-size: 0.9em; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        
        html.append("<div class='header'>\n");
        html.append("<h1>Relatório de Testes</h1>\n");
        html.append("<h2>").append(testClass).append("</h2>\n");
        html.append("<p class='timestamp'>Gerado em: ").append(LocalDateTime.now().format(formatter)).append("</p>\n");
        html.append("</div>\n");
        
        html.append("<div class='summary'>\n");
        html.append("<h3>Resumo</h3>\n");
        html.append("<div class='summary-item total'>Total: ").append(total).append("</div>\n");
        html.append("<div class='summary-item success'>Sucesso: ").append(success).append("</div>\n");
        html.append("<div class='summary-item failed'>Falhou: ").append(failed).append("</div>\n");
        html.append("<div class='summary-item aborted'>Abortado: ").append(aborted).append("</div>\n");
        html.append("<div class='summary-item disabled'>Desabilitado: ").append(disabled).append("</div>\n");
        html.append("</div>\n");
        
        html.append("<table>\n");
        html.append("<thead>\n");
        html.append("<tr>\n");
        html.append("<th>Método de Teste</th>\n");
        html.append("<th>Status</th>\n");
        html.append("<th>Mensagem</th>\n");
        html.append("<th>Data/Hora</th>\n");
        html.append("</tr>\n");
        html.append("</thead>\n");
        html.append("<tbody>\n");
        
        for (TestResult result : results) {
            html.append("<tr>\n");
            html.append("<td>").append(result.testMethod).append("</td>\n");
            html.append("<td><span class='status-").append(result.status.toLowerCase()).append("'>")
                 .append(result.status).append("</span></td>\n");
            html.append("<td>").append(escapeHtml(result.message)).append("</td>\n");
            html.append("<td class='timestamp'>").append(result.timestamp.format(formatter)).append("</td>\n");
            html.append("</tr>\n");
        }
        
        html.append("</tbody>\n");
        html.append("</table>\n");
        html.append("</body>\n</html>");
        
        return html.toString();
    }
    
    private String generateTextReport(String testClass, List<TestResult> results) {
        StringBuilder text = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        int total = results.size();
        long success = results.stream().filter(r -> r.status.equals("SUCCESS")).count();
        long failed = results.stream().filter(r -> r.status.equals("FAILED")).count();
        long aborted = results.stream().filter(r -> r.status.equals("ABORTED")).count();
        long disabled = results.stream().filter(r -> r.status.equals("DISABLED")).count();
        
        text.append("=".repeat(80)).append("\n");
        text.append("RELATÓRIO DE TESTES\n");
        text.append("=".repeat(80)).append("\n");
        text.append("Classe: ").append(testClass).append("\n");
        text.append("Gerado em: ").append(LocalDateTime.now().format(formatter)).append("\n");
        text.append("-".repeat(80)).append("\n");
        text.append("RESUMO\n");
        text.append("-".repeat(80)).append("\n");
        text.append(String.format("Total de Testes: %d\n", total));
        text.append(String.format("Sucesso:         %d\n", success));
        text.append(String.format("Falhou:          %d\n", failed));
        text.append(String.format("Abortado:        %d\n", aborted));
        text.append(String.format("Desabilitado:    %d\n", disabled));
        text.append("-".repeat(80)).append("\n");
        text.append("DETALHES DOS TESTES\n");
        text.append("-".repeat(80)).append("\n");
        
        for (TestResult result : results) {
            text.append(String.format("Método: %s\n", result.testMethod));
            text.append(String.format("Status: %s\n", result.status));
            text.append(String.format("Mensagem: %s\n", result.message));
            text.append(String.format("Data/Hora: %s\n", result.timestamp.format(formatter)));
            text.append("-".repeat(80)).append("\n");
        }
        
        return text.toString();
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}

