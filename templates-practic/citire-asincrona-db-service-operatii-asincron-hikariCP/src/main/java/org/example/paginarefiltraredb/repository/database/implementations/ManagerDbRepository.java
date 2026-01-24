package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Manager;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;

public class ManagerDbRepository extends EntityDbRepository<Long, Manager> {

    public ManagerDbRepository(Validator<Manager> validator) {
        super(validator, "managers", Long.class);
        loadFromDatabase();
    }

    @Override
    public Manager extractEntityFromResultSet(ResultSet rs) throws SQLException {
        Manager manager = new Manager();
        // Base Person/Staff fields
        manager.setId(rs.getLong("id"));
        manager.setName(rs.getString("name"));
        manager.setEmail(rs.getString("email"));
        manager.setPhone(rs.getString("phone_number"));
        manager.setSalary(rs.getBigDecimal("salary"));
        manager.setDepartment(rs.getString("department"));
        Date date = rs.getDate("hire_date");
        if (date != null) manager.setHireDate(date.toLocalDate());

        // Manager specific fields
        manager.setBonus(rs.getDouble("bonus"));
        manager.setTeamSize(rs.getInt("team_size"));
        manager.setAccessLevel(rs.getInt("access_level"));

        return manager;
    }

    @Override
    public void saveToDatabase(Manager entity) {
        // Very wide INSERT statement
        String sql = "INSERT INTO managers (name, email, phone_number, salary, department, hire_date, bonus, team_size, access_level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getEmail());
            stmt.setString(3, entity.getPhone());
            stmt.setBigDecimal(4, entity.getSalary());
            stmt.setString(5, entity.getDepartment());
            stmt.setDate(6, Date.valueOf(entity.getHireDate()));
            stmt.setDouble(7, entity.getBonus());
            stmt.setInt(8, entity.getTeamSize());
            stmt.setInt(9, entity.getAccessLevel());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String sql = "DELETE FROM managers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFromDatabase(Manager entity) {
        String sql = "UPDATE managers SET name=?, email=?, phone_number=?, salary=?, department=?, hire_date=?, bonus=?, team_size=?, access_level=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getEmail());
            stmt.setString(3, entity.getPhone());
            stmt.setBigDecimal(4, entity.getSalary());
            stmt.setString(5, entity.getDepartment());
            stmt.setDate(6, Date.valueOf(entity.getHireDate()));
            stmt.setDouble(7, entity.getBonus());
            stmt.setInt(8, entity.getTeamSize());
            stmt.setInt(9, entity.getAccessLevel());
            stmt.setLong(10, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}