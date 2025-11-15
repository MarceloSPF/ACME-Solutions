package com.acme.workshop.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class ReportIndexGenerator {
    
    private static final String REPORT_DIR = "target/test-reports";
    private static final String INDEX_FILE = "target/test-reports/index.html";
    
    public static void generateIndex() {
        try {
            Path reportDir = Paths.get(REPORT_DIR);
            if (!Files.exists(reportDir)) {
                return;
            }
            
            StringBuilder html = new StringBuilder();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n<head>\n");
            html.append("<meta charset='UTF-8'>\n");
            html.append("<title>Índice de Relatórios de Teste</title>\n");
            html.append("<style>\n");
            html.append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
            html.append(".header { background-color: #2c3e50; color: white; padding: 20px; border-radius: 5px; margin-bottom: 20px; }\n");
            html.append(".report-list { background-color: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
            html.append(".report-item { padding: 15px; margin: 10px 0; border-left: 4px solid #3498db; background-color: #ecf0f1; border-radius: 4px; }\n");
            html.append(".report-item:hover { background-color: #d5dbdb; }\n");
            html.append("a { color: #2980b9; text-decoration: none; font-weight: bold; }\n");
            html.append("a:hover { text-decoration: underline; }\n");
            html.append(".timestamp { color: #7f8c8d; font-size: 0.9em; margin-top: 5px; }\n");
            html.append("</style>\n");
            html.append("</head>\n<body>\n");
            
            html.append("<div class='header'>\n");
            html.append("<h1>📊 Índice de Relatórios de Teste</h1>\n");
            html.append("<p class='timestamp'>Gerado em: ").append(LocalDateTime.now().format(formatter)).append("</p>\n");
            html.append("</div>\n");
            
            html.append("<div class='report-list'>\n");
            html.append("<h2>Relatórios Disponíveis</h2>\n");
            
            try (Stream<Path> paths = Files.list(reportDir)) {
                paths.filter(path -> path.toString().endsWith("-report.html"))
                     .sorted()
                     .forEach(path -> {
                         String fileName = path.getFileName().toString();
                         String testClass = fileName.replace("-report.html", "");
                         
                         html.append("<div class='report-item'>\n");
                         html.append("<a href='").append(fileName).append("'>")
                              .append(testClass).append("</a>\n");
                         html.append("<p class='timestamp'>Arquivo: ").append(fileName).append("</p>\n");
                         html.append("</div>\n");
                     });
            }
            
            html.append("</div>\n");
            html.append("</body>\n</html>");
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(INDEX_FILE))) {
                writer.println(html.toString());
            }
            
            System.out.println("Índice de relatórios gerado: " + INDEX_FILE);
            
        } catch (IOException e) {
            System.err.println("Erro ao gerar índice: " + e.getMessage());
        }
    }
}

