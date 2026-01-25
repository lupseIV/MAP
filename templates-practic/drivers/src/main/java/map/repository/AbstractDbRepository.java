package map.repository;


import map.domain.Entity;
import map.domain.validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDbRepository<E extends Entity<Integer>> implements Repository<Integer, E> {
    protected final String url;
    protected final String username;
    protected final String password;
    private Validator<E> validator;

    public AbstractDbRepository(String url, String username, String password, Validator<E> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    public abstract E createEntity(ResultSet rs) throws SQLException;
    public abstract PreparedStatement findOneStatement(Connection connection, Integer id) throws SQLException;
    public abstract PreparedStatement findAllStatement(Connection connection) throws SQLException;
    public abstract PreparedStatement saveStatement(Connection connection, E entity) throws SQLException;
    public abstract PreparedStatement deleteStatement(Connection connection, Integer id) throws SQLException;
    public abstract PreparedStatement updateStatement(Connection connection, E entity) throws SQLException;

    @Override
    public Optional<E> findOne(Integer id) {
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = findOneStatement(connection, id); //connection.prepareStatement("SELECT * FROM users WHERE id = ?");

            ResultSet rs = statement.executeQuery();
            rs.next();

            E entity = createEntity(rs);

            statement.close();
            rs.close();
            connection.close();

            return Optional.of(entity);
        }
        catch (SQLException e){
            return Optional.empty();
        }
    }

    @Override
    public List<E> findAll() {
        List<E> entities = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(url, username, password)){
            PreparedStatement statement = findAllStatement(connection); //connection.prepareStatement("SELECT * FROM friendships");
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                E entity = createEntity(resultSet);
                entities.add(entity);
            }
            statement.close();
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return entities;
    }

    @Override
    public Optional<E> save(E entity) {
        validator.validate(entity);

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = saveStatement(connection, entity) //connection.prepareStatement("INSERT INTO friendships(id1, id2) VALUES(?,?)");
        ){
            int rez = statement.executeUpdate();

            statement.close();
            connection.close();
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public Optional<E> delete(Integer id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = deleteStatement(connection, id) //connection.prepareStatement("DELETE FROM friendships WHERE id = ?");
        ){
            E e = findOne(id).orElse(null);
            if (e == null) return Optional.empty();

            int rez = statement.executeUpdate();
            statement.close();
            connection.close();
            return Optional.of(e);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<E> update(E entity) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = updateStatement(connection, entity) //connection.prepareStatement("UPDATE friendships SET id1 = ?, id2 = ? WHERE id = ?");
        ){
            int rez = statement.executeUpdate();
            statement.close();
            connection.close();
            return Optional.empty();
        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
    }
}
