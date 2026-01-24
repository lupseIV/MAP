package org.repository.db;

import database.DatabaseConnection;
import org.domain.events.RaceEvent;
import org.domain.exceptions.RepositoryException;
import org.domain.users.duck.Duck;
import org.domain.users.duck.SwimmingDuck;
import org.domain.users.person.Person;
import org.domain.validators.Validator;
import org.utils.enums.status.RaceEventStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        RaceEventStatus state = RaceEventStatus.valueOf(resultSet.getString("state"));
        Person owner = personDatabaseRepository.findOne(resultSet.getLong("owner_person_id"));

        List<SwimmingDuck> participants = loadEventParticipants(id);
        List<Integer> distances = loadDistances(id);
        Map<Integer, SwimmingDuck> winners = loadWinners(id);

        RaceEvent event = new RaceEvent(participants, name,owner);
        event.setId(id);
        event.setMaxTime(maxTime);
        event.setState(state);
        event.setDistances(distances);
        event.setWinners(winners);

        return event;
    }

    private List<SwimmingDuck> loadEventParticipants(Long id) {
        List<SwimmingDuck> participants = new ArrayList<>();
        String sql = "SELECT * FROM race_event_participants WHERE event_id = ?";

        try (DatabaseConnection.AutoCloseableConnection con = DatabaseConnection.getAutoCloseableConnection();
        PreparedStatement stmt = con.get().prepareStatement(sql)){

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

    private List<Integer> loadDistances(Long eventId) {
        List<Integer> distances = new ArrayList<>();
        String sql = "SELECT distance FROM race_event_distances WHERE event_id = ? ORDER BY lane_index ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    distances.add(rs.getInt("distance"));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error loading distances", e);
        }
        return distances;
    }

    private Map<Integer, SwimmingDuck> loadWinners(Long eventId) {
        Map<Integer, SwimmingDuck> winners = new HashMap<>();
        String sql = "SELECT duck_id, position FROM race_event_winners WHERE event_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long duckId = rs.getLong("duck_id");
                    int position = rs.getInt("position");
                    Duck duck = duckDatabaseRepository.findOne(duckId);
                    if (duck instanceof SwimmingDuck) {
                        winners.put(position, (SwimmingDuck) duck);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error loading winners", e);
        }
        return winners;
    }

    @Override
    public void saveToDatabase(RaceEvent event) {
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getAutoCloseableConnection()) {
            String sql = "INSERT INTO race_events (id, name, max_time, owner_person_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.get().prepareStatement(sql)) {
                stmt.setLong(1, event.getId());
                stmt.setString(2, event.getName());
                stmt.setDouble(3, event.getMaxTime());
                stmt.setLong(4, event.getOwner().getId());
                stmt.executeUpdate();
            }

            saveEventParticipants(conn, event);
            saveDistances(conn, event);
            saveWinners(conn, event);

        } catch (SQLException e) {
            throw new RepositoryException("Error saving race event to database", e);
        }
    }

    private void saveEventParticipants(DatabaseConnection.AutoCloseableConnection conn, RaceEvent event) throws SQLException {
        List<SwimmingDuck> participants = event.getSubscribers();
        String sql = "INSERT INTO race_event_participants (event_id, duck_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.get().prepareStatement(sql)) {
            for(SwimmingDuck duck : participants){
                stmt.setLong(1, event.getId());
                stmt.setLong(2, duck.getId());
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error saving participants to database", e);
        }
    }

    private void saveDistances(DatabaseConnection.AutoCloseableConnection conn, RaceEvent event) throws SQLException {
        List<Integer> distances = event.getDistances();
        if (distances == null || distances.isEmpty()) return;

        String sql = "INSERT INTO race_event_distances (event_id, distance, lane_index) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.get().prepareStatement(sql)) {
            for (int i = 0; i < distances.size(); i++) {
                stmt.setLong(1, event.getId());
                stmt.setInt(2, distances.get(i));
                stmt.setInt(3, i + 1); // 1-based lane index
                stmt.executeUpdate();
            }
        }
    }

    private void saveWinners(DatabaseConnection.AutoCloseableConnection conn, RaceEvent event) throws SQLException {
        Map<Integer, SwimmingDuck> winners = event.getWinners();
        if (winners == null || winners.isEmpty()) return;

        String sql = "INSERT INTO race_event_winners (event_id, duck_id, position) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.get().prepareStatement(sql)) {
            for (Map.Entry<Integer, SwimmingDuck> entry : winners.entrySet()) {
                stmt.setLong(1, event.getId());
                stmt.setLong(2, entry.getValue().getId());
                stmt.setInt(3, entry.getKey());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getAutoCloseableConnection()) {
            String[] tables = {"race_event_participants", "race_event_distances", "race_event_winners"};
            for (String table : tables) {
                try (PreparedStatement stmt = conn.get().prepareStatement("DELETE FROM " + table + " WHERE event_id = ?")) {
                    stmt.setLong(1, id);
                    stmt.executeUpdate();
                }
            }

            String deleteEventSql = "DELETE FROM race_events WHERE id = ?";
            try (PreparedStatement stmt = conn.get().prepareStatement(deleteEventSql)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting race event from database", e);
        }
    }

    @Override
    public void updateFromDatabase(RaceEvent event) {
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getAutoCloseableConnection()) {
            String sql = "UPDATE race_events SET name = ?, max_time = ?, state = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.get().prepareStatement(sql)) {
                stmt.setString(1, event.getName());
                stmt.setDouble(2, event.getMaxTime());
                stmt.setString(3, event.getState().name());
                stmt.setLong(4, event.getId());
                stmt.executeUpdate();
            }

            String[] tables = {"race_event_participants", "race_event_distances", "race_event_winners"};
            for (String table : tables) {
                try (PreparedStatement stmt = conn.get().prepareStatement("DELETE FROM " + table + " WHERE event_id = ?")) {
                    stmt.setLong(1, event.getId());
                    stmt.executeUpdate();
                }
            }

            saveEventParticipants(conn, event);
            saveDistances(conn, event);
            saveWinners(conn, event);

        } catch (SQLException e) {
            throw new RepositoryException("Error updating race event in database", e);
        }
    }

}



