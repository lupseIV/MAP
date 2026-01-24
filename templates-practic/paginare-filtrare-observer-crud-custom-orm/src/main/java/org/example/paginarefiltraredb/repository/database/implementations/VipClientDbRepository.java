package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Manager;
import org.example.paginarefiltraredb.domain.entities.VipClient;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;
import java.time.LocalDate;

public class VipClientDbRepository extends EntityDbRepository<Integer, VipClient> {

    public VipClientDbRepository(Validator<VipClient> validator) {
        super(validator, "vip_clients", Integer.class);
        loadFromDatabase();
    }

    @Override
    public VipClient extractEntityFromResultSet(ResultSet rs) throws SQLException {
        // 1. Extract Basic Fields
        Integer id = rs.getInt("id");
        String name = rs.getString("full_name");
        String type = rs.getString("client_type");
        Double budget = rs.getDouble("budget");
        Date regDateSql = rs.getDate("registration_date");
        LocalDate regDate = (regDateSql != null) ? regDateSql.toLocalDate() : null;
        int loyalty = rs.getInt("loyalty_points");

        // 2. Handle Foreign Key (Lazy load Manager)
        Long managerId = rs.getLong("personalManager_id");
        Manager manager = null;
        if (managerId != 0) { // JDBC returns 0 for null numeric columns unless wasNull() is checked
            manager = findManagerById(managerId);
        }

        VipClient client = new VipClient();
        client.setId(id);
        client.setName(name);
        client.setType(type);
        client.setBudget(budget);
        client.setRegistrationDate(regDate);
        client.setLoyaltyPoints(loyalty);
        client.setPersonalManager(manager);

        return client;
    }

    /**
     * Helper to fetch the manager entity for the relationship.
     * In an exam, a simple select by ID is perfectly acceptable.
     */
    private Manager findManagerById(Long id) {
        String sql = "SELECT * FROM managers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Reuse logic from Manager Repo (Manual copy here for speed)
                    Manager m = new Manager();
                    m.setId(rs.getLong("id"));
                    m.setName(rs.getString("name"));
                    m.setEmail(rs.getString("email"));
                    m.setDepartment(rs.getString("department"));
                    // ... set other fields if needed
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveToDatabase(VipClient entity) {
        String sql = "INSERT INTO vip_clients (id,full_name, client_type, budget, registration_date, loyalty_points, personalManager_id) VALUES (?,?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getId());
            stmt.setString(2, entity.getName());
            stmt.setString(3, entity.getType());
            stmt.setDouble(4, entity.getBudget());
            stmt.setDate(5, Date.valueOf(entity.getRegistrationDate()));
            stmt.setInt(6, entity.getLoyaltyPoints());

            // Set Foreign Key
            if (entity.getPersonalManager() != null) {
                stmt.setLong(6, entity.getPersonalManager().getId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Integer id) {
        String sql = "DELETE FROM vip_clients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFromDatabase(VipClient entity) {
        String sql = "UPDATE vip_clients SET full_name=?, client_type=?, budget=?, registration_date=?, loyalty_points=?, personalManager_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getType());
            stmt.setDouble(3, entity.getBudget());
            stmt.setDate(4, Date.valueOf(entity.getRegistrationDate()));
            stmt.setInt(5, entity.getLoyaltyPoints());

            if (entity.getPersonalManager() != null) {
                stmt.setLong(6, entity.getPersonalManager().getId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }

            stmt.setLong(7, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}