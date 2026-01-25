package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.MenuItem;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.entities.OrderStatus;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDbRepository extends EntityDbRepository<Integer, Order> {

    private final MenuItemDbRepository menuItemRepository;

    public OrderDbRepository(Validator<Order> validator, MenuItemDbRepository menuItemRepository) {
        super(validator, "orders", Integer.class);
        this.menuItemRepository = menuItemRepository;
        loadFromDatabase();
    }

    @Override
    public Order extractEntityFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        Integer tableId = rs.getInt("table_id");
        Timestamp dateTs = rs.getTimestamp("order_date");
        LocalDateTime date = dateTs != null ? dateTs.toLocalDateTime() : null;
        String statusStr = rs.getString("status");
        OrderStatus status = OrderStatus.valueOf(statusStr);

        Order order = new Order(tableId, date, status);
        order.setId(id);

        // Load order items
        loadOrderItems(order);

        return order;
    }

    private void loadOrderItems(Order order) {
        String sql = "SELECT menu_item_id FROM order_items WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, order.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                List<Integer> menuItemIds = new ArrayList<>();
                List<MenuItem> menuItems = new ArrayList<>();

                while (rs.next()) {
                    Integer menuItemId = rs.getInt("menu_item_id");
                    menuItemIds.add(menuItemId);

                    // Load the actual MenuItem
                    menuItemRepository.findOne(menuItemId).ifPresent(menuItems::add);
                }

                order.setMenuItemIds(menuItemIds);
                order.setMenuItems(menuItems);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveToDatabase(Order entity) {
        String sql = "INSERT INTO orders (id, table_id, order_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getId());
            stmt.setInt(2, entity.getTableId());
            stmt.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            stmt.setString(4, entity.getStatus().name());
            stmt.executeUpdate();

            // Save order items
            saveOrderItems(entity);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveOrderItems(Order entity) {
        String sql = "INSERT INTO order_items (order_id, menu_item_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Integer menuItemId : entity.getMenuItemIds()) {
                stmt.setInt(1, entity.getId());
                stmt.setInt(2, menuItemId);
                stmt.addBatch();
            }
            stmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Integer id) {
        // First delete order items
        String deleteItems = "DELETE FROM order_items WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteItems)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Then delete the order
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFromDatabase(Order entity) {
        String sql = "UPDATE orders SET table_id=?, order_date=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getTableId());
            stmt.setTimestamp(2, Timestamp.valueOf(entity.getDate()));
            stmt.setString(3, entity.getStatus().name());
            stmt.setInt(4, entity.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Integer generateNewId() {
        String sql = "SELECT nextval('orders_id_seq')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> findByStatus(OrderStatus status) {
        List<Order> orders = new ArrayList<>();
        for (Order order : findAll()) {
            if (order.getStatus() == status) {
                orders.add(order);
            }
        }
        // Sort by date ascending
        orders.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
        return orders;
    }
}
