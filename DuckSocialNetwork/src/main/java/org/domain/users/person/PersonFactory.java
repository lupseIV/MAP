package org.domain.users.person;

import org.domain.Factory;
import org.domain.dtos.PersonData;
import org.utils.enums.types.PersonTypes;

public class PersonFactory implements Factory<Person, PersonTypes, PersonData> {
    public PersonFactory() {
    }

    @Override
    public Person create(PersonTypes personType, PersonData personData) {
        switch (personType) {
            case DEFAULT -> {
                return new Person(personData);
            }
            default -> {
                return null;
            }
        }
    }
}
