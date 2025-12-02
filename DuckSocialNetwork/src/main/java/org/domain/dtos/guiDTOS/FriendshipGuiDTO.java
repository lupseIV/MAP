package org.domain.dtos.guiDTOS;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FriendshipGuiDTO {

    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty user1Id = new SimpleLongProperty();
    private final StringProperty user1Username = new SimpleStringProperty();
    private final LongProperty user2Id = new SimpleLongProperty();
    private final StringProperty user2Username = new SimpleStringProperty();

    public FriendshipGuiDTO() { }

    public FriendshipGuiDTO(Long id, Long user1Id, String user1Username, Long user2Id, String user2Username) {
        this.id.set(id);
        this.user1Id.set(user1Id);
        this.user1Username.set(user1Username);
        this.user2Id.set(user2Id);
        this.user2Username.set(user2Username);
    }

    public LongProperty idProperty() { return id; }
    public LongProperty user1IdProperty() { return user1Id; }
    public StringProperty user1UsernameProperty() { return user1Username; }
    public LongProperty user2IdProperty() { return user2Id; }
    public StringProperty user2UsernameProperty() { return user2Username; }

    public Long getId() { return id.get(); }
    public Long getUser1Id() { return user1Id.get(); }
    public String getUser1Username() { return user1Username.get(); }
    public Long getUser2Id() { return user2Id.get(); }
    public String getUser2Username() { return user2Username.get(); }

    public void setId(Long id) { this.id.set(id); }
    public void setUser1Id(Long user1Id) { this.user1Id.set(user1Id); }
    public void setUser1Username(String user1Username) { this.user1Username.set(user1Username); }
    public void setUser2Id(Long user2Id) { this.user2Id.set(user2Id); }
    public void setUser2Username(String user2Username) { this.user2Username.set(user2Username); }

    @Override
    public String toString() {
        return "FriendshipGuiDTO{" +
                "id=" + getId() +
                ", user1Id=" + getUser1Id() +
                ", user1Username=" + getUser1Username() +
                ", user2Id=" + getUser2Id() +
                ", user2Username=" + getUser2Username() +
                '}';
    }
}
