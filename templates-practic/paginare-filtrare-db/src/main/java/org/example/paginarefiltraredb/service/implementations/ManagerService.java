package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Manager;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.ManagerDbRepository;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ManagerService extends BaseService<Long, Manager> {

    public ManagerService(EntityRepository<Long, Manager> repository, Validator<Manager> validator) {
        super(repository, validator);
    }

    /**
     * Specific: Sort by Team Size (Largest teams first)
     */
    public List<Manager> findAllSortedByTeamSize() {
        return StreamSupport.stream(findAll().spliterator(), false)
                .sorted(Comparator.comparingInt(Manager::getTeamSize).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Business Logic: Promote managers with large teams
     */
    public void promoteTopManagers(int minTeamSize) {
        StreamSupport.stream(findAll().spliterator(), false)
                .filter(m -> m.getTeamSize() > minTeamSize && m.getAccessLevel() < 5)
                .forEach(m -> {
                    m.setAccessLevel(m.getAccessLevel() + 1);
                    m.setBonus(m.getBonus() + 1000.0);
                    update(m); // Notifies UI
                });
    }
}