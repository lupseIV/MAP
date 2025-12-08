package org.repository.db;

import database.DatabaseConnection;
import org.domain.exceptions.RepositoryException;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.domain.validators.Validator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.StreamSupport;

public class FriendshipDatabaseRepository extends EntityDatabaseRepository<Long, Friendship>{

    private final DuckDatabaseRepository duckDatabaseRepository;
    private final PersonDatabaseRepository personDatabaseRepository;

    public FriendshipDatabaseRepository(Validator<Friendship> validator, DuckDatabaseRepository duckDatabaseRepository, PersonDatabaseRepository personDatabaseRepository) {
        super(validator, "SELECT * FROM friendships",false);
        this.duckDatabaseRepository = duckDatabaseRepository;
        this.personDatabaseRepository = personDatabaseRepository;
        loadFromDatabase();
    }

    /**
     * Get the next ID from the database sequence.
     * This ensures unique IDs across multiple application instances.
     */
    private Long getNextIdFromDatabase() {
        String sql = "SELECT nextval('friendships_id_seq')";
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

    /**
     * Override save to use database-generated ID instead of in-memory generator.
     */
    @Override
    public Friendship save(Friendship entity) {
        // Check if entity already exists in memory
        Friendship existing = super.findOne(entity.getId());
        if (existing != null) {
            return null; // Already exists
        }
        
        // Generate ID from database sequence if not already set
        if (entity.getId() == null) {
            entity.setId(getNextIdFromDatabase());
        }
        
        // Save to database first
        saveToDatabase(entity);
        
        // Then add to in-memory cache
        entities.put(entity.getId(), entity);
        
        return null;
    }

    private User findUserById(Long id) {
        User user = StreamSupport.stream(duckDatabaseRepository.findAll().spliterator(), false)
                .filter(d -> d.getId().equals(id) )
                .findFirst()
                .orElse(null);
        if (user == null) {
            user = StreamSupport.stream(personDatabaseRepository.findAll().spliterator(),false)
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }
        return user;
    }


    @Override
    public Friendship extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long user1Id = resultSet.getLong("user1_id");
        Long user2Id = resultSet.getLong("user2_id");

        User user1 = findUserById(user1Id);
        User user2 = findUserById(user2Id);

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
