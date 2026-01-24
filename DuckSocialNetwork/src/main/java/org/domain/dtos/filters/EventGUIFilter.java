package org.domain.dtos.filters;

import org.repository.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class EventGUIFilter implements SqlFilter{
    @Override
    public Pair<String, List<Object>> toSql() {
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        String sql = String.join(" and ", conditions);
        return new Pair<>(sql, params);
    }
}
