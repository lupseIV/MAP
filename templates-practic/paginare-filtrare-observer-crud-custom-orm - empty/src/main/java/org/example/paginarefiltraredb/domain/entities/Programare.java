package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbColumn;
import org.example.paginarefiltraredb.customORM.annotations.DbTable;

import java.time.LocalDateTime;

@DbTable(name = "programari")
public class Programare extends Entity<Long>{

    @DbColumn(name = "id_medic",nullable = false)
    private Doctor idMedic;

    @DbColumn(name = "id_pacient",nullable = false)
    private Pacient idPacient;

    @DbColumn(name = "data_ora",nullable = false)
    private LocalDateTime dataOra;

    @DbColumn(name = "status",nullable = false)
    private String status;

    public Programare(Long id, Doctor idMedic, Pacient idPacient, LocalDateTime dataOra, String status) {
        this.idMedic = idMedic;
        this.idPacient = idPacient;
        this.dataOra = dataOra;
        this.status = status;
        super.setId(id);
    }

    public Programare(Doctor idMedic, Pacient idPacient, LocalDateTime dataOra, String status) {
        this.idMedic = idMedic;
        this.idPacient = idPacient;
        this.dataOra = dataOra;
        this.status = status;
    }

    public Doctor getIdMedic() {
        return idMedic;
    }

    public void setIdMedic(Doctor idMedic) {
        this.idMedic = idMedic;
    }

    public Pacient getIdPacient() {
        return idPacient;
    }

    public void setIdPacient(Pacient idPacient) {
        this.idPacient = idPacient;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public void setStatus(String finished) {
        status = finished;
    }

    public String getStatus() {
        return status;
    }
}
