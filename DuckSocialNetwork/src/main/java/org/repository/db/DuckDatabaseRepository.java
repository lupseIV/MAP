package org.repository.db;

import database.DatabaseConnection;
import org.domain.dtos.DuckData;
import org.domain.exceptions.RepositoryException;
import org.domain.users.duck.Duck;
import org.domain.users.duck.DuckFactory;
import org.domain.validators.Validator;
import org.utils.enums.types.DuckTypes;

import java.sql.*;

import java.util.List;

public class DuckDatabaseRepository extends EntityDatabaseRepository<Long, Duck> {

    DuckFactory duckFactory = null;

    public DuckDatabaseRepository(Validator<Duck> validator) {
        super(validator, "SELECT * FROM ducks");
    }

    @Override
    public Duck extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        if(duckFactory == null) duckFactory = new DuckFactory();

        Long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String email = resultSet.getString("email");
        DuckTypes type = DuckTypes.valueOf(resultSet.getString("duck_type"));
        Double speed =  resultSet.getDouble("speed");
        Double rezistance = resultSet.getDouble("rezistance");

        List<String> dataAttributes = List.of(
                username, password, email,
                Double.toString(speed), Double.toString(rezistance)
        );

        DuckData data = new DuckData(dataAttributes);
        Duck duck = duckFactory.create(type, data);
        duck.setId(id);

        return duck;
    }

    @Override
    public void saveToDatabase(Duck duck) {
        String sql = """
            INSERT INTO ducks (id, username, password, email, duck_type, speed, rezistance)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, duck.getId());
            stmt.setString(2, duck.getUsername());
            stmt.setString(3, duck.getPassword());
            stmt.setString(4, duck.getEmail());
            stmt.setString(5, duck.getDuckType().name());
            stmt.setDouble(6, duck.getSpeed());
            stmt.setDouble(7, duck.getRezistance());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error saving duck to database", e);
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String sql = "DELETE FROM ducks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting duck from database", e);
        }
    }

    @Override
    public void updateFromDatabase(Duck duck) {
        String sql = """
            UPDATE ducks 
            SET username = ?, password = ?, email = ?, duck_type = ?, speed = ?, rezistance = ?
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, duck.getUsername());
            stmt.setString(2, duck.getPassword());
            stmt.setString(3, duck.getEmail());
            stmt.setString(4, duck.getDuckType().name());
            stmt.setDouble(5, duck.getSpeed());
            stmt.setDouble(6, duck.getRezistance());
            stmt.setLong(7, duck.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error updating duck in database", e);
        }
    }
}
