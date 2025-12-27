package org.repository.db;

import database.DatabaseConnection;
import org.domain.events.RaceEvent;
import org.domain.exceptions.RepositoryException;
import org.domain.users.duck.Duck;
import org.domain.users.duck.SwimmingDuck;
import org.domain.users.person.Person;
import org.domain.validators.Validator;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.utils.enums.EventState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RaceEventDatabaseRepository extends EntityDatabaseRepository<Long, RaceEvent>{

    private final DuckDatabaseRepository duckDatabaseRepository;
    private final PersonDatabaseRepository personDatabaseRepository;

    public RaceEventDatabaseRepository(Validator<RaceEvent> validator, DuckDatabaseRepository duckDatabaseRepository, PersonDatabaseRepository personDatabaseRepository) {
        super(validator, "SELECT * FROM race_events",false);
        this.duckDatabaseRepository = duckDatabaseRepository;
        this.personDatabaseRepository = personDatabaseRepository;

        loadFromDatabase();
    }


    @Override
    public RaceEvent extractEntityFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        Double maxTime = resultSet.getDouble("max_time");
        EventState state = EventState.valueOf(resultSet.getString("state"));
        Person owner = personDatabaseRepository.findOne(resultSet.getLong("owner_person_id"));

        List<SwimmingDuck> participants = loadEventParticipants(id);

        RaceEvent event = new RaceEvent(participants, name,owner);
        event.setId(id);
        event.setMaxTime(maxTime);
        event.setState(state);

        return event;
    }

    private List<SwimmingDuck> loadEventParticipants(Long id) {
        List<SwimmingDuck> participants = new ArrayList<>();
        String sql = "SELECT * FROM race_event_participants WHERE event_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql)){

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long duckId = rs.getLong("duck_id");
                Duck duck = duckDatabaseRepository.findOne(duckId);
                if(duck instanceof SwimmingDuck){
                    participants.add((SwimmingDuck) duck);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error loading participants from database",e);
        }
        return participants;
    }

    @Override
    public void saveToDatabase(RaceEvent event) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO race_events (id, name, max_time, owner_person_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, event.getId());
                stmt.setString(2, event.getName());
                stmt.setDouble(3, event.getMaxTime());
                stmt.setLong(4, event.getOwner().getId());
                stmt.executeUpdate();
            }

            saveEventParticipants(conn, event);

        } catch (SQLException e) {
            throw new RepositoryException("Error saving race event to database", e);
        }
    }

    private void saveEventParticipants(Connection conn, RaceEvent event) throws SQLException {
        List<SwimmingDuck> participants = event.getSubscribers();
        String sql = "INSERT INTO race_event_participants (event_id, duck_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for(SwimmingDuck duck : participants){
                stmt.setLong(1, event.getId());
                stmt.setLong(2, duck.getId());
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error saving participants to database", e);
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String deleteParticipantsSql = "DELETE FROM race_event_participants WHERE event_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteParticipantsSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            String deleteEventSql = "DELETE FROM race_events WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteEventSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting race event from database", e);
        }
    }

    @Override
    public void updateFromDatabase(RaceEvent event) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE race_events SET name = ?, max_time = ?, state = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, event.getName());
                stmt.setDouble(2, event.getMaxTime());
                stmt.setString(3, event.getState().name());
                stmt.setLong(4, event.getId());
                stmt.executeUpdate();
            }

            String deleteSql = "DELETE FROM race_event_participants WHERE event_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setLong(1, event.getId());
                stmt.executeUpdate();
            }

            saveEventParticipants(conn, event);

        } catch (SQLException e) {
            throw new RepositoryException("Error updating race event in database", e);
        }
    }

}



