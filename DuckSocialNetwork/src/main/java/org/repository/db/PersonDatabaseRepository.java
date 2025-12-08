package org.repository.db;

import database.DatabaseConnection;
import org.domain.dtos.PersonData;
import org.domain.exceptions.RepositoryException;
import org.domain.users.person.Person;
import org.domain.users.person.PersonFactory;
import org.domain.validators.Validator;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.utils.Constants;
import org.utils.enums.PersonTypes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PersonDatabaseRepository extends EntityDatabaseRepository<Long, Person> {

    private PersonFactory personFactory = null;

    public PersonDatabaseRepository(Validator<Person> validator) {
        super(validator, "SELECT * FROM persons");
    }

    /**
     * Get the next ID from the database sequence.
     * This ensures unique IDs across multiple application instances.
     */
    private Long getNextIdFromDatabase() {
        String sql = "SELECT nextval('persons_id_seq')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new RepositoryException("Failed to get next ID from database sequence");
        } catch (SQLException e) {
            throw new RepositoryException("Error getting next ID from database sequence", e);
        }
    }

    /**
     * Override save to use database-generated ID instead of in-memory generator.
     */
    @Override
    public Person save(Person entity) {
        // Check if entity already exists in memory
        Person existing = super.findOne(entity.getId());
        if (existing != null) {
            return null; // Already exists
        }
        
        // Generate ID from database sequence if not already set
        if (entity.getId() == null) {
            entity.setId(getNextIdFromDatabase());
        }
        
        // Save to database first
        saveToDatabase(entity);
        
        // Then add to in-memory cache
        entities.put(entity.getId(), entity);
        
        return null;
    }



    @Override
    public Person extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        personFactory = new PersonFactory();
        Long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String email = resultSet.getString("email");
        String first_name = resultSet.getString("first_name");
        String last_name = resultSet.getString("last_name");
        String occupation = resultSet.getString("occupation");
        LocalDate date_of_birth = resultSet.getDate("date_of_birth").toLocalDate();
        Double empathy = resultSet.getDouble("empathy_level");

        List<String> dataAttributes = List.of(
                username,password,email,first_name,last_name,occupation,
                date_of_birth.toString(),
                empathy.toString()
        );

        PersonData personData = new PersonData(dataAttributes);
        Person person = personFactory.create(PersonTypes.DEFAULT, personData);
        person.setId(id);

        return person;

    }

    @Override
    public void saveToDatabase(Person entity) {
        String sql = """
            INSERT INTO persons(id, username, password, email, first_name, last_name, occupation, date_of_birth, empathy_level)
            VALUES (?,?,?,?,?,?,?,?,?)    
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)){

            stmt.setLong(1, entity.getId());
            stmt.setString(2, entity.getUsername());
            stmt.setString(3, entity.getPassword());
            stmt.setString(4, entity.getEmail());
            stmt.setString(5, entity.getFirstName());
            stmt.setString(6, entity.getLastName());
            stmt.setString(7, entity.getOccupation());
            stmt.setDate(8, java.sql.Date.valueOf(entity.getDateOfBirth()));
            stmt.setDouble(9, entity.getEmpathyLevel());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error saving person to database", e);
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        String sql = """
            DELETE FROM persons WHERE id = ?;
        """;

        try (Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql)){

            stmt.setLong(1, id);

            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RepositoryException("Error deleting person from database", e);
        }
    }

    @Override
    public void updateFromDatabase(Person entity) {
        String sql = """
            UPDATE persons 
            SET username=?, password=?, email=?,
            first_name=?, last_name=?, occupation=?,
            date_of_birth=?, empathy_level=? WHERE id=?;
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)){

            stmt.setString(1, entity.getUsername());
            stmt.setString(2, entity.getPassword());
            stmt.setString(3, entity.getEmail());
            stmt.setString(4, entity.getFirstName());
            stmt.setString(5, entity.getLastName());
            stmt.setString(6, entity.getOccupation());
            stmt.setDate(7, java.sql.Date.valueOf(entity.getDateOfBirth()));
            stmt.setDouble(8, entity.getEmpathyLevel());
            stmt.setLong(9, entity.getId());


            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RepositoryException("Error updating person from database", e);
        }
    }
}
