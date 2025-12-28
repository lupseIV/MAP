package org.repository.db;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import database.DatabaseConnection;
import org.domain.exceptions.RepositoryException;
import org.domain.users.User;
import org.domain.users.relationships.notifications.Notification;
import org.domain.users.relationships.notifications.NotificationData;
import org.domain.validators.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.types.NotificationType;

import java.sql.*;

public class NotificationDatabaseRepository extends EntityDatabaseRepository<Long, Notification>{

    private FriendshipDatabaseRepository friendshipDatabaseRepository;
    private final ObjectMapper objectMapper;

    public NotificationDatabaseRepository(Validator<Notification> validator, FriendshipDatabaseRepository friendshipDatabaseRepository) {
        super(validator, "SELECT * FROM notifications",false);
        this.friendshipDatabaseRepository = friendshipDatabaseRepository;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        loadFromDatabase();
    }


    @Override
    public Notification extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String typeStr = resultSet.getString("type");
        String statusStr = resultSet.getString("status");
        String description = resultSet.getString("description");
        Long senderId = resultSet.getLong("sender_id");
        Long receiverId = resultSet.getLong("receiver_id");
        Timestamp createdAtTs = resultSet.getTimestamp("created_at");
        String jsonData = resultSet.getString("data");

        NotificationType type = NotificationType.valueOf(typeStr);
        NotificationStatus status = NotificationStatus.valueOf(statusStr);

        User sender = (senderId != 0) ? friendshipDatabaseRepository.findUserById(senderId) : null;
        User receiver = friendshipDatabaseRepository.findUserById(receiverId);

        Notification notification = new Notification(type, status, sender, receiver);
        notification.setId(id);
        notification.setDescription(description);
        // notification.setDate(createdAtTs.toLocalDateTime());

        if (jsonData != null && !jsonData.isEmpty()) {
            try {
                NotificationData data = objectMapper.readValue(jsonData, NotificationData.class);
                notification.setData(data);
            } catch (JsonProcessingException e) {
               throw new RepositoryException("Failed to parse notification data: " + e.getMessage());
            }
        }

        return notification;
    }

    @Override
    public void saveToDatabase(Notification entity) {
        String sql = "INSERT INTO notifications (type, status, description, sender_id, receiver_id, data) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, entity.getType().name());
            stmt.setString(2, entity.getStatus().name());
            stmt.setString(3, entity.getDescription());

            if (entity.getSender() != null) {
                stmt.setLong(4, entity.getSender().getId());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }

            stmt.setLong(5, entity.getReceiver().getId());

            String json = null;
            if (entity.getData() != null) {
                try {
                    json = objectMapper.writeValueAsString(entity.getData());
                } catch (JsonProcessingException e) {
                    throw new RepositoryException("Error serializing notification data", e);
                }
            }
            stmt.setString(6, json);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error saving notification", e);
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String sql = "DELETE FROM notifications WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting notification", e);
        }
    }

    @Override
    public void updateFromDatabase(Notification entity) {
        String sql = "UPDATE notifications SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getStatus().name());
            stmt.setLong(2, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating notification", e);
        }
    }
}
