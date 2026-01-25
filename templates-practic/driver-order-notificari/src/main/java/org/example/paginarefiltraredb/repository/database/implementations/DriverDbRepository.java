package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.entities.Driver;
import org.example.paginarefiltraredb.domain.exceptions.RepositoryException;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DriverDbRepository extends EntityDbRepository<Integer, Driver> {

    public DriverDbRepository(Validator<Driver> validator) {
        super(validator, "drivers", Integer.class);
        loadFromDatabase();
    }

    @Override
    public Driver extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("id");
        String name = resultSet.getString("name");

        Driver driver = new Driver(name);
        driver.setId(id);

        return driver;
    }

    @Override
    public void saveToDatabase(Driver entity) {
        String sql = "INSERT INTO drivers(id, name)" +
                "VALUES (?,?)";
        try(DatabaseConnection.AutoCloseableConnection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.get().prepareStatement(sql)){

            stmt.setInt(1,entity.getId());
            stmt.setString(2, entity.getName());

            stmt.execute();

        }catch (SQLException e){
            throw new RepositoryException("Error saving to database" + e.getMessage());
        }
    }

    @Override
    public void deleteFromDatabase(Integer integer) {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try(DatabaseConnection.AutoCloseableConnection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.get().prepareStatement(sql)){

            stmt.setInt(1,integer);

            stmt.execute();

        }catch (SQLException e){
            throw new RepositoryException("Error deleting from database" + e.getMessage());
        }
    }

    @Override
    public void updateFromDatabase(Driver entity) {
        String sql = "UPDATE drivers " +
                "SET name = ? " +
                "WHERE id = ?";
        try(DatabaseConnection.AutoCloseableConnection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.get().prepareStatement(sql)){

            stmt.setString(1, entity.getName());
            stmt.setInt(2,entity.getId());
            stmt.execute();

        }catch (SQLException e){
            throw new RepositoryException("Error updating in database" + e.getMessage());
        }
    }
}
