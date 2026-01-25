package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.RestaurantTable;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;

public class RestaurantTableDbRepository extends EntityDbRepository<Integer, RestaurantTable> {

    public RestaurantTableDbRepository(Validator<RestaurantTable> validator) {
        super(validator, "tables", Integer.class);
        loadFromDatabase();
    }

    @Override
    public RestaurantTable extractEntityFromResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        RestaurantTable table = new RestaurantTable(id);
        return table;
    }

    @Override
    public void saveToDatabase(RestaurantTable entity) {
        String sql = "INSERT INTO tables (id) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Integer id) {
        String sql = "DELETE FROM tables WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFromDatabase(RestaurantTable entity) {
        // No additional fields to update for RestaurantTable
    }

    @Override
    protected Integer generateNewId() {
        String sql = "SELECT nextval('tables_id_seq')";

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
