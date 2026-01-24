package org.example.paginarefiltraredb.domain.filters.implementations;

import org.example.paginarefiltraredb.domain.filters.GenericSqlFilter;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.repository.paging.util.Pair;

import java.util.List;

public class StaffFilter implements SqlFilter {
    private String searchJson; // Generic search box input
    private String department;

    public void setSearchJson(String searchJson) { this.searchJson = searchJson; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public Pair<String, List<Object>> toSql() {
        GenericSqlFilter root = GenericSqlFilter.and();

        // 1. Exact match for Department
        root.eq("department", department);

        // 2. OR Logic: (name LIKE %val% OR email LIKE %val%)
        if (searchJson != null && !searchJson.isBlank()) {
            GenericSqlFilter orClause = GenericSqlFilter.or()
                    .like("name", searchJson)
                    .like("email", searchJson);

            root.add(orClause);
        }

        return root.toSql();
    }
}