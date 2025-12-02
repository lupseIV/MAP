package org.service;

import org.domain.users.person.Person;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.repository.Repository;
import org.service.utils.IdGenerator;

public class PersonsService extends EntityService<Long, Person> {
    public PersonsService(Validator<Person> validator, PagingRepository<Long, Person> repository, IdGenerator<Long> idGenerator) {
        super(validator, repository, idGenerator);
    }
}
