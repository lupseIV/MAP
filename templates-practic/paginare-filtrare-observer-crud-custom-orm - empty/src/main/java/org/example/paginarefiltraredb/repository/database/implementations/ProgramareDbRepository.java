package org.example.paginarefiltraredb.repository.database.implementations;

import org.example.paginarefiltraredb.database.DatabaseConnection;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.EntityDbRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ProgramareDbRepository extends EntityDbRepository<Long, Programare> {

    private final String TABLE_NAME = "programari";

    public ProgramareDbRepository(Validator<Programare> validator) {
        super(validator, "programari", Long.class);
        loadFromDatabase();
    }

    @Override
    public Programare extractEntityFromResultSet(ResultSet rs) throws SQLException {
        Long idMedic = rs.getLong("id_medic");
        Long idPacient = rs.getLong("id_pacient");
        Long id = rs.getLong("id");
         LocalDateTime ts = rs.getTimestamp("data_ora").toLocalDateTime();
        String status = rs.getString("status");

        Doctor doctor = resolveDoctorById(idMedic);
        Pacient pacient = resolvePacientById(idPacient);

        return new Programare(id, doctor, pacient, ts, status);
    }


    private Doctor resolveDoctorById(Long idMedic) {
        String sql = "SELECT * FROM doctors WHERE id = ?";
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             var ps = conn.get().prepareStatement(sql)) {
            ps.setLong(1, idMedic);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String specialty = rs.getString("specialty");
                    Doctor doctor = new Doctor(idMedic, name, specialty);
                    return doctor;
                }
            }
    } catch (SQLException e) {
        e.printStackTrace();
        }
        return null;
    }

    private Pacient resolvePacientById(Long idPacient) {
        String sql = "SELECT * FROM pacients WHERE id = ?";
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             var ps = conn.get().prepareStatement(sql)) {
            ps.setLong(1, idPacient);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String cnp = rs.getString("cnp");
                    Pacient pacient = new Pacient(idPacient, name, cnp);
                    return pacient;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveToDatabase(Programare entity) {
        // We do not include 'id' in the INSERT if it is SERIAL/AUTO_INCREMENT in Postgres
        String sql = "INSERT INTO " + TABLE_NAME + " (id_medic, id_pacient, data_ora, status) VALUES (?, ?, ?, ?)";

        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            // 1. Foreign Key: Doctor
            // We assume the Doctor entity is not null. If it might be, check for null.
            stmt.setLong(1, entity.getIdMedic().getId());

            // 2. Foreign Key: Pacient
            stmt.setLong(2, entity.getIdPacient().getId());

            // 3. Timestamp (Convert LocalDateTime to java.sql.Timestamp)
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(entity.getDataOra()));

            // 4. Status
            stmt.setString(4, entity.getStatus());

            stmt.executeUpdate();

        } catch (SQLException e) {
            // It is important to throw the exception up so the Service/UI knows it failed
            throw new RuntimeException("Error saving Programare to DB", e);
        }
    }

    @Override
    public void deleteFromDatabase(Long id) {

    }

    @Override
    public void updateFromDatabase(Programare entity) {
        String sql = "UPDATE " + TABLE_NAME + " SET status = ? WHERE id = ?" ;
        //TODO             "SET nume_col = ? ..." ;
        try (DatabaseConnection.AutoCloseableConnection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.get().prepareStatement(sql)) {

            stmt.setString(1, entity.getStatus());
            stmt.setLong(2, entity.getId());


            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
