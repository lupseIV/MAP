package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Manager;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.ManagerDbRepository;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ManagerService extends BaseService<Long, Manager> {

    public ManagerService(EntityRepository<Long, Manager> repository, Validator<Manager> validator, Executor executor) {
        super(repository, validator, executor);
    }

    /**
     * Specific: Sort by Team Size (Largest teams first)
     */
    public CompletableFuture<List<Manager>> findAllSortedByTeamSize() {
        return findAll().thenApply(clients ->
                StreamSupport.stream(clients.spliterator(), false)
                        .sorted(Comparator.comparingInt(Manager::getTeamSize).reversed())
                        .collect(Collectors.toList()));
    }

    /**
     * Business Logic: Promote managers with large teams
     */
    public CompletableFuture<Void> promoteTopManagers(int minTeamSize) {
        // Use thenCompose because we are returning a new Future (the result of allOf)
        return findAll().thenCompose(managers -> {

            // 1. Create a list of Futures (one for each update)
            List<CompletableFuture<Manager>> updateFutures = StreamSupport.stream(managers.spliterator(), false)
                    .filter(m -> m.getTeamSize() > minTeamSize && m.getAccessLevel() < 5)
                    .map(m -> {
                        m.setAccessLevel(m.getAccessLevel() + 1);
                        m.setBonus(m.getBonus() + 1000.0);
                        // Return the future so we can track it
                        return update(m);
                    })
                    .collect(Collectors.toList());

            // 2. Wrap them in allOf so we wait for everyone to finish
            CompletableFuture<Void> allDone = CompletableFuture.allOf(
                    updateFutures.toArray(new CompletableFuture[0])
            );

            return allDone;
        });
    }
}