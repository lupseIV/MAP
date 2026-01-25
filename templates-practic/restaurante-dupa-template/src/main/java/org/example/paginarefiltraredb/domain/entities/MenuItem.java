package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbTable;

@DbTable(name = "menu_items")
public class MenuItem extends Entity<Integer> {

    @DbColumn(nullable = false)
    private String category;

    @DbColumn(nullable = false)
    private String item;

    @DbColumn(nullable = false)
    private Double price;

    @DbColumn(nullable = false)
    private String currency;

    public MenuItem() {}

    public MenuItem(String category, String item, Double price, String currency) {
        this.category = category;
        this.item = item;
        this.price = price;
        this.currency = currency;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public String toString() {
        return item + " - " + price + " " + currency;
    }
}
