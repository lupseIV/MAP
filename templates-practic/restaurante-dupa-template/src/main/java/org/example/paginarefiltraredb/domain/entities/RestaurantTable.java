package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbTable;

@DbTable(name = "tables")
public class RestaurantTable extends Entity<Integer> {

    public RestaurantTable() {}

    public RestaurantTable(Integer id) {
        setId(id);
    }

    @Override
    public String toString() {
        return "Table " + getId();
    }
}
