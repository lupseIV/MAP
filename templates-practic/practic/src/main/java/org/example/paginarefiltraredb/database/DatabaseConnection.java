package org.example.paginarefiltraredb.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/practic";
    private static final String USER = "postgres";
    private static final String PASS = "141105";
    private static final String SCHEMA_FILE = "/db/schema.sql";

    private static HikariDataSource dataSource;

    /**
     * Initialize the HikariCP connection pool
     * Call this once at application startup
     */
    public static void initPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            return; // Pool already initialized
        }

        HikariConfig config = new HikariConfig();

        // Basic connection settings
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config.setPassword(PASS);

        // Pool configuration
        config.setMaximumPoolSize(10);           // Max 10 concurrent connections
        config.setMinimumIdle(2);                // Keep at least 2 connections ready
        config.setConnectionTimeout(30000);      // Wait 30s for connection from pool
        config.setIdleTimeout(600000);           // Close idle connections after 10 min
        config.setMaxLifetime(1800000);          // Recycle connections after 30 min

        // Performance optimizations
        config.setAutoCommit(true);
        config.setConnectionTestQuery("SELECT 1"); // Validate connections

        // Connection pool name for monitoring/debugging
        config.setPoolName("PostgreSQL-Pool");

        // Additional PostgreSQL-specific settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        System.out.println("✅ HikariCP connection pool initialized");
    }

    /**
     * Get a connection from the pool
     * IMPORTANT: Always use try-with-resources to ensure connection is returned to pool
     *
     * Example usage:
     * try (Connection conn = DatabaseConnection.getConnection()) {
     *     // Use connection
     * } // Connection automatically returned to pool
     *
     * @return Connection from the pool
     * @throws SQLException if connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initPool();
        }
        return dataSource.getConnection();
    }

    /**
     * Close the entire connection pool
     * Call this at application shutdown
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("✅ HikariCP connection pool closed");
        }
    }

    /**
     * Get pool statistics for monitoring
     */
    public static String getPoolStats() {
        if (dataSource == null || dataSource.isClosed()) {
            return "Pool not initialized";
        }

        return String.format(
                "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }

    /**
     * Initialize database schema from schema.sql file
     */
    public static void initDatabaseSchema() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

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
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

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

    /**
     * Register shutdown hook to close pool on JVM shutdown
     */
    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closePool();
            System.out.println("🛑 Application shutdown - database pool closed");
        }));
    }
}