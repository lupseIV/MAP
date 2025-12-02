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
import java.util.ArrayList;
import java.util.List;

public class PersonDatabaseRepository extends EntityDatabaseRepository<Long, Person> {

    private PersonFactory personFactory = null;

    public PersonDatabaseRepository(Validator<Person> validator) {
        super(validator, "SELECT * FROM persons");
    }

    @Override
    public Page<Person> findAllOnPage(Pageable pageable) {
        String sql = sqlSelectAllStatement + " LIMIT ? OFFSET ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            List<Person> entries = new ArrayList<>();

            stmt.setInt(1, pageable.getPageSize());
            stmt.setInt(2, pageable.getPageNumber() * pageable.getPageSize());

            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Person person = extractEntityFromResultSet(resultSet);
                    entries.add(person);
                }
            }
            return new Page<>(entries, entities.size());
        } catch (SQLException e) {
            throw new RepositoryException("Error getting page of persons", e);
        }
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
