package org.domain.users;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.domain.Entity;
import org.utils.enums.types.UserTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = User.class
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
public abstract class User extends Entity<Long>  {
    private String username;
    private String password;
    private String email;
    private List<User> friends;
    private UserTypes userType;

    public User() {
    }

    public User(String username, String password, String email, UserTypes userType ) {

        this.username = username;
        this.password = password;
        this.email = email;
        this.friends = new ArrayList<>();
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<User> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return super.toString()+"User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, email, friends);
    }

    public abstract void login();
    public abstract void logout();
    public abstract void sendMessage();
    public abstract void receiveMessage();
    public abstract void update();

    public void addFriend(User user) {
        if (user == null) return;
        if (friends == null) friends = new ArrayList<>();
        if (!friends.contains(user)) {
            friends.add(user);
            user.addFriend(this);
        }
    }

    public void removeFriend(User user) {
        if (user == null || friends == null) return;
        if (friends.contains(user)) {
            friends.remove(user);
            user.removeFriend(this);
        }
    }


    public UserTypes getUserType() {
        return userType;
    }
}
