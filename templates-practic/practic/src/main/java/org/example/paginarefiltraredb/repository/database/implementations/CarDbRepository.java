package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Car;
import org.example.paginarefiltraredb.domain.entities.CarStatus;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDbRepository extends EntityDbRepository<Long, Car> {

    private static final String TABLE_NAME = "cars";

    public CarDbRepository(Validator<Car> validator) {
        super(validator, TABLE_NAME, Long.class);
        loadFromDatabase();
    }

    @Override
    public Car extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        Double basePrice = resultSet.getDouble("base_price");
        String statusStr = resultSet.getString("status");
        CarStatus status = CarStatus.valueOf(statusStr);
        String comments = resultSet.getString("comments");
        String rejectionReason = resultSet.getString("rejection_reason");

        Car car = new Car(name, description, basePrice);
        car.setId(id);
        car.setStatus(status);
        car.setComments(comments);
        car.setRejectionReason(rejectionReason);
        return car;
    }

    @Override
    public void saveToDatabase(Car entity) {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (id, name, description, base_price, status, comments, rejection_reason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, entity.getId());
            stmt.setString(2, entity.getName());
            stmt.setString(3, entity.getDescription());
            stmt.setDouble(4, entity.getBasePrice());
            stmt.setString(5, entity.getStatus().name());
            stmt.setString(6, entity.getComments());
            stmt.setString(7, entity.getRejectionReason());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateFromDatabase(Car entity) {
        String sql = "UPDATE " + TABLE_NAME +
                " SET name = ?, description = ?, base_price = ?, status = ?, comments = ?, rejection_reason = ? " +
                "WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getDescription());
            stmt.setDouble(3, entity.getBasePrice());
            stmt.setString(4, entity.getStatus().name());
            stmt.setString(5, entity.getComments());
            stmt.setString(6, entity.getRejectionReason());
            stmt.setLong(7, entity.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Car> findByStatus(CarStatus status) {
        List<Car> result = new ArrayList<>();
        for (Car car : findAll()) {
            if (car.getStatus() == status) {
                result.add(car);
            }
        }
        return result;
    }
}

