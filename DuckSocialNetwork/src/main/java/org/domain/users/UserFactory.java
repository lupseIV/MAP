package org.domain.users;

import org.domain.dtos.DuckData;
import org.domain.dtos.PersonData;
import org.domain.users.duck.DuckFactory;
import org.domain.users.person.PersonFactory;
import org.utils.enums.types.DuckTypes;
import org.utils.enums.types.PersonTypes;

public class UserFactory {

    private final DuckFactory duckFactory;
    private final PersonFactory personFactory;

    public UserFactory() {
        this.duckFactory = new DuckFactory();
        this.personFactory = new PersonFactory();
    }

    public User createUser(PersonTypes personType, PersonData data) {
        if (personType == null || data == null) {
            throw new IllegalArgumentException("PersonType and data cannot be null");
        }
        return personFactory.create(personType, data);
    }

    public User createUser(DuckTypes duckType, DuckData data) {
        if (duckType == null || data == null) {
            throw new IllegalArgumentException("DuckType and data cannot be null");
        }
        return duckFactory.create(duckType, data);
    }
}
