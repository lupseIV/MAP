package org.example.paginarefiltraredb.domain.entities;

import org.example.paginarefiltraredb.customORM.annotations.DbId;

import java.io.Serializable;
import java.util.Objects;

public abstract class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;

    @DbId
    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity<?> entity)) return false;
        return getId() != null && Objects.equals(getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Entity{id=" + id + '}';
    }
}