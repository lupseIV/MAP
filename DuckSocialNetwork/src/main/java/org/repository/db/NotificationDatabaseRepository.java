package org.repository.db;

import database.DatabaseConnection;
import org.domain.exceptions.RepositoryException;
import org.domain.users.User;
import org.domain.users.duck.Duck;
import org.domain.users.person.Person;
import org.domain.users.relationships.messages.Notification;
import org.domain.validators.Validator;
import org.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;

public class NotificationDatabaseRepository extends EntityDatabaseRepository<Long, Notification> {

    private final Repository<Long, Duck> duckRepository;
    private final Repository<Long, Person> personRepository;

    public NotificationDatabaseRepository(Validator<Notification> validator, 
                                          Repository<Long, Duck> duckRepository, 
                                          Repository<Long, Person> personRepository) {
        super(validator, "SELECT * FROM notifications");
        this.duckRepository = duckRepository;
        this.personRepository = personRepository;
    }

    /**
     * Get the next ID from the database sequence.
     * This ensures unique IDs across multiple application instances.
     */
    private Long getNextIdFromDatabase() {
        String sql = "SELECT nextval('notifications_id_seq')";
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
    public Notification save(Notification entity) {
        // Generate ID from database sequence if not already set
        if (entity.getId() == null) {
            entity.setId(getNextIdFromDatabase());
        }
        
        // Check if entity already exists in memory
        Notification existing = super.findOne(entity.getId());
        if (existing != null) {
            return null; // Already exists
        }
        
        // Save to database first
        saveToDatabase(entity);
        
        // Then add to in-memory cache
        entities.put(entity.getId(), entity);
        
        return null; // Return null to indicate successful save (no previous value)
    }

    private User findOneUserById(Long id) {
        User user = duckRepository.findOne(id);
        if (user == null) {
            user = personRepository.findOne(id);
        }
        return user;
    }

    @Override
    public Notification extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long recipientId = resultSet.getLong("recipient_id");
        Long senderId = resultSet.getLong("sender_id");
        String messagePreview = resultSet.getString("message_preview");
        Timestamp timestamp = resultSet.getTimestamp("timestamp");
        LocalDateTime dateTime = timestamp != null ? timestamp.toLocalDateTime() : null;
        boolean isRead = resultSet.getBoolean("is_read");

        User recipient = findOneUserById(recipientId);
        User sender = findOneUserById(senderId);

        return new Notification(id, recipient, sender, messagePreview, dateTime, isRead);
    }

    @Override
    public void saveToDatabase(Notification notification) {
        String sql = "INSERT INTO notifications (id, recipient_id, sender_id, message_preview, timestamp, is_read) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, notification.getId());
            stmt.setLong(2, notification.getRecipient().getId());
            stmt.setLong(3, notification.getSender().getId());
            stmt.setString(4, notification.getMessagePreview());
            stmt.setTimestamp(5, Timestamp.valueOf(notification.getTimestamp()));
            stmt.setBoolean(6, notification.isRead());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error saving notification to database", e);
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
            throw new RepositoryException("Error deleting notification from database", e);
        }
    }

    @Override
    public void updateFromDatabase(Notification notification) {
        String sql = "UPDATE notifications SET is_read = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, notification.isRead());
            stmt.setLong(2, notification.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error updating notification in database", e);
        }
    }
}
