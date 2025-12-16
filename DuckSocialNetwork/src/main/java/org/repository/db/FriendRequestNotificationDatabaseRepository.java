package org.repository.db;

import database.DatabaseConnection;
import org.domain.exceptions.RepositoryException;
import org.domain.users.User;
import org.domain.users.relationships.notifications.FriendRequestNotification;
import org.domain.validators.Validator;
import org.utils.enums.NotificationStatus;

import java.sql.*;

public class FriendRequestNotificationDatabaseRepository extends EntityDatabaseRepository<Long, FriendRequestNotification>{

    private FriendshipDatabaseRepository friendshipDatabaseRepository;

    public FriendRequestNotificationDatabaseRepository(Validator<FriendRequestNotification> validator, FriendshipDatabaseRepository friendshipDatabaseRepository) {
        super(validator, "SELECT * FROM friend_notifications",false);
        this.friendshipDatabaseRepository = friendshipDatabaseRepository;
        loadFromDatabase();
    }

    @Override
    public FriendRequestNotification extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("notification_id");
        Long user1Id = resultSet.getLong("user1_id");
        Long user2Id = resultSet.getLong("user2_id");
        NotificationStatus notificationStatus = NotificationStatus.valueOf(resultSet.getString("status"));


        User u1 = friendshipDatabaseRepository.findUserById(user1Id);
        User u2 = friendshipDatabaseRepository.findUserById(user2Id);

        if (u1 == null || u2 == null) {
            return null;
        }

        var not = new FriendRequestNotification(id, u1, u2);
        not.setStatus(notificationStatus);
        return not;
    }

    @Override
    public void saveToDatabase(FriendRequestNotification entity) {
        String sql = "INSERT INTO friend_notifications (notification_id, user1_id, user2_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, entity.getId());
            stmt.setLong(2, entity.getFrom().getId());
            stmt.setLong(3, entity.getTo().getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error saving notification", e);
        }
    }

    @Override
    public void deleteFromDatabase(Long aLong) {
        String sql = "DELETE FROM friend_notifications WHERE notification_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, aLong);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting notification", e);
        }
    }

    @Override
    public void updateFromDatabase(FriendRequestNotification entity) {
        String sql = "UPDATE friend_notifications SET user1_id = ?, user2_id = ?, status = ? WHERE notification_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, entity.getFrom().getId());
            stmt.setLong(2, entity.getTo().getId());
            stmt.setString(3, entity.getStatus().toString());
            stmt.setLong(4, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating notification", e);
        }
    }
}
