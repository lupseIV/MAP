# Real-Time Chat Implementation

## Overview

The chat functionality has been enhanced to support real-time messaging between multiple application instances running simultaneously on the same or different computers.

## Architecture - MVC with Observer Pattern

The implementation follows the **Model-View-Controller (MVC)** pattern with the **Observer** design pattern for real-time updates:

### Components

1. **Model** (`MessageService`)
   - Implements `Observable<Observer>` interface
   - Manages message data and business logic
   - Notifies all registered observers when new messages are sent
   - Maintains a list of observers (chat windows)

2. **View** (`ChatView.fxml` and message display)
   - FXML-based user interface
   - ListView displays messages with custom cell rendering
   - Different styling for sent/received messages

3. **Controller** (`ChatController`)
   - Implements `Observer` interface
   - Handles user interactions (send, reply)
   - Registers with MessageService to receive updates
   - Auto-refreshes every 2 seconds to check for new messages

## How It Works

### Observer Pattern Implementation

```java
// MessageService (Observable)
public class MessageService implements Observable<Observer> {
    private List<Observer> observers = new ArrayList<>();
    
    public void sendMessage(...) {
        // Save message to database
        save(message);
        // Notify all observers
        notifyObservers();
    }
}

// ChatController (Observer)
public class ChatController implements Observer {
    @Override
    public void update() {
        // Reload messages when notified
        loadMessages();
    }
}
```

### Dual Update Mechanism

The chat updates through **two complementary mechanisms**:

1. **Observer Notifications** (Immediate)
   - When a user sends a message, `MessageService.sendMessage()` calls `notifyObservers()`
   - All registered `ChatController` instances receive the `update()` call
   - Messages appear immediately in the sender's chat window

2. **Periodic Polling** (Every 2 seconds)
   - A background `Timer` checks for new messages every 2 seconds
   - Ensures messages are received even if observer notification fails
   - Handles cases where the chat was opened before a message was sent

### Thread Safety

- Uses `Platform.runLater()` to update JavaFX UI from background threads
- Timer runs as a daemon thread (won't prevent app shutdown)
- Prevents unnecessary UI updates by tracking message count

## Testing with Multiple Instances

### In IntelliJ IDEA

1. **Configure Multiple Instances:**
   - Go to `Run → Edit Configurations`
   - Select your run configuration (e.g., `GuiLauncher`)
   - Check the box: ✓ `Allow multiple instances`
   - Click `OK`

2. **Run Multiple Instances:**
   - Click Run (green play button) for the first instance
   - Click Run again for the second instance
   - Both applications will start independently

3. **Test Real-Time Chat:**
   - Log in as User A in the first instance
   - Log in as User B in the second instance
   - Open chat between them in both windows
   - Send messages from either user
   - Messages appear in both chat windows within 2 seconds

## Features

✅ **Real-time Updates**: Messages appear automatically without manual refresh  
✅ **Multi-Instance Support**: Works with multiple app instances on same/different computers  
✅ **Shared Database**: All instances connect to the same database  
✅ **Observer Pattern**: Clean separation of concerns following MVC  
✅ **Efficient Updates**: Only updates UI when message count changes  
✅ **Thread-Safe**: Proper synchronization with JavaFX UI thread  

## Database Synchronization

Both instances share the same PostgreSQL database:
- Messages are stored in the `messages` table
- Recipients are tracked in the `message_recipients` junction table
- Each instance polls the database every 2 seconds
- New messages from any instance are immediately visible to all users

## Technical Details

### Auto-Refresh Timer

```java
private void startAutoRefresh() {
    refreshTimer = new Timer(true); // Daemon thread
    refreshTimer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
            loadMessages();
        }
    }, 0, 2000); // Initial delay: 0ms, Period: 2000ms
}
```

### Message Loading Optimization

```java
private void loadMessages() {
    List<Message> conversation = messageService.getConversation(currentUser, chatPartner);
    
    // Only update if message count changed
    if (conversation.size() != lastMessageCount) {
        lastMessageCount = conversation.size();
        Platform.runLater(() -> {
            messageListView.setItems(FXCollections.observableArrayList(conversation));
            messageListView.scrollTo(conversation.size() - 1);
        });
    }
}
```

## Cleanup

The `ChatController.cleanup()` method properly releases resources:
- Cancels the refresh timer
- Unregisters from the MessageService observers

This should be called when the chat window is closed to prevent memory leaks.

## Future Enhancements

Potential improvements:
- WebSocket or Server-Sent Events for truly push-based updates
- Reduce polling interval or implement smart polling
- Add typing indicators
- Show message delivery/read receipts
- Implement message encryption for privacy
