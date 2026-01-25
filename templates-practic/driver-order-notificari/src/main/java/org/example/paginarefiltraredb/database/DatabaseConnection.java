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

    /**
     * Get an auto-closeable connection wrapper
     *
     * ALWAYS use with try-with-resources:
     * try (AutoCloseableConnection conn = DatabaseConnection.getConnection()) {
     *     // Use conn.get() to access the actual Connection
     * } // Connection automatically closed
     *
     * @return AutoCloseableConnection wrapper
     * @throws SQLException if connection cannot be established
     */
    public static AutoCloseableConnection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        return new AutoCloseableConnection(conn);
    }

    /**
     * Auto-closeable wrapper for database connections
     * Ensures connections are always properly closed
     */
    public static class AutoCloseableConnection implements AutoCloseable {
        private final Connection connection;
        private boolean closed = false;

        private AutoCloseableConnection(Connection connection) {
            this.connection = connection;
        }

        /**
         * Get the underlying Connection object
         * @return the actual JDBC Connection
         */
        public Connection get() {
            if (closed) {
                throw new IllegalStateException("Connection has been closed");
            }
            return connection;
        }

        @Override
        public void close() throws SQLException {
            if (!closed && connection != null && !connection.isClosed()) {
                connection.close();
                closed = true;
            }
        }

        /**
         * Check if this connection wrapper is closed
         */
        public boolean isClosed() {
            return closed;
        }
    }

    /**
     * Initialize database schema from schema.sql file
     */
    public static void initDatabaseSchema() throws SQLException {
        try (AutoCloseableConnection conn = getConnection();
             Statement stmt = conn.get().createStatement()) {

            String sql = readSchemaFile();

            String[] statements = sql.split(";");
            for (String statement : statements) {
                String trimmedStatement = statement.trim();

                if (!trimmedStatement.isEmpty()) {
                    String cleanSql = removeComments(trimmedStatement);
                    if (!cleanSql.isEmpty()) {
                        stmt.execute(cleanSql);
                    }
                }
            }

            System.out.println("✅ Database schema initialized");

        } catch (IOException e) {
            throw new SQLException("Failed to load DB Schema file: " + e.getMessage(), e);
        }
    }

    /**
     * Reset the entire database schema (use with caution!)
     */
    public static void resetSchema() throws SQLException {
        try (AutoCloseableConnection conn = getConnection();
             Statement stmt = conn.get().createStatement()) {

            System.out.println("⚠️  RESETTING DATABASE...");

            // Drop and recreate schema
            stmt.execute("DROP SCHEMA public CASCADE");
            stmt.execute("CREATE SCHEMA public");

            System.out.println("✅ Database wiped clean");

            // Re-create tables
            initDatabaseSchema();

            System.out.println("✅ Schema re-initialized successfully");
        }
    }

    /**
     * Read schema file from resources
     */
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

    /**
     * Remove SQL comments from statements
     */
    private static String removeComments(String sql) {
        StringBuilder result = new StringBuilder();
        String[] lines = sql.split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.startsWith("--")) {
                int commentIndex = line.indexOf("--");
                if (commentIndex > 0) {
                    result.append(line, 0, commentIndex).append("\n");
                } else {
                    result.append(line).append("\n");
                }
            }
        }
        return result.toString().trim();
    }
}