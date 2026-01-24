package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Client;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;
import java.time.LocalDate;

public class ClientDbRepository extends EntityDbRepository<Integer, Client> {

    public ClientDbRepository(Validator<Client> validator) {
        super(validator, "clients",Integer.class);
        loadFromDatabase();
    }

    @Override
    public Client extractEntityFromResultSet(ResultSet rs) throws SQLException {
        // 2. Changed getLong to getInt
        Integer id = rs.getInt("id");

        String name = rs.getString("full_name");
        String type = rs.getString("client_type");
        Double budget = rs.getDouble("budget");
        Date regDateSql = rs.getDate("registration_date");
        LocalDate regDate = (regDateSql != null) ? regDateSql.toLocalDate() : null;

        Client client = new Client(name, type, budget, regDate);
        client.setId(id); // Ensure Client.setId accepts Integer
        return client;
    }

    @Override
    public void saveToDatabase(Client entity) {
        // ID is auto-generated (SERIAL), so we don't insert it
        String sql = "INSERT INTO clients (id, full_name, client_type, budget, registration_date) " +
                "VALUES (?,?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getId());
            stmt.setString(2, entity.getName());
            stmt.setString(3, entity.getType());
            stmt.setDouble(4, entity.getBudget());
            stmt.setDate(5, Date.valueOf(entity.getRegistrationDate()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Integer id) { // 3. Changed parameter to Integer
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id); // 4. Changed setLong to setInt
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFromDatabase(Client entity) {
        String sql = "UPDATE clients SET full_name=?, client_type=?, budget=?, registration_date=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getType());
            stmt.setDouble(3, entity.getBudget());
            stmt.setDate(4, Date.valueOf(entity.getRegistrationDate()));

            stmt.setInt(5, entity.getId()); // 5. Changed to setInt

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Integer generateNewId() {
        String sql = "SELECT nextval('clients_id_seq')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}