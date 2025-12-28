package com.wa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupService {
    
    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    
    public byte[] createBackup() throws IOException, InterruptedException {
        // Extrair informações da URL do banco de dados
        // Formato: jdbc:postgresql://host:port/database
        String url = datasourceUrl.replace("jdbc:postgresql://", "");
        String[] parts = url.split("/");
        String hostAndPort = parts[0];
        String database = parts[1];
        
        String[] hostPortParts = hostAndPort.split(":");
        String host = hostPortParts[0];
        String port = hostPortParts.length > 1 ? hostPortParts[1] : "5432";
        
        // Criar nome do arquivo com timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = "backup_wa_db_" + timestamp + ".sql";
        
        // Criar diretório temporário se não existir
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
        Path backupFile = tempDir.resolve(backupFileName);
        
        // Construir comando pg_dump com formato SQL simples (texto)
        ProcessBuilder processBuilder = new ProcessBuilder(
            "pg_dump",
            "-h", host,
            "-p", port,
            "-U", datasourceUsername,
            "-d", database,
            "-F", "p",  // Formato SQL simples (texto)
            "-f", backupFile.toString()
        );
        
        // Configurar variável de ambiente para senha
        processBuilder.environment().put("PGPASSWORD", datasourcePassword);
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        
        // Ler output do processo (erros e warnings)
        StringBuilder errorOutput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                errorOutput.append(line).append("\n");
                System.out.println("pg_dump: " + line);
            }
        }
        
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new IOException("pg_dump falhou com código de saída: " + exitCode + 
                "\nErro: " + errorOutput.toString());
        }
        
        // Verificar se o arquivo foi criado
        if (!Files.exists(backupFile)) {
            throw new IOException("Arquivo de backup não foi criado: " + backupFile.toString());
        }
        
        // Ler o arquivo de backup
        byte[] backupData = Files.readAllBytes(backupFile);
        
        // Deletar arquivo temporário
        Files.deleteIfExists(backupFile);
        
        return backupData;
    }
}

