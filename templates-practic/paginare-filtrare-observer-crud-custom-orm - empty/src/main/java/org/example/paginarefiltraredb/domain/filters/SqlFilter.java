package org.example.paginarefiltraredb.domain.filters;

import org.example.paginarefiltraredb.repository.paging.util.Pair;

import java.util.List;
import java.util.function.Predicate;

public interface SqlFilter  {
    Pair<String, List<Object>> toSql();
}