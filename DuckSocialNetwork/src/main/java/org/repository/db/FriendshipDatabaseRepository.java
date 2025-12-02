package org.repository.db;

import database.DatabaseConnection;
import org.domain.exceptions.RepositoryException;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.domain.validators.Validator;

import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendshipDatabaseRepository extends EntityDatabaseRepository<Long, Friendship>{

    private final DuckDatabaseRepository duckDatabaseRepository;
    private final PersonDatabaseRepository personDatabaseRepository;
    protected Map<Long, User> users;

    public FriendshipDatabaseRepository(Validator<Friendship> validator, DuckDatabaseRepository duckDatabaseRepository, PersonDatabaseRepository personDatabaseRepository) {
        super(validator, "SELECT * FROM friendships",false);
        this.duckDatabaseRepository = duckDatabaseRepository;
        this.personDatabaseRepository = personDatabaseRepository;
        initUsers();
        loadFromDatabase();
    }

    private void initUsers() {
        users = new HashMap<>();
        duckDatabaseRepository.findAll().forEach(d -> users.put(d.getId(), d));
        personDatabaseRepository.findAll().forEach(u -> users.put(u.getId(), u));
    }

    @Override
    public Page<Friendship> findAllOnPage(Pageable pageable) {
        String sql = sqlSelectAllStatement + " LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            List<Friendship> entries = new ArrayList<>();

            stmt.setInt(1, pageable.getPageSize());
            stmt.setInt(2, pageable.getPageNumber() * pageable.getPageSize());

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Friendship friendship = extractEntityFromResultSet(resultSet);
                    entries.add(friendship);
                }
            }
            return new Page<>(entries, entities.size());
        } catch (SQLException e) {
            throw new RepositoryException("Error getting page of friendships", e);
        }
    }

    @Override
    public Friendship extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long user1Id = resultSet.getLong("user1_id");
        Long user2Id = resultSet.getLong("user2_id");

        User user1 = users.get(user1Id);
        User user2 = users.get(user2Id);

        if (user1 == null || user2 == null) {
            throw new RepositoryException("User IDs do not exist in the system");
        }

        var friendShip = new Friendship(user1, user2);
        friendShip.setId(id);
        user1.addFriend(user2);
        return friendShip;
    }

    @Override
    public void saveToDatabase(Friendship friendship) {
        String sql = "INSERT INTO friendships (id, user1_id, user2_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, friendship.getId());
            stmt.setLong(2, friendship.getUser1().getId());
            stmt.setLong(3, friendship.getUser2().getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error saving friendship to database", e);
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String sql = "DELETE FROM friendships WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting friendship from database", e);
        }
    }

    @Override
    public void updateFromDatabase(Friendship friendship) {
        String sql = "UPDATE friendships SET user1_id = ?, user2_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, friendship.getUser1().getId());
            stmt.setLong(2, friendship.getUser2().getId());
            stmt.setLong(3, friendship.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating friendship in database", e);
        }
    }
}
