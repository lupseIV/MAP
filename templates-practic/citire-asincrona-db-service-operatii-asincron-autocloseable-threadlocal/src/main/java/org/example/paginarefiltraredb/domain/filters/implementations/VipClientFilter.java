package org.example.paginarefiltraredb.domain.filters.implementations;

import org.example.paginarefiltraredb.domain.filters.GenericSqlFilter;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.repository.paging.util.Pair;

import java.util.List;

public class VipClientFilter implements SqlFilter {
    private Integer minLoyaltyPoints;
    private Long managerId; // Filter VIPs belonging to a specific manager

    public void setMinLoyaltyPoints(Integer minLoyaltyPoints) { this.minLoyaltyPoints = minLoyaltyPoints; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    @Override
    public Pair<String, List<Object>> toSql() {
        return GenericSqlFilter.and()
                .gt("loyalty_points", minLoyaltyPoints)
                .eq("personalManager_id", managerId) // Note: Use the DB column name, not Java field name
                .toSql();
    }
}