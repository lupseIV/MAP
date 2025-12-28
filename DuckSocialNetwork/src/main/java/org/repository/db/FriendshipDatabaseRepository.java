package org.repository.db;

import database.DatabaseConnection;
import org.domain.exceptions.RepositoryException;
import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.domain.validators.Validator;
import org.utils.enums.status.FriendRequestStatus;

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

    public User findUserById(Long id) {
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
        FriendRequestStatus status = FriendRequestStatus.valueOf(resultSet.getString("status"));

        User user1 = findUserById(user1Id);
        User user2 = findUserById(user2Id);

        if (user1 == null || user2 == null) {
            throw new RepositoryException("User IDs do not exist in the system");
        }


        var friendShip = new Friendship(user1, user2, status);
        friendShip.setId(id);

        if (status.equals(FriendRequestStatus.APPROVED)) {
        user1.addFriend(user2);
        }

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
        String sql = "UPDATE friendships SET user1_id = ?, user2_id = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, friendship.getUser1().getId());
            stmt.setLong(2, friendship.getUser2().getId());
            stmt.setString(3, friendship.getStatus().name());
            stmt.setLong(4, friendship.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating friendship in database", e);
        }
    }
}
