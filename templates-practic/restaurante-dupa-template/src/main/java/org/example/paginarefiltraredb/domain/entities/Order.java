package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbTable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DbTable(name = "orders")
public class Order extends Entity<Integer> {

    @DbColumn(name = "table_id", nullable = false)
    private Integer tableId;

    @DbColumn(name = "order_date", nullable = false)
    private LocalDateTime date;

    @DbColumn(nullable = false)
    private OrderStatus status;

    // List of menu item IDs (not persisted directly, loaded via join table)
    private List<Integer> menuItemIds = new ArrayList<>();

    // For display purposes - loaded menu items
    private List<MenuItem> menuItems = new ArrayList<>();

    public Order() {}

    public Order(Integer tableId, LocalDateTime date, OrderStatus status) {
        this.tableId = tableId;
        this.date = date;
        this.status = status;
    }

    public Integer getTableId() { return tableId; }
    public void setTableId(Integer tableId) { this.tableId = tableId; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public List<Integer> getMenuItemIds() { return menuItemIds; }
    public void setMenuItemIds(List<Integer> menuItemIds) { this.menuItemIds = menuItemIds; }

    public List<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }

    public void addMenuItem(MenuItem item) {
        if (item.getId() != null) {
            menuItemIds.add(item.getId());
        }
        menuItems.add(item);
    }

    public String getMenuItemsAsString() {
        if (menuItems == null || menuItems.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < menuItems.size(); i++) {
            sb.append(menuItems.get(i).getItem());
            if (i < menuItems.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + getId() +
                ", tableId=" + tableId +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}
