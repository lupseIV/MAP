package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Client;
import org.example.paginarefiltraredb.domain.exceptions.RepositoryException;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;
import java.time.LocalDate;

public class ClientDbRepository extends EntityDbRepository<Integer, Client> {

    public ClientDbRepository(Validator<Client> validator) {
        super(validator, "clients", Integer.class);
        loadFromDatabase();
    }

    @Override
    public Client extractEntityFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("full_name");
        String type = rs.getString("client_type");
        Double budget = rs.getDouble("budget");
        Date regDateSql = rs.getDate("registration_date");
        LocalDate regDate = (regDateSql != null) ? regDateSql.toLocalDate() : null;

        Client client = new Client(name, type, budget, regDate);
        client.setId(id);
        return client;
    }

    @Override
    public void saveToDatabase(Client entity) {
        String sql = "INSERT INTO clients (id, full_name, client_type, budget, registration_date) VALUES (?, ?, ?, ?, ?)";
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setInt(1, entity.getId());
            stmt.setString(2, entity.getName());
            stmt.setString(3, entity.getType());
            stmt.setDouble(4, entity.getBudget());
            stmt.setDate(5, Date.valueOf(entity.getRegistrationDate()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Failed to save Client to DB", e);
        }
    }

    @Override
    public void deleteFromDatabase(Integer id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Failed to delete Client from DB", e);
        }
    }

    @Override
    public void updateFromDatabase(Client entity) {
        String sql = "UPDATE clients SET full_name=?, client_type=?, budget=?, registration_date=? WHERE id=?";
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getType());
            stmt.setDouble(3, entity.getBudget());
            stmt.setDate(4, Date.valueOf(entity.getRegistrationDate()));
            stmt.setInt(5, entity.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Failed to update Client in DB", e);
        }
    }
}