//package org.example.paginarefiltraredb.repository.database.implementations;
//
//import org.example.paginarefiltraredb.database.DatabaseConnection;
//import org.example.paginarefiltraredb.domain.entities.Entity;
//import org.example.paginarefiltraredb.domain.validation.Validator;
//import org.example.paginarefiltraredb.repository.database.EntityDbRepository;
//
//import java.sql.*;
//
//public class Template extends EntityDbRepository<Long, Entity<Long>> {
//
//    private final String TABLE_NAME = "TABLE_NAME";
//
//    public Template(Validator<Entity<Long>> validator) {
//        super(validator, "TABLE_NAME", Long.class);
//        loadFromDatabase();
//    }
//
//    @Override
//    public Entity<Long> extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
//        return null;
//    }
//
//    @Override
//    public void saveToDatabase(Entity<Long> entity) {
//        String sql = "INSERT INTO " + TABLE_NAME + " +
//                //TODO             "(  campuri din baza de date) " +
//                "VALUES (?,?, ?, ?, ?, ?, ?)";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, );
//            stmt.setString(2, );
//            stmt.setString(3, );
//            stmt.setDouble(4, );
//            stmt.setDate(5, );
//
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void deleteFromDatabase(Long id) {
//        String sql = "DELETE FROM "+ NUME_TABEL +" WHERE id = ?";
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setLong(1, id); // 4. Changed setLong to setInt
//            stmt.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void updateFromDatabase(Entity<Long> entity) {
//        String sql = "UPDATE " + TABLE_NAME + " +
//        //TODO             "SET nume_col = ? ..." ;
//        try (Connection conn = DatabaseConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, );
//            stmt.setString(2, );
//            stmt.setString(3, );
//            stmt.setDouble(4, );
//            stmt.setDate(5, );
//
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
