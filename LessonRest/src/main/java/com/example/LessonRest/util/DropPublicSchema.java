package com.example.LessonRest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DropPublicSchema {
    private static final Logger logger = LoggerFactory.getLogger(DropPublicSchema.class);

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/game";
        String user = "postgres";
        String password = "postgres";
        
        String[] dropStatements = {
            "DROP TABLE IF EXISTS public.tbl_editors CASCADE",
            "DROP TABLE IF EXISTS public.tbl_news CASCADE",
            "DROP TABLE IF EXISTS public.tbl_notices CASCADE",
            "DROP TABLE IF EXISTS public.tbl_stickers CASCADE",
            "DROP TABLE IF EXISTS public.tbl_news_stickers CASCADE"
        };
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            
            for (String sql : dropStatements) {
                try {
                    stmt.execute(sql);
                    logger.info("Executed: {}", sql);
                } catch (Exception e) {
                    logger.info("Skipped: {} - {}", sql, e.getMessage());
                }
            }
            
            logger.info("Public schema tables dropped successfully!");
            
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
        }
    }
}
