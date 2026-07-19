package com.example.LessonRest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseCleaner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseCleaner.class);

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/game";
        String user = "postgres";
        String password = "postgres";
        
        // First, drop all tables and schema
        String[] dropStatements = {
            "DROP TABLE IF EXISTS distcomp.tbl_editors CASCADE",
            "DROP TABLE IF EXISTS distcomp.tbl_news CASCADE",
            "DROP TABLE IF EXISTS distcomp.tbl_notices CASCADE",
            "DROP TABLE IF EXISTS distcomp.tbl_stickers CASCADE",
            "DROP TABLE IF EXISTS distcomp.tbl_news_stickers CASCADE",
            "DROP SCHEMA IF EXISTS distcomp CASCADE"
        };
        
        // Then recreate schema
        String createSchema = "CREATE SCHEMA distcomp";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            // Drop all tables and schema
            for (String sql : dropStatements) {
                try {
                    stmt.execute(sql);
                    logger.info("Executed: {}", sql);
                } catch (Exception e) {
                    // Ignore if table/schema doesn't exist
                    logger.info("Skipped (not found): {}", sql);
                }
            }
            
            // Recreate schema
            stmt.execute(createSchema);
            logger.info("Executed: {}", createSchema);
            
            logger.info("Database schema dropped and recreated successfully!");
            
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
        }
    }
}
