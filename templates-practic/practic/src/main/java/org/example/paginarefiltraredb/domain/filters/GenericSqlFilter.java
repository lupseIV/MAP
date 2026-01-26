package org.example.paginarefiltraredb.domain.filters;

import org.example.paginarefiltraredb.repository.paging.util.Pair;

import java.util.List;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GenericSqlFilter implements SqlFilter {

    private final List<String> conditions = new ArrayList<>();
    private final List<Object> params = new ArrayList<>();
    private String separator = " AND "; // Default

    public static GenericSqlFilter and() {
        GenericSqlFilter filter = new GenericSqlFilter();
        filter.separator = " AND ";
        return filter;
    }

    public static GenericSqlFilter or() {
        GenericSqlFilter filter = new GenericSqlFilter();
        filter.separator = " OR ";
        return filter;
    }

    public GenericSqlFilter eq(String column, Object value) {
        if (value != null) {
            conditions.add(column + " = ?");
            params.add(value);
        }
        return this;
    }

    public GenericSqlFilter like(String column, String value) {
        if (value != null && !value.isBlank()) {
            conditions.add(column + " LIKE ?");
            params.add("%" + value + "%");
        }
        return this;
    }

    public GenericSqlFilter gt(String column, Object value) {
        if (value != null) {
            conditions.add(column + " > ?");
            params.add(value);
        }
        return this;
    }

    public GenericSqlFilter add(SqlFilter subFilter) {
        Pair<String, List<Object>> result = subFilter.toSql();
        if (!result.getFirst().isBlank()) {
            //  ( condition OR condition )
            conditions.add("(" + result.getFirst() + ")");
            params.addAll(result.getSecond());
        }
        return this;
    }

    @Override
    public Pair<String, List<Object>> toSql() {
        if (conditions.isEmpty()) {
            return new Pair<>("", Collections.emptyList());
        }
        String sql = String.join(separator, conditions);
        return new Pair<>(sql, params);
    }



    //    public void exampleUsage(Long currentUserId, FriendRequestStatus statusEnum) {
//
//        // 1. Build the complex "OR" part first: (user1_id = ? OR user2_id = ?)
//        GenericSqlFilter userFilter = GenericSqlFilter.or()
//                .eq("user1_id", currentUserId)
//                .eq("user2_id", currentUserId);
//
//        // 2. Build the main "AND" query, adding the "OR" part as a sub-clause
//        GenericSqlFilter mainFilter = GenericSqlFilter.and()
//                .add(userFilter)                  // Adds the (u1 OR u2) logic
//                .eq("status", statusEnum.name()); // Adds AND status = ?
//
//        // 3. Generate SQL
//        Pair<String, List<Object>> sqlResult = mainFilter.toSql();
//
//        System.out.println("SQL: " + sqlResult.getKey());
//        System.out.println("Params: " + sqlResult.getValue());
//    }
}