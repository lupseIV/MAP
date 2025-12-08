package database;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io. InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java. sql.Statement;
import java.util.stream.Collectors;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "141105";
    private static final String SCHEMA_FILE = "/db/schema.sql";

    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config. setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config. setPassword(PASS);

        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config. setIdleTimeout(300000);        // 5 minutes
        config.setMaxLifetime(600000);        // 10 minutes
        config.setConnectionTimeout(30000);   // 30 seconds
        config.setKeepaliveTime(60000);       // 1 minute keepalive
        config. setAutoCommit(true);
        // Validate connections before use
        config.setConnectionTestQuery("SELECT 1");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
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
                    if (! cleanSql.isEmpty()) {
                        stmt.execute(cleanSql);
                    }
                }
            }
        } catch (IOException e) {
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
        String[] lines = sql. split("\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (! trimmedLine.startsWith("--")) {
                int commentIndex = line.indexOf("--");
                if (commentIndex > 0) {
                    result.append(line, 0, commentIndex).append("\n");
                } else {
                    result.append(line). append("\n");
                }
            }
        }
        return result.toString(). trim();
    }
}