package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.MenuItem;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;

public class MenuItemDbRepository extends EntityDbRepository<Integer, MenuItem> {

    public MenuItemDbRepository(Validator<MenuItem> validator) {
        super(validator, "menu_items", Integer.class);
        loadFromDatabase();
    }

    @Override
    public MenuItem extractEntityFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String category = rs.getString("category");
        String item = rs.getString("item");
        Double price = rs.getDouble("price");
        String currency = rs.getString("currency");

        MenuItem menuItem = new MenuItem(category, item, price, currency);
        menuItem.setId(id);
        return menuItem;
    }

    @Override
    public void saveToDatabase(MenuItem entity) {
        String sql = "INSERT INTO menu_items (id, category, item, price, currency) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getId());
            stmt.setString(2, entity.getCategory());
            stmt.setString(3, entity.getItem());
            stmt.setDouble(4, entity.getPrice());
            stmt.setString(5, entity.getCurrency());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Integer id) {
        String sql = "DELETE FROM menu_items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFromDatabase(MenuItem entity) {
        String sql = "UPDATE menu_items SET category=?, item=?, price=?, currency=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getCategory());
            stmt.setString(2, entity.getItem());
            stmt.setDouble(3, entity.getPrice());
            stmt.setString(4, entity.getCurrency());
            stmt.setInt(5, entity.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Integer generateNewId() {
        String sql = "SELECT nextval('menu_items_id_seq')";

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
}
