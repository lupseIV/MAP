package org.example.paginarefiltraredb.repository.database;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.database.DatabaseConnection.AutoCloseableConnection;
import org.example.paginarefiltraredb.domain.entities.Entity;
import org.example.paginarefiltraredb.domain.exceptions.RepositoryException;
import org.example.paginarefiltraredb.domain.filters.SqlFilter;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.repository.paging.util.Pair;
import org.example.paginarefiltraredb.repository.paging.util.paging.Page;
import org.example.paginarefiltraredb.repository.paging.util.paging.Pageable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class EntityDbRepository<ID, E extends Entity<ID>>
        extends EntityRepository<ID, E>
        implements DatabaseCRUD<ID, E> {

    protected String sqlSelectAllStatement;
    protected String tableName;
    protected Class<ID> idClass;

    public EntityDbRepository(Validator<E> validator, String tableName, Class<ID> idClass) {
        super(validator);
        this.tableName = tableName;
        this.sqlSelectAllStatement = "SELECT * FROM " + tableName;
        this.idClass = idClass;
    }

    /**
     * Generate new ID using database sequence
     * Uses AutoCloseableConnection to ensure proper resource cleanup
     */
    @Override
    protected ID generateNewId() {
        String sql = "SELECT nextval('" + tableName + "_id_seq')";

        try (AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                if (idClass.equals(Long.class)) {
                    return idClass.cast(rs.getLong(1));
                } else if (idClass.equals(Integer.class)) {
                    return idClass.cast(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error generating ID", e);
        }
        return null;
    }

    /**
     * Find all entities on a specific page with filtering
     * Internal method - uses passed connection for transaction consistency
     */
    private List<E> findAllOnPageInternal(AutoCloseableConnection connection,
                                          Pageable pageable,
                                          SqlFilter filter) throws SQLException {
        List<E> tuplesOnPage = new ArrayList<>();

        String sql = sqlSelectAllStatement;
        Pair<String, List<Object>> sqlFilter = filter.toSql();
        if (!sqlFilter.getFirst().isEmpty()) {
            sql += " WHERE " + sqlFilter.getFirst();
        }
        sql += " LIMIT ? OFFSET ?";

        try (PreparedStatement statement = connection.get().prepareStatement(sql)) {
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

    /**
     * Count total entities matching the filter
     * Internal method - uses passed connection for transaction consistency
     */
    private int count(AutoCloseableConnection connection, SqlFilter filter) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM (" + sqlSelectAllStatement + ") t";
        Pair<String, List<Object>> sqlFilter = filter.toSql();
        if (!sqlFilter.getFirst().isEmpty()) {
            sql += " WHERE " + sqlFilter.getFirst();
        }

        try (PreparedStatement statement = connection.get().prepareStatement(sql)) {
            int paramIndex = 0;
            for (Object param : sqlFilter.getSecond()) {
                statement.setObject(++paramIndex, param);
            }

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("count");
                }
                return 0;
            }
        }
    }

    /**
     * PUBLIC API: Find all entities on page with filter
     * Opens its own connection - safe to call from async context
     */
    public Page<E> findAllOnPage(Pageable pageable, SqlFilter filter) {
        try (AutoCloseableConnection connection = DatabaseConnection.getConnection()) {
            int totalNumberOfTuples = count(connection, filter);
            List<E> tuplesOnPage;

            if (totalNumberOfTuples > 0) {
                tuplesOnPage = findAllOnPageInternal(connection, pageable, filter);
            } else {
                tuplesOnPage = new ArrayList<>();
            }

            return new Page<>(tuplesOnPage, totalNumberOfTuples);
        } catch (SQLException e) {
            throw new RepositoryException("Error finding page with filter", e);
        }
    }

    /**
     * PUBLIC API: Find all entities on page (no filter)
     * Opens its own connection - safe to call from async context
     */
    @Override
    public Page<E> findAllOnPage(Pageable pageable) {
        String sql = sqlSelectAllStatement + " LIMIT ? OFFSET ?";

        try (AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            List<E> entries = new ArrayList<>();

            stmt.setInt(1, pageable.getPageSize());
            stmt.setInt(2, pageable.getPageNumber() * pageable.getPageSize());

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    E e = extractEntityFromResultSet(resultSet);
                    entries.add(entities.get(e.getId()));
                }
            }

            return new Page<>(entries, entities.size());
        } catch (SQLException e) {
            throw new RepositoryException("Error getting page", e);
        }
    }

    /**
     * Extract entity from ResultSet - to be implemented by subclasses
     */
    public abstract E extractEntityFromResultSet(ResultSet resultSet) throws SQLException;

    /**
     * Load all entities from database into in-memory cache
     * Opens its own connection - safe to call from async context
     */
    protected void loadFromDatabase() {
        try (AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sqlSelectAllStatement);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                E entity = extractEntityFromResultSet(rs);
                if (entity != null) {
                    super.save(entity);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error loading entities from database: " + sqlSelectAllStatement, e);
        }
    }

    /**
     * Save entity - persists to database if not already in memory
     * Opens its own connection - safe to call from async context
     */
    @Override
    public E save(E entity) {
        E result = super.save(entity);
        if (result == null) {
            saveToDatabase(entity);
        }
        return result;
    }

    /**
     * Delete entity - removes from database if exists in memory
     * Opens its own connection - safe to call from async context
     */
    @Override
    public E delete(ID id) {
        E result = super.delete(id);
        if (result != null) {
            deleteFromDatabase(id);
        }
        return result;
    }

    /**
     * Update entity - persists to database if successful in memory
     * Opens its own connection - safe to call from async context
     */
    @Override
    public E update(E entity) {
        E result = super.update(entity);
        if (result == null) {
            updateFromDatabase(entity);
        }
        return result;
    }
}
