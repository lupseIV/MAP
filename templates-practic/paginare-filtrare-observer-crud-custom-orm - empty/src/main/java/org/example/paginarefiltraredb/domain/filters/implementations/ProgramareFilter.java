package org.example.paginarefiltraredb.domain.filters.implementations;

import org.example.paginarefiltraredb.domain.filters.GenericSqlFilter;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.repository.paging.util.Pair;

import java.util.List;

public class ProgramareFilter implements SqlFilter {
    private String status;
    private Long idMedic;
    // Setters for UI binding
    public void setType(String type) { this.status = type; }

    public void setIdMedic(Long idMedic) {
        this.idMedic = idMedic;
    }

    @Override
    public Pair<String, List<Object>> toSql() {
        // Use GenericSqlFilter to build the query dynamically
        return GenericSqlFilter.and()
                .like("status", status)        // DB Column: client_type
                .eq("id_medic", idMedic)
                .toSql();
    }


}