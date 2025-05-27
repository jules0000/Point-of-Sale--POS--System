package com.pos.utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.pos.utils.ConfigLoader;

public class BackupManager {
    private static final ConfigLoader config = ConfigLoader.getInstance();
    private static final String BACKUP_PATH = config.getBackupPath();
    private static final int RETENTION_DAYS = 30;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static void createBackup() throws IOException, InterruptedException {
        // Ensure backup directory exists
        createBackupDirectory();

        // Generate backup filename
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String backupFile = BACKUP_PATH + "backup_" + timestamp + ".sql";

        // Build mysqldump command
        List<String> command = new ArrayList<>();
        command.add("mysqldump");
        command.add("--user=" + config.getDatabaseUsername());
        if (!config.getDatabasePassword().isEmpty()) {
            command.add("--password=" + config.getDatabasePassword());
        }
        command.add("--databases");
        command.add("pos_db");
        command.add("--result-file=" + backupFile);
        command.add("--add-drop-database");
        command.add("--add-drop-table");
        command.add("--triggers");
        command.add("--routines");
        command.add("--events");
        command.add("--comments");
        command.add("--complete-insert");

        // Execute backup command
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Wait for completion
        if (!process.waitFor(5, TimeUnit.MINUTES)) {
            process.destroy();
            throw new InterruptedException("Backup process timed out");
        }

        // Check if backup was successful
        if (process.exitValue() != 0) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String error = reader.lines().reduce("", (a, b) -> a + "\n" + b);
                throw new IOException("Backup failed: " + error);
            }
        }

        // Compress backup file
        compressBackup(backupFile);

        // Clean up old backups
        cleanupOldBackups();

        // Log the backup
        AuditLogger.logSystemAction(
            "BACKUP_CREATED",
            "Database backup created: " + backupFile
        );
    }

    public static void restoreBackup(String backupFile) throws IOException, InterruptedException {
        if (!Files.exists(Paths.get(backupFile))) {
            throw new FileNotFoundException("Backup file not found: " + backupFile);
        }

        // If file is compressed, decompress it first
        if (backupFile.endsWith(".gz")) {
            backupFile = decompressBackup(backupFile);
        }

        // Build mysql command
        List<String> command = new ArrayList<>();
        command.add("mysql");
        command.add("--user=" + config.getDatabaseUsername());
        if (!config.getDatabasePassword().isEmpty()) {
            command.add("--password=" + config.getDatabasePassword());
        }

        // Execute restore command
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectInput(new File(backupFile));
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Wait for completion
        if (!process.waitFor(5, TimeUnit.MINUTES)) {
            process.destroy();
            throw new InterruptedException("Restore process timed out");
        }

        // Check if restore was successful
        if (process.exitValue() != 0) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String error = reader.lines().reduce("", (a, b) -> a + "\n" + b);
                throw new IOException("Restore failed: " + error);
            }
        }

        // Log the restore
        AuditLogger.logSystemAction(
            "BACKUP_RESTORED",
            "Database restored from backup: " + backupFile
        );
    }

    public static List<String> listBackups() throws IOException {
        List<String> backups = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(BACKUP_PATH), 
                "backup_*.{sql,sql.gz}")) {
            for (Path entry : stream) {
                backups.add(entry.getFileName().toString());
            }
        }
        return backups;
    }

    private static void createBackupDirectory() throws IOException {
        Path backupDir = Paths.get(BACKUP_PATH);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }
    }

    private static void compressBackup(String backupFile) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("gzip", backupFile);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Compression interrupted", e);
        }

        if (process.exitValue() != 0) {
            throw new IOException("Compression failed");
        }
    }

    private static String decompressBackup(String compressedFile) throws IOException {
        String uncompressedFile = compressedFile.substring(0, compressedFile.length() - 3);
        ProcessBuilder pb = new ProcessBuilder("gunzip", "-k", compressedFile);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Decompression interrupted", e);
        }

        if (process.exitValue() != 0) {
            throw new IOException("Decompression failed");
        }

        return uncompressedFile;
    }

    private static void cleanupOldBackups() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(BACKUP_PATH))) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    try {
                        LocalDateTime fileTime = LocalDateTime.parse(
                            path.getFileName().toString().substring(7, 22),
                            DATE_FORMAT
                        );
                        
                        if (fileTime.plusDays(RETENTION_DAYS).isBefore(LocalDateTime.now())) {
                            Files.delete(path);
                            AuditLogger.logSystemAction(
                                "BACKUP_DELETED",
                                "Old backup deleted: " + path.getFileName()
                            );
                        }
                    } catch (Exception e) {
                        // Skip files that don't match our naming pattern
                        continue;
                    }
                }
            }
        }
    }
} 