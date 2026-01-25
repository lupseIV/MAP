package map.repository;

import map.domain.Driver;
import map.domain.Order;
import map.domain.Status;
import map.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository extends AbstractDbRepository<Order>{
    
    public OrderRepository(String url, String username, String password, Validator<Order> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Order createEntity(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String clientName = rs.getString("clientname");
        String pickUpAddress = rs.getString("pickupaddress");
        String destinationAddress = rs.getString("destinationaddress");
        LocalDateTime startDate = rs.getTimestamp("startdate").toLocalDateTime();
        LocalDateTime endDate = rs.getTimestamp("enddate") != null
                ? rs.getTimestamp("enddate").toLocalDateTime()
                : null;
        Integer driverId = rs.getInt("id_driver");
        Status status = Status.valueOf(rs.getString("status"));

        Driver driver = null;
        String driverSql = "SELECT * FROM drivers WHERE id = ?";
        try (PreparedStatement driverStatement = rs.getStatement().getConnection().prepareStatement(driverSql)) {
            driverStatement.setInt(1, driverId);
            ResultSet driverResultSet = driverStatement.executeQuery();
            if (driverResultSet.next()) {
                String driverName = driverResultSet.getString("name");
                driver = new Driver(driverName);
                driver.setId(driverId);
            }
            driverResultSet.close();
        }

        Order order = new Order(driver, status, startDate, endDate, pickUpAddress, destinationAddress, clientName);
        order.setId(id);
        return order;
    }

    @Override
    public PreparedStatement findOneStatement(Connection connection, Integer id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        return statement;
    }

    @Override
    public PreparedStatement findAllStatement(Connection connection) throws SQLException {
        String sql = "SELECT * FROM orders";
        return connection.prepareStatement(sql);
    }

    @Override
    public PreparedStatement saveStatement(Connection connection, Order entity) throws SQLException {
        String sql = "INSERT INTO orders (clientname, pickupaddress, destinationaddress, startdate, enddate, id_driver, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, entity.getClientName());
        statement.setString(2, entity.getPickUpAddress());
        statement.setString(3, entity.getDestinationAddress());
        statement.setTimestamp(4, Timestamp.valueOf(entity.getStartDate()));
        statement.setTimestamp(5, entity.getEndDate() != null ? Timestamp.valueOf(entity.getEndDate()) : null);
        if(entity.getDriver() == null)
            statement.setNull(6, Types.INTEGER);
        else
            statement.setInt(6, entity.getDriver().getId());
        statement.setString(7, entity.getStatus().name());
        return statement;
    }

    @Override
    public PreparedStatement deleteStatement(Connection connection, Integer id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        return statement;
    }

    @Override
    public PreparedStatement updateStatement(Connection connection, Order entity) throws SQLException {
        String sql = "UPDATE orders SET clientname = ?, pickupaddress = ?, destinationaddress = ?, startdate = ?, enddate = ?, " +
                "id_driver = ?, status = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, entity.getClientName());
        statement.setString(2, entity.getPickUpAddress());
        statement.setString(3, entity.getDestinationAddress());
        statement.setTimestamp(4, Timestamp.valueOf(entity.getStartDate()));
        statement.setTimestamp(5, entity.getEndDate() != null ? Timestamp.valueOf(entity.getEndDate()) : null);
        statement.setInt(6, entity.getDriver().getId());
        statement.setString(7, entity.getStatus().name());
        statement.setInt(8, entity.getId());
        return statement;
    }

    public List<Order> getOrdersByDriver(Integer id) {

        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE id_driver = ? AND status = 'IN_PROGRESS'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = createEntity(resultSet);
                orders.add(order);
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching orders by driver", e);
        }
        return orders;
    }


    public int getLastInsertedId() {
        
        String query = "SELECT id from orders ORDER BY id DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching last inserted ID", e);
        }
        return -1; // Default fallback value if no ID is found
    }
}
