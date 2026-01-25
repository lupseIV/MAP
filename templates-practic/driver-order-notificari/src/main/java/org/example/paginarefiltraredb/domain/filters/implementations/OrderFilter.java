package org.example.paginarefiltraredb.domain.filters.implementations;

import org.example.paginarefiltraredb.domain.filters.GenericSqlFilter;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.repository.paging.util.Pair;

import java.util.List;

public class OrderFilter implements SqlFilter {
    private Integer driverId;
    private String status;

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Pair<String, List<Object>> toSql() {
        // Use GenericSqlFilter to build the query dynamically
        return GenericSqlFilter.and()
                .eq("status", status)
                .eq("driverid_id", driverId)
                .toSql();
    }
}
