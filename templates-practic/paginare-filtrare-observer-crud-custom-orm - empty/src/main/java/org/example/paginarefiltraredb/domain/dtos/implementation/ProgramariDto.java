package org.example.paginarefiltraredb.domain.dtos.implementation;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;

public class ProgramariDto {
    public Long id;
    public SimpleObjectProperty<LocalDateTime> time = new SimpleObjectProperty<>();
    public SimpleStringProperty numePacient = new SimpleStringProperty();
    public SimpleStringProperty cnpPacient = new SimpleStringProperty();
    public String status;

    public ProgramariDto(Programare p) {
        id = p.getId();
        status = p.getStatus();
        this.time.set(p.getDataOra());
        this.numePacient.set(p.getIdPacient().getName());
        this.cnpPacient.set(p.getIdPacient().getCnp());
    }

    public LocalDateTime getTime() {
        return time.get();
    }

    public SimpleObjectProperty<LocalDateTime> timeProperty() {
        return time;
    }

    public String getNumePacient() {
        return numePacient.get();
    }

    public SimpleStringProperty numePacientProperty() {
        return numePacient;
    }

    public String getCnpPacient() {
        return cnpPacient.get();
    }

    public SimpleStringProperty cnpPacientProperty() {
        return cnpPacient;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
