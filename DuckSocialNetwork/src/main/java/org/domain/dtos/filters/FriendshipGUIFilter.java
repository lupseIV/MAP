package org.domain.dtos.filters;

import org.domain.users.relationships.Friendship;
import org.repository.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class FriendshipGUIFilter implements SqlFilter {
    @Override
    public Pair<String, List<Object>> toSql() {
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String sql = String.join(" and ", conditions);
        return new Pair<>(sql, params);
    }
}
