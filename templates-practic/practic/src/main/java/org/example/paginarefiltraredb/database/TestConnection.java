package org.example.paginarefiltraredb.database;

public class TestConnection {
    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
}
