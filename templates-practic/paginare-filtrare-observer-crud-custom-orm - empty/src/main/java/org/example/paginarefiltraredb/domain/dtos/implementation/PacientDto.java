package org.example.paginarefiltraredb.domain.dtos.implementation;

public class PacientDto {
    private Long id;
    private String nume;
    private String cnp;

    public PacientDto(Long id, String nume, String cnp) {
        this.id = id;
        this.nume = nume;
        this.cnp = cnp;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    @Override
    public String toString() {
        return nume + " (" + cnp + ")";
    }
}