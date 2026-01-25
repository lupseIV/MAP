package map.repository;

import map.domain.Driver;
import map.domain.Order;
import map.domain.validators.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DriverRepository extends AbstractDbRepository<Driver>{

    public DriverRepository(String url, String username, String password, Validator<Driver> validator) {
        super(url, username, password, validator);
    }

    @Override
    public Driver createEntity(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Driver driver = new Driver(name);
        driver.setId(id);
        return driver;
    }

    @Override
    public PreparedStatement findOneStatement(Connection connection, Integer id) throws SQLException {
        String query = "SELECT * FROM drivers WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        return statement;
    }

    @Override
    public PreparedStatement findAllStatement(Connection connection) throws SQLException {
        String query = "SELECT * FROM drivers";
        return connection.prepareStatement(query);
    }

    @Override
    public PreparedStatement saveStatement(Connection connection, Driver driver) throws SQLException {
        String query = "INSERT INTO drivers (name) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setString(1, driver.getName());
        return statement;
    }

    @Override
    public PreparedStatement deleteStatement(Connection connection, Integer id) throws SQLException {
        String query = "DELETE FROM drivers WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        return statement;
    }

    @Override
    public PreparedStatement updateStatement(Connection connection, Driver driver) throws SQLException {
        String query = "UPDATE drivers SET name = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, driver.getName());
        statement.setInt(2, driver.getId());
        return statement;
    }


}
