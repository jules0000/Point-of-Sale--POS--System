package com.pos.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

import com.pos.db.DatabaseConnection;

public class DatabaseReset {
    public static void main(String[] args) {
        System.out.println("Starting database reset...");
        
        try {
            // Test database connection first
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (!dbConn.testConnection()) {
                System.err.println("Failed to connect to database. Please check your MySQL configuration.");
                return;
            }
            
            // Get database connection
            Connection conn = dbConn.getConnection();
            System.out.println("Connected to database successfully.");
            
            // Drop and recreate database
            try (Statement stmt = conn.createStatement()) {
                System.out.println("Dropping existing database...");
                stmt.execute("DROP DATABASE IF EXISTS pos_db");
                
                System.out.println("Creating new database...");
                stmt.execute("CREATE DATABASE pos_db");
                stmt.execute("USE pos_db");
                System.out.println("Database recreated successfully.");
            }
            
            // Read and execute schema.sql
            System.out.println("\nExecuting schema.sql...");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    DatabaseReset.class.getResourceAsStream("/sql/schema.sql")))) {
                
                StringBuilder script = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    script.append(line).append("\n");
                }
                
                // Split script into individual statements
                String[] statements = script.toString().split(";");
                
                try (Statement stmt = conn.createStatement()) {
                    for (String statement : statements) {
                        if (!statement.trim().isEmpty()) {
                            try {
                                stmt.execute(statement);
                            } catch (Exception e) {
                                System.err.println("Error executing statement: " + statement);
                                System.err.println("Error message: " + e.getMessage());
                            }
                        }
                    }
                }
                System.out.println("Schema created successfully.");
            }
            
            // Read and execute dummy_data.sql
            System.out.println("\nExecuting dummy_data.sql...");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    DatabaseReset.class.getResourceAsStream("/dummy_data.sql")))) {
                
                StringBuilder script = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    script.append(line).append("\n");
                }
                
                // Split script into individual statements
                String[] statements = script.toString().split(";");
                
                try (Statement stmt = conn.createStatement()) {
                    for (String statement : statements) {
                        if (!statement.trim().isEmpty()) {
                            try {
                                stmt.execute(statement);
                            } catch (Exception e) {
                                System.err.println("Error executing statement: " + statement);
                                System.err.println("Error message: " + e.getMessage());
                            }
                        }
                    }
                }
                System.out.println("Dummy data inserted successfully.");
            }
            
            // Close database connection
            dbConn.closePool();
            System.out.println("\nDatabase reset completed successfully.");
            
        } catch (Exception e) {
            System.err.println("\nError resetting database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 