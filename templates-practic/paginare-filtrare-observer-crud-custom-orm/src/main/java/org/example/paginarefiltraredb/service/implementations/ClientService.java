package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Client;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.ClientDbRepository;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClientService extends BaseService<Integer, Client> {

    public ClientService(EntityRepository<Integer, Client> repository, Validator<Client> validator) {
        super(repository, validator);
    }

    /**
     * Specific Business Logic: Returns all clients sorted by budget (Descending)
     */
    public List<Client> findAllSortedByBudget() {
        return StreamSupport.stream(findAll().spliterator(), false)
                .sorted(Comparator.comparing(Client::getBudget).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Filter Logic: Find Corporate clients with budget > X
     */
    public List<Client> findCorporateClientsWithBudget(Double minBudget) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(c -> "Corporate".equalsIgnoreCase(c.getType()) && c.getBudget() >= minBudget)
                .collect(Collectors.toList());
    }

    public Set<String> getAllTypes(){
        return StreamSupport.stream(findAll().spliterator(),false)
                .map( Client::getType)
                .collect(Collectors.toSet());
    }
}