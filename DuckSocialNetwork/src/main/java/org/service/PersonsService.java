package org.service;

import org.domain.dtos.guiDTOS.PersonGuiDTO;
import org.domain.users.person.Person;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.repository.Repository;
import org.repository.util.paging.Page;
import org.service.utils.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class PersonsService extends EntityService<Long, Person> {
    public PersonsService(Validator<Person> validator, PagingRepository<Long, Person> repository, IdGenerator<Long> idGenerator) {
        super(validator, repository, idGenerator);
    }

    public List<PersonGuiDTO> getGuiPersons(){
        List<PersonGuiDTO> list = new ArrayList<>();
        for(Person p : repository.findAll()){
            Long id = p.getId();
            String username = p.getUsername();
            String email = p.getEmail();
            String firstName = p.getFirstName();
            String lastName = p.getLastName();
            String occupation = p.getOccupation();
            String dateOfBirth = p.getDateOfBirth().toString();
            Double empathyLevel = p.getEmpathyLevel();

            list.add(new PersonGuiDTO(id,username,email,firstName,lastName,occupation,dateOfBirth,empathyLevel));
        }
        return list;
    }

    public List<PersonGuiDTO> getGuiPersonsFromPage(Page<Person> page){
        List<PersonGuiDTO> list = new ArrayList<>();

        page.getElementsOnPage().forEach(p -> {
            Long id = p.getId();
            String username = p.getUsername();
            String email = p.getEmail();
            String firstName = p.getFirstName();
            String lastName = p.getLastName();
            String occupation = p.getOccupation();
            String dateOfBirth = p.getDateOfBirth().toString();
            Double empathyLevel = p.getEmpathyLevel();

            list.add(new PersonGuiDTO(id,username,email,firstName,lastName,occupation,dateOfBirth,empathyLevel));
        });
        return list;
    }
}
