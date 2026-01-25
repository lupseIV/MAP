package org.example.paginarefiltraredb.domain;

import org.example.paginarefiltraredb.customORM.annotations.SchemaGenerator;
import org.example.paginarefiltraredb.database.DatabaseConnection;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class SqlSchemaGenerator {

    public static void main(String[] args) {
        // 1. Define the specific path: src/main/resources/db/schema.sql
        // This works for standard IntelliJ/Eclipse/Maven/Gradle projects
        Path outputPath = Paths.get("src", "main", "resources", "db", "schema.sql");

//        List<Class<?>> entities = List.of(
//                Staff.class,
//                Manager.class,
//                Client.class,
//                VipClient.class
//        );

        System.out.println("Generating SQL schema at: " + outputPath.toAbsolutePath());

        try {
            // 2. CRITICAL: Create the directory structure (db folder) if it doesn't exist
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }

            // 3. Write to the file
            try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {

                // Add a header comment
                writer.write("-- Auto-generated Schema");
                writer.newLine();
                writer.write("-- Generated at: " + java.time.LocalDateTime.now());
                writer.newLine();
                writer.newLine();

//                for (Class<?> entity : entities) {
//                    String sql = SchemaGenerator.generateCreateSQL(entity);
//
//                    writer.write("-- Table for " + entity.getSimpleName());
//                    writer.newLine();
//                    writer.write(sql);
//                    writer.newLine();
//                    writer.newLine();
//                }
            }

            System.out.println("✅ Successfully generated schema.sql");
            System.out.println("👉 IMPORTANT: If you don't see the file, right-click 'src' -> 'Reload/Refresh from Disk'");

            DatabaseConnection.resetSchema();
//            DatabaseConnection.initDatabaseSchema();
            System.out.println("Successfully imported schema.sql");



        } catch (IOException e) {
            System.err.println("❌ Error writing file: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}