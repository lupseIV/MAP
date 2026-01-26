package org.example.paginarefiltraredb.domain.filters.implementations;

import org.example.paginarefiltraredb.domain.filters.GenericSqlFilter;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.repository.paging.util.Pair;

import java.util.List;

public class ManagerFilter implements SqlFilter {
    private Integer accessLevel;
    private Integer minTeamSize;

    public void setAccessLevel(Integer accessLevel) { this.accessLevel = accessLevel; }
    public void setMinTeamSize(Integer minTeamSize) { this.minTeamSize = minTeamSize; }

    @Override
    public Pair<String, List<Object>> toSql() {
        return GenericSqlFilter.and()
                .eq("access_level", accessLevel)
                .gt("team_size", minTeamSize)
                .toSql();
    }

}