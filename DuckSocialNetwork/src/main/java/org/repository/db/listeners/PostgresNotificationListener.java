package org.repository.db;

import database.DatabaseConnection;
import org.domain.Observer;
import org. domain.Observable;
import org. domain.events.MessageEvent;
import org.domain.users.relationships. messages.Message;
import javafx.application.Platform;
import org.postgresql.PGConnection;
import org. postgresql.PGNotification;
import org.service.MessageService;
import org.utils.enums.MessageType;

import java.sql.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util. concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostgresNotificationListener implements Observable<MessageEvent, Observer<MessageEvent>> {

    private final List<Observer<MessageEvent>> observers = new CopyOnWriteArrayList<>();
    private Connection listenerConnection;
    private final ExecutorService listenerThread = Executors. newSingleThreadExecutor();
    private volatile boolean running = true;
    private final Long currentUserId;

    private MessageService messageService;

    public PostgresNotificationListener(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void startListening() {
        listenerThread.submit(() -> {
            try {
                // Create DEDICATED connection for listening
                listenerConnection = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/postgres",
                        "postgres",
                        "141105"
                );

                try (Statement stmt = listenerConnection.createStatement()) {
                    stmt. execute("LISTEN new_message");
                }

                PGConnection pgConnection = listenerConnection.unwrap(PGConnection. class);

                while (running) {
                    PGNotification[] notifications = pgConnection. getNotifications(2000);

                    if (notifications != null) {
                        for (PGNotification notification : notifications) {
                            handleNotification(notification. getParameter());
                        }
                    }
                }
            } catch (SQLException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleNotification(String payload) {
        try {
            System.out. println("=== RECEIVED NOTIFICATION ===");
            System.out.println("Payload: " + payload);
            System.out.println("=============================");
            Long messageId = extractLong(payload, "id");
            Long fromUserId = extractLong(payload, "from_user_id");
            String content = extractString(payload, "content");

            Message message = messageService.findOne(messageId);

            if (message != null && isRelevantToCurrentUser(message)) {
                MessageEvent event = new MessageEvent(MessageType.NEW_MESSAGE, List.of(message));
                notifyObservers(event);
            }

        } catch (Exception e) {
            e. printStackTrace();
        }
    }

    private boolean isRelevantToCurrentUser(Message message) {
        if (message.getFrom().getId().equals(currentUserId)) {
            return true;
        }
        return message.getTo().stream()
                .anyMatch(user -> user.getId().equals(currentUserId));
    }

    public void stopListening() {
        running = false;
        listenerThread.shutdown();
        try {
            if (listenerConnection != null && !listenerConnection. isClosed()) {
                listenerConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addObserver(Observer<MessageEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<MessageEvent> observer) {
        observers. remove(observer);
    }

    @Override
    public void notifyObservers(MessageEvent event) {
        for (Observer<MessageEvent> observer : observers) {
            Platform.runLater(() -> observer.update(event));
        }
    }

    private Long extractLong(String json, String key) {
        try {
            // ✅ Pattern handles optional spaces around colon
            // Matches: "key" : 123 or "key":  123 or "key":123
            String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
            Matcher matcher = Pattern.compile(pattern).matcher(json);

            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Failed to parse Long for key: " + key);
        }
        return null;
    }

    private String extractString(String json, String key) {
        try {
            // ✅ Pattern handles optional spaces and captures quoted string
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"";
            Matcher matcher = Pattern.compile(pattern).matcher(json);

            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to parse String for key: " + key);
        }
        return null;
    }
}