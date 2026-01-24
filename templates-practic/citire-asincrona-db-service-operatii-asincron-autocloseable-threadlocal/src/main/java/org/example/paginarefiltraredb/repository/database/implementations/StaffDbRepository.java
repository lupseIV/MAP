package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Staff;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;

public class StaffDbRepository extends EntityDbRepository<Long, Staff> {

    public StaffDbRepository(Validator<Staff> validator) {
        super(validator, "staff", Long.class);
        loadFromDatabase();
    }

    @Override
    public Staff extractEntityFromResultSet(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getLong("id"));
        staff.setName(rs.getString("name"));
        staff.setEmail(rs.getString("email"));
        staff.setPhone(rs.getString("phone_number"));
        staff.setSalary(rs.getBigDecimal("salary"));
        staff.setDepartment(rs.getString("department"));

        Date date = rs.getDate("hire_date");
        if (date != null) staff.setHireDate(date.toLocalDate());

        return staff;
    }

    @Override
    public void saveToDatabase(Staff entity) {
        String sql = "INSERT INTO staff (name, email, phone_number, salary, department, hire_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getEmail());
            stmt.setString(3, entity.getPhone());
            stmt.setBigDecimal(4, entity.getSalary());
            stmt.setString(5, entity.getDepartment());
            stmt.setDate(6, Date.valueOf(entity.getHireDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String sql = "DELETE FROM staff WHERE id = ?";
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFromDatabase(Staff entity) {
        String sql = "UPDATE staff SET name=?, email=?, phone_number=?, salary=?, department=?, hire_date=? WHERE id=?";
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getEmail());
            stmt.setString(3, entity.getPhone());
            stmt.setBigDecimal(4, entity.getSalary());
            stmt.setString(5, entity.getDepartment());
            stmt.setDate(6, Date.valueOf(entity.getHireDate()));
            stmt.setLong(7, entity.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
