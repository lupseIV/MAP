package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Client;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.ClientDbRepository;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClientService extends BaseService<Integer, Client> {

    public ClientService(ClientDbRepository repository, Validator<Client> validator, Executor executor) {
        super(repository, validator, executor);
    }

    // --- Async Business Logic ---

    public CompletableFuture<List<Client>> findAllSortedByBudget() {
        return findAll().thenApply(clients ->
                StreamSupport.stream(clients.spliterator(), false)
                        .sorted(Comparator.comparing(Client::getBudget).reversed())
                        .collect(Collectors.toList())
        );
    }

    public CompletableFuture<Set<String>> getAllTypes() {
        return findAll().thenApply(clients ->
                StreamSupport.stream(clients.spliterator(), false)
                        .map(Client::getType)
                        .collect(Collectors.toSet())
        );
    }
}