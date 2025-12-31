package org.repository.db;

import database.DatabaseConnection;
import org.domain.exceptions.RepositoryException;
import org.domain.users.User;
import org.domain.users.duck.Duck;
import org.domain.users.person.Person;
import org.domain.users.relationships.messages.Message;
import org.domain.users.relationships.messages.ReplyMessage;
import org.domain.validators.Validator;
import org.repository.Repository;
import org.utils.enums.status.MessageStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDatabaseRepository extends EntityDatabaseRepository<Long, Message> {

    private final Repository<Long, Duck> duckRepository;
    private final Repository<Long, Person> personRepository;

    public MessageDatabaseRepository(Validator<Message> validator, Repository<Long, Duck> duckRepository, Repository<Long, Person> personRepository) {
        super(validator, "SELECT * FROM messages",false);
        this.duckRepository = duckRepository;
        this.personRepository = personRepository;
        loadFromDatabase();
    }

    private User findOneUserById(Long id) {
        User user = duckRepository.findOne(id);
        if (user == null) {
            user = personRepository.findOne(id);
        }
        return user;
    }

    @Override
    public Message extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long fromId = resultSet.getLong("from_user_id");
        String text = resultSet.getString("message");
        Timestamp timestamp = resultSet.getTimestamp("date");
        LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;
        Long replyToId = resultSet.getLong("reply_to_id");
        MessageStatus status = MessageStatus.valueOf(resultSet.getString("status"));

        User from = findOneUserById(fromId);
        List<User> to = getRecipientsForMessage(id);

        Message message;
        if (replyToId != 0) {
            Message parentPlaceholder = getParentMessage(replyToId);
            message = new ReplyMessage(id, from, to, text, date, parentPlaceholder, status);
        } else {
            message = new Message(id, from, to, text, date, status);
        }
        if(message.getFrom() == null || message.getTo().isEmpty()){
            deleteFromDatabase(id);
            return null;
        }
        return message;
    }

    private Message getParentMessage(Long id) {
        String sql = "SELECT * FROM messages WHERE id = ? ";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getAutoCloseableConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractParentFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error getting parent message from database", e);
        }

        return null;
    }

    private Message extractParentFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long fromId = rs. getLong("from_user_id");
        String text = rs.getString("message");
        Timestamp timestamp = rs.getTimestamp("date");
        LocalDateTime date = timestamp != null ? timestamp.toLocalDateTime() : null;
        MessageStatus status = MessageStatus.valueOf(rs.getString("status"));

        User from = findOneUserById(fromId);
        List<User> to = getRecipientsForMessage(id);

        if(from == null || to.isEmpty()){
            deleteFromDatabase(id);
        }

        return new Message(id, from, to, text, date,status);
    }

    @Override
    public void saveToDatabase(Message message) {
        String sql = "INSERT INTO messages (id, from_user_id, message, date, reply_to_id) VALUES (?, ?, ?, ?, ?)";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getAutoCloseableConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setLong(1, message.getId());
            stmt.setLong(2, message.getFrom().getId());
            stmt.setString(3, message.getMessage());
            stmt.setTimestamp(4, Timestamp.valueOf(message.getDate()));

            if (message instanceof ReplyMessage && ((ReplyMessage) message).getRepliedMessage() != null) {
                stmt.setLong(5, ((ReplyMessage) message).getRepliedMessage().getId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            stmt.executeUpdate();

            saveRecipients(message);

        } catch (SQLException e) {
            throw new RepositoryException("Error saving message to database", e);
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String deleteRecipientsSql = "DELETE FROM message_recipients WHERE message_id = ?";
        String deleteMessageSql = "DELETE FROM messages WHERE id = ?";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getAutoCloseableConnection()) {
            try (PreparedStatement stmt = conn.get().prepareStatement(deleteRecipientsSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.get().prepareStatement(deleteMessageSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error deleting message from database", e);
        }
    }

    @Override
    public void updateFromDatabase(Message message) {
        String sql = "UPDATE messages SET message = ?, date = ?, status = ? WHERE id = ?";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getAutoCloseableConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setString(1, message.getMessage());
            stmt.setTimestamp(2, Timestamp.valueOf(message.getDate()));
            stmt.setString(3, message.getStatus().name());
            stmt.setLong(4, message.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error updating message in database", e);
        }
    }

    private List<User> getRecipientsForMessage(Long messageId) {
        List<User> recipients = new ArrayList<>();
        String sql = "SELECT recipient_id FROM message_recipients WHERE message_id = ?";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getAutoCloseableConnection();
             PreparedStatement statement = conn.get().prepareStatement(sql)) {


            statement.setLong(1, messageId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Long userId = rs.getLong("recipient_id");
                    recipients.add(findOneUserById(userId));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error fetching message recipients", e);
        }
        return recipients;
    }

    private void saveRecipients(Message message) throws SQLException {
        String sql = "INSERT INTO message_recipients (message_id, recipient_id) VALUES (?, ?)";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getAutoCloseableConnection();
             PreparedStatement ps = conn.get().prepareStatement(sql)) {


            for (User recipient : message.getTo()) {
                ps.setLong(1, message.getId());
                ps.setLong(2, recipient.getId());
                ps.executeUpdate();
            }
        }
    }
}