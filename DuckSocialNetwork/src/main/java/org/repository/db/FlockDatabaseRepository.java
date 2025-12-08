package org.repository.db;

import database.DatabaseConnection;
import org.domain.exceptions.RepositoryException;
import org.domain.users.duck.Duck;
import org.domain.users.duck.flock.Flock;
import org.domain.validators.Validator;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlockDatabaseRepository extends EntityDatabaseRepository<Long, Flock<Duck>>{

    private final DuckDatabaseRepository duckDatabaseRepository;


    public FlockDatabaseRepository(Validator<Flock<Duck>> validator, DuckDatabaseRepository duckDatabaseRepository) {
        super(validator, "SELECT * FROM flocks", false);
        this.duckDatabaseRepository = duckDatabaseRepository;
        loadFromDatabase();
    }

    /**
     * Get the next ID from the database sequence.
     * This ensures unique IDs across multiple application instances.
     */protected Long getNextIdFromDatabase() {
        String sql = "SELECT nextval('flocks_id_seq')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new RepositoryException("Failed to get next ID from database sequence");
        } catch (SQLException e) {
            throw new RepositoryException("Error getting next ID from database sequence", e);
        }
    }



    @Override
    public Flock<Duck> extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("flock_name");

        Flock<Duck> flock = new Flock<>(name);
        flock.setId(id);

        // Load members
        List<Duck> members = loadFlockMembers(id);
        members.forEach(flock::addMember);

        return flock;
    }

    private List<Duck> loadFlockMembers(Long flockId) {
        List<Duck> members = new ArrayList<>();
        String sql = "SELECT duck_id FROM flock_members WHERE flock_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, flockId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long duckId = rs.getLong("duck_id");
                Duck duck = duckDatabaseRepository.findOne(duckId);
                if (duck != null) {
                    members.add(duck);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error loading flock members from database", e);
        }

        return members;
    }

    @Override
    public void saveToDatabase(Flock<Duck> flock) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO flocks (id, flock_name) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, flock.getId());
                stmt.setString(2, flock.getFlockName());
                stmt.executeUpdate();
            }

            saveFlockMembers(conn, flock);

        } catch (SQLException e) {
            throw new RepositoryException("Error saving flock to database", e);
        }
    }

    private void saveFlockMembers(Connection conn, Flock<Duck> flock) throws SQLException {
        String sql = "INSERT INTO flock_members (flock_id, duck_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Duck member : flock.getMembers()) {
                stmt.setLong(1, flock.getId());
                stmt.setLong(2, member.getId());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Delete members first
            String deleteMembersSql = "DELETE FROM flock_members WHERE flock_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteMembersSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            // Delete flock
            String deleteFlockSql = "DELETE FROM flocks WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteFlockSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting flock from database", e);
        }
    }

    @Override
    public void updateFromDatabase(Flock<Duck> flock) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Update flock
            String sql = "UPDATE flocks SET flock_name = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, flock.getFlockName());
                stmt.setLong(2, flock.getId());
                stmt.executeUpdate();
            }

            // Delete old members
            String deleteSql = "DELETE FROM flock_members WHERE flock_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setLong(1, flock.getId());
                stmt.executeUpdate();
            }

            // Insert new members
            saveFlockMembers(conn, flock);

        } catch (SQLException e) {
            throw new RepositoryException("Error updating flock in database", e);
        }
    }
}
