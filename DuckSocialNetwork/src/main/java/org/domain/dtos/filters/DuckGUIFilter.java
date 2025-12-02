package org.domain.dtos.filters;

import org.repository.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DuckGUIFilter implements SqlFilter {

    public void setDuckType(Optional<String> duckType) {
        this.duckType = duckType;
    }

    private  Optional<String> duckType;

    public DuckGUIFilter(Optional<String> duckType) {
        this.duckType = duckType;
    }

    public Optional<String> getDuckType() {
        return duckType;
    }

    @Override
    public Pair<String, List<Object>> toSql() {
        if (duckType.isEmpty()) {
            return new Pair<>("", Collections.emptyList());
        }
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        duckType.ifPresent(typeFilter -> {
            conditions.add("duck_type = ?");
            params.add(typeFilter);
        });

        String sql = String.join(" and ", conditions);
        return new Pair<>(sql, params);
    }
}
