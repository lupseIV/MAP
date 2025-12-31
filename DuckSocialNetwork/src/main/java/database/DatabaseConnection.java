package database;

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
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "141105";
    private static final String SCHEMA_FILE = "/db/schema.sql";

    /**
     * ThreadLocal storage - each thread gets its own Connection
     * This ensures thread safety without synchronization overhead
     */
    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    /**
     * Get connection for the current thread - THREAD SAFE
     *
     * If the current thread doesn't have a connection yet, or if it's closed,
     * a new connection will be created and stored for this thread.
     *
     * @return Connection for the current thread
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = threadLocalConnection.get();

        // Check if connection exists and is still valid
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            threadLocalConnection.set(conn);
        }

        return conn;
    }

    /**
     * Close the connection for the current thread
     *
     * Should be called when the thread is done with database operations
     * (e.g., at the end of a request in a web application, or when a worker thread finishes)
     *
     * @throws SQLException if connection cannot be closed
     */
    public static void closeConnection() throws SQLException {
        Connection conn = threadLocalConnection.get();
        if (conn != null && !conn.isClosed()) {
            conn.close();
            threadLocalConnection.remove(); // Remove from ThreadLocal to prevent memory leaks
        }
    }

    public static AutoCloseableConnection getAutoCloseableConnection() throws SQLException {
        return new AutoCloseableConnection(getConnection());
    }

    public static class AutoCloseableConnection implements AutoCloseable {
        private final Connection connection;

        private AutoCloseableConnection(Connection connection) {
            this.connection = connection;
        }

        public Connection get() {
            return connection;
        }

        @Override
        public void close() throws SQLException {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                threadLocalConnection.remove();
            }
        }
    }


    public static void closeAllConnections() {
        try {
            Connection conn = threadLocalConnection.get();
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        } finally {
            threadLocalConnection.remove();
        }
    }


    public static void initDatabaseSchema() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = readSchemaFile();

            // Split by semicolon and execute each statement
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


    public static boolean hasActiveConnection() {
        try {
            Connection conn = threadLocalConnection.get();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }


    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closeAllConnections();
            System.out.println("Database connections closed");
        }));
    }
}