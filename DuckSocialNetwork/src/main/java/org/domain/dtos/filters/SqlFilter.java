package org.domain.dtos.filters;

import org.repository.util.Pair;

import java.util.List;

public interface SqlFilter {
    Pair<String, List<Object>> toSql();
}
