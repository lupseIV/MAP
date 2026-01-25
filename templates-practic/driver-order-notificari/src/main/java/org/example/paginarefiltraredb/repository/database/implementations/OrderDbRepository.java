package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.entities.enums.OrderStatus;
import org.example.paginarefiltraredb.domain.exceptions.RepositoryException;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;
import java.time.LocalDateTime;

public class OrderDbRepository extends EntityDbRepository<Integer, Order> {

    public OrderDbRepository(Validator<Order> validator) {
        super(validator, "orders", Integer.class);
        loadFromDatabase();
    }

    @Override
    public Order extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String statusStr = resultSet.getString("status");
        OrderStatus status = statusStr != null ? OrderStatus.valueOf(statusStr) : null;

        // Safely handle nullable TIMESTAMP columns to avoid NPE when converting
        Timestamp startTs = resultSet.getTimestamp("start_date");
        LocalDateTime startDate = startTs != null ? startTs.toLocalDateTime() : null;
        Timestamp endTs = resultSet.getTimestamp("end_date");
        LocalDateTime endDate = endTs != null ? endTs.toLocalDateTime() : null;

        String pickUpAddress = resultSet.getString("pick_up_address");
        String destinationAddress = resultSet.getString("destination_address");
        String clienName = resultSet.getString("client_name");
        Integer driverId =  resultSet.getInt("driverid_id");


        Order order = new Order(driverId, status, startDate, endDate, pickUpAddress, clienName,destinationAddress);
        order.setId(id);

        return order;
    }

    @Override
    public void saveToDatabase(Order entity) {
        String sql = "INSERT INTO orders(id, client_name, pick_up_address,start_date,end_date,status,driverid_id,destination_address)" +
                "VALUES (?,?,?,?,?,?,?,?)";
        try(DatabaseConnection.AutoCloseableConnection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.get().prepareStatement(sql)){

            stmt.setInt(1,entity.getId());
            stmt.setString(2, entity.getClientName());
            stmt.setString(3, entity.getPickUpAddress());
            stmt.setTimestamp(4, Timestamp.valueOf(entity.getStartDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(entity.getEndDate()));
            stmt.setString(6, entity.getStartDate().toString());
            stmt.setInt(7, entity.getDriverId());
            stmt.setString(8,entity.getDestinationAddress());

            stmt.execute();

        }catch (SQLException e){
            throw new RepositoryException("Error saving to database" + e.getMessage());
        }
    }

    @Override
    public void deleteFromDatabase(Integer aLong) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try(DatabaseConnection.AutoCloseableConnection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.get().prepareStatement(sql)){

            stmt.setInt(1,aLong);

            stmt.execute();

        }catch (SQLException e){
            throw new RepositoryException("Error deleting from database" + e.getMessage());
        }
    }

    @Override
    public void updateFromDatabase(Order entity) {
        String sql = "UPDATE orders " +
                "SET client_name = ?, pick_up_address = ?, end_date = ?, start_date = ?, status = ?, driverid_id = ?, destination_address = ? " +
                "WHERE id = ?";
        try(DatabaseConnection.AutoCloseableConnection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.get().prepareStatement(sql)){

            stmt.setString(1, entity.getClientName());
            stmt.setString(2, entity.getPickUpAddress());
            stmt.setTimestamp(3, Timestamp.valueOf(entity.getStartDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(entity.getEndDate()));
            stmt.setString(5, entity.getStartDate().toString());
            stmt.setInt(6, entity.getDriverId());
            stmt.setString(7, entity.getDestinationAddress());

            stmt.setInt(8,entity.getId());

            stmt.execute();

        }catch (SQLException e){
            throw new RepositoryException("Error updating in database" + e.getMessage());
        }
    }
}
