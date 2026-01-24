package org.example.paginarefiltraredb.domain.filters.implementations;

import org.example.paginarefiltraredb.domain.filters.GenericSqlFilter;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.repository.paging.util.Pair;

import java.util.List;

public class ClientFilter implements SqlFilter {
    private String nameSearch;
    private String type;
    private Double minBudget;

    // Setters for UI binding
    public void setNameSearch(String nameSearch) { this.nameSearch = nameSearch; }
    public void setType(String type) { this.type = type; }
    public void setMinBudget(Double minBudget) { this.minBudget = minBudget; }

    @Override
    public Pair<String, List<Object>> toSql() {
        // Use GenericSqlFilter to build the query dynamically
        return GenericSqlFilter.and()
                .like("full_name", nameSearch)  // DB Column: full_name
                .eq("client_type", type)        // DB Column: client_type
                .gt("budget", minBudget)        // DB Column: budget
                .toSql();
    }
}