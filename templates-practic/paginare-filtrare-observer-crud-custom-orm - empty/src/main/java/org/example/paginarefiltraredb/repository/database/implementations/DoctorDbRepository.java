package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctorDbRepository extends EntityDbRepository<Long, Doctor> {

    private final String TABLE_NAME = "doctors";

    public DoctorDbRepository(Validator<Doctor> validator) {
        super(validator, "doctors", Long.class);
        loadFromDatabase();
    }

    @Override
    public Doctor extractEntityFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String specialty = rs.getString("specialty");

        return new Doctor(id, name, specialty);
    }

    @Override
    public void saveToDatabase(Doctor entity) {
        String sql = "INSERT INTO " + TABLE_NAME + "( name,specialty,id) " +
                //TODO             "(  campuri din baza de date) " +
                "VALUES (?,?, ?)";
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getSpecialty());
            stmt.setLong(3, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {

    }

    @Override
    public void updateFromDatabase(Doctor entity) {

    }
}