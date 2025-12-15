package org.domain.dtos.filters;

import org.domain.users.User;
import org.domain.users.relationships.Friendship;
import org.repository.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FriendshipGUIFilter implements SqlFilter {

    private Optional<User> currentUser;

    public void setCurrentUser(Optional<User> currentUser) {
        this.currentUser = currentUser;
    }

    public Optional<User> getCurrentUser() {
        return currentUser;
    }

    @Override
    public Pair<String, List<Object>> toSql() {
        if (currentUser.isEmpty()) {
            return new Pair<>("", Collections.emptyList());
        }
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        currentUser.ifPresent(user -> {
            conditions.add("user1_id = ?");
            params.add(user.getId());
            conditions.add("user2_id = ?");
            params.add(user.getId());
        });

        String sql = String.join(" or ", conditions);
        return new Pair<>(sql, params);
    }
}
