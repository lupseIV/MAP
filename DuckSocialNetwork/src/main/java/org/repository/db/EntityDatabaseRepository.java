package org.repository.db;

import database.DatabaseConnection;
import org.domain.Entity;
import org.domain.dtos.filters.DuckGUIFilter;
import org.domain.dtos.filters.SqlFilter;
import org.domain.exceptions.RepositoryException;
import org.domain.users.duck.Duck;
import org.domain.validators.Validator;
import org.repository.EntityRepository;
import org.repository.util.Pair;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public abstract class EntityDatabaseRepository<ID, E extends Entity<ID>> extends EntityRepository<ID, E> implements DatabaseCRUD<ID, E>  {

    protected String sqlSelectAllStatement;

    public EntityDatabaseRepository(Validator<E> validator, String sqlSelectAllStatement) {
        this(validator,sqlSelectAllStatement,true);

    }

    private List<E> findAllOnPage(Connection connection, Pageable pageable, SqlFilter filter) throws SQLException {
        List<E> tuplesOnPage = new ArrayList<>();
        // Using StringBuilder rather than "+" operator for concatenating Strings is more performant
        // since Strings are immutable, so every operation applied on a String will create a new String
        String sql = sqlSelectAllStatement;
        Pair<String, List<Object>> sqlFilter = filter.toSql();
        if (!sqlFilter.getFirst().isEmpty()) {
            sql += " where " + sqlFilter.getFirst();
        }
        sql += " limit ? offset ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getSecond()) {
                statement.setObject(++paramIndex, param);
            }
            statement.setInt(++paramIndex, pageable.getPageSize());
            statement.setInt(++paramIndex, pageable.getPageSize() * pageable.getPageNumber());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    E e = extractEntityFromResultSet(resultSet);
                    tuplesOnPage.add(e);
                }
            }
        }
        return tuplesOnPage;
    }

    private int count(Connection connection, SqlFilter filter) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM (" + sqlSelectAllStatement + ") t";
        Pair<String, List<Object>> sqlFilter = filter.toSql();
        if (!sqlFilter.getFirst().isEmpty()) {
            sql += " where " + sqlFilter.getFirst();
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getSecond()) {
                statement.setObject(++paramIndex, param);
            }
            try (ResultSet result = statement.executeQuery()) {
                int totalNumberOfDucks = 0;
                if (result.next()) {
                    totalNumberOfDucks = result.getInt("count");
                }
                return totalNumberOfDucks;
            }
        }
    }

    public Page<E> findAllOnPage(Pageable pageable, SqlFilter filter) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            int totalNumberOfTuples = count(connection, filter);
            List<E> tuplesOnPage;
            if (totalNumberOfTuples > 0) {
                tuplesOnPage = findAllOnPage(connection, pageable, filter);
            } else {
                tuplesOnPage = new ArrayList<>();
            }
            return new Page<>(tuplesOnPage, totalNumberOfTuples);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<E> findAllOnPage(Pageable pageable) {
        String sql = sqlSelectAllStatement + " limit ? offset ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            List<E> entries = new ArrayList<>();

            stmt.setInt(1, pageable.getPageSize());
            stmt.setInt(2, pageable.getPageNumber()*pageable.getPageSize());

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    E e = extractEntityFromResultSet(resultSet);
                    entries.add(entities.get(e.getId()));
                }
            }
            return new Page<E>(entries, entities.size());
        } catch (SQLException e) {
            throw new RepositoryException("Error getting page", e);
        }
    }

    public EntityDatabaseRepository(Validator<E> validator, String sqlSelectAllStatement, boolean autoLoad) {
        super(validator);
        this.sqlSelectAllStatement = sqlSelectAllStatement;
        if(autoLoad) loadFromDatabase();
    }

    public abstract E extractEntityFromResultSet(ResultSet resultSet) throws SQLException;
    protected void loadFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSelectAllStatement);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                E entity = extractEntityFromResultSet(rs);
                super.save(entity);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error loading entities:" + sqlSelectAllStatement +"from database", e);
        }
    }

    @Override
    public E save(E entity) {
        E result = super.save(entity);
        if(result == null) {
            saveToDatabase(entity);
        }
        return result;
    }

    @Override
    public E delete(ID id) {
        E result = super.delete(id);
        if(result != null) {
            deleteFromDatabase(id);
        }
        return result;
    }

    @Override
    public E update(E entity) {
        E result = super.update(entity);
        if(result == null) {
            updateFromDatabase(entity);
        }
        return result;
    }
}
