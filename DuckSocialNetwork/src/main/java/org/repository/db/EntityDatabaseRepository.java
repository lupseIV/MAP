package org.repository.db;

import database.DatabaseConnection;
import org.domain.Entity;
import org.domain.exceptions.RepositoryException;
import org.domain.users.duck.Duck;
import org.domain.validators.Validator;
import org.repository.EntityRepository;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public abstract class EntityDatabaseRepository<ID, E extends Entity<ID>> extends EntityRepository<ID, E> implements DatabaseCRUD<ID, E>  {

    protected String sqlSelectAllStatement;

    public EntityDatabaseRepository(Validator<E> validator, String sqlSelectAllStatement) {
        this(validator,sqlSelectAllStatement,true);

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
                    entries.add(e);
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
