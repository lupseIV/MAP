package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.User;
import org.example.paginarefiltraredb.domain.entities.UserRole;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;
import java.util.Optional;

public class UserDbRepository extends EntityDbRepository<Long, User> {

    private static final String TABLE_NAME = "users";

    public UserDbRepository(Validator<User> validator) {
        super(validator, TABLE_NAME, Long.class);
        loadFromDatabase();
    }

    @Override
    public User extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String roleStr = resultSet.getString("role");
        UserRole role = UserRole.valueOf(roleStr);

        User user = new User(username, password, role);
        user.setId(id);
        return user;
    }

    @Override
    public void saveToDatabase(User entity) {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, username, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, entity.getId());
            stmt.setString(2, entity.getUsername());
            stmt.setString(3, entity.getPassword());
            stmt.setString(4, entity.getRole().name());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFromDatabase(User entity) {
        String sql = "UPDATE " + TABLE_NAME + " SET username = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getUsername());
            stmt.setString(2, entity.getPassword());
            stmt.setString(3, entity.getRole().name());
            stmt.setLong(4, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Optional<User> findByUsernameAndPassword(String username, String password) {
        for (User user : findAll()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
