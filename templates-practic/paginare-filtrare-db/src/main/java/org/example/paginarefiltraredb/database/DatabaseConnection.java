package org.example.paginarefiltraredb.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/paginare-filtrare-test";
    private static final String USER = "postgres";
    private static final String PASS = "141105";
    private static final String SCHEMA_FILE = "/db/schema.sql";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            // In a shutdown scenario, we usually just log the error and move on
            System.err.println("Error while closing connection: " + e.getMessage());
        } finally {
            // Force reset the variable so future calls know it's gone
            connection = null;
        }
    }

    public static void initDatabaseSchema() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = readSchemaFile();

            String[] statements = sql.split(";");
            for (String statement : statements) {
                String trimmedStatement = statement.trim();

                if (!trimmedStatement.isEmpty()) {
                    String cleanSql = removeComments(trimmedStatement);
                    if(!cleanSql.isEmpty()) {
                        stmt.execute(cleanSql);
                    }
                }
            }

        } catch (IOException e){
            throw new SQLException("Failed to load DB Schema file: " + e.getMessage(), e);
        }
    }


    private static String readSchemaFile() throws IOException {
        try (InputStream inputStream = DatabaseConnection.class.getResourceAsStream(SCHEMA_FILE)) {
            if (inputStream == null) {
                throw new IOException("Schema file not found: " + SCHEMA_FILE);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }


    private static String removeComments(String sql) {
        StringBuilder result = new StringBuilder();
        String[] lines = sql.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if(!trimmedLine.startsWith("--")) {
                int commentIndex = line.indexOf("--");
                if(commentIndex > 0) {
                    result.append(line, 0, commentIndex).append("\n");
                } else {
                    result.append(line).append("\n");
                }
            }
        }
        return result.toString().trim();
    }

    public static void resetSchema() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("⚠️ RESETTING DATABASE...");

            // 1. The "Nuclear" Option: Wipes all tables, sequences, and views instantly.
            // This fixes the "no unique constraint" errors by removing old broken tables.
            stmt.execute("DROP SCHEMA public CASCADE");
            stmt.execute("CREATE SCHEMA public");

            System.out.println("✅ Database wiped clean.");

            // 2. Re-create the tables from your schema.sql file
            initDatabaseSchema();

            System.out.println("✅ Schema re-initialized successfully.");
        }
    }
}