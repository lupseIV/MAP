package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Pacient;
import org.example.paginarefiltraredb.domain.exceptions.RepositoryException;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PacientDbRepository extends EntityDbRepository<Long, Pacient> {

    public PacientDbRepository(Validator<Pacient> validator) {
        // Pass the validator, table name "pacients", and ID class Long.class
        super(validator, "pacients", Long.class);
        loadFromDatabase();
    }

    @Override
    public Pacient extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String cnp = resultSet.getString("cnp");
        return new Pacient(id, name, cnp);
    }

    @Override
    public void saveToDatabase(Pacient entity) {
        String sql = "INSERT INTO pacients (name, cnp) VALUES (?, ?)";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            // We don't set ID here because the DB (SERIAL) or generateNewId() handles it
            // depending on how your specific EntityDbRepository generates IDs.
            // Usually for this exam pattern, we insert data columns:
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getCnp());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error saving pacient to DB", e);
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String sql = "DELETE FROM pacients WHERE id = ?";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error deleting pacient from DB", e);
        }
    }

    @Override
    public void updateFromDatabase(Pacient entity) {
        String sql = "UPDATE pacients SET name = ?, cnp = ? WHERE id = ?";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getCnp());
            stmt.setLong(3, entity.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error updating pacient in DB", e);
        }
    }
}