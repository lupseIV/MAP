package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Manager;
import org.example.paginarefiltraredb.domain.entities.VipClient;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.VipClientDbRepository;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class VipClientService extends BaseService<Integer, VipClient> {

    // 1. FIX: Inject Executor to pass to BaseService
    public VipClientService(VipClientDbRepository repository, Validator<VipClient> validator, Executor executor) {
        super(repository, validator, executor);
    }

    /**
     * Specific: Sort by Loyalty Points (Most loyal first)
     * Returns a Future List, not a raw List.
     */
    public CompletableFuture<List<VipClient>> findAllSortedByLoyalty() {
        // 2. FIX: Chain off the Future returned by findAll()
        return findAll().thenApply(clients ->
                StreamSupport.stream(clients.spliterator(), false)
                        .sorted(Comparator.comparingInt(VipClient::getLoyaltyPoints).reversed())
                        .collect(Collectors.toList())
        );
    }

    /**
     * Business Logic: Assign a Personal Manager to all high-value VIPs who don't have one
     * Returns a Future so the UI knows when it's safe to reload.
     */
    public CompletableFuture<Void> assignManagerToTopVips(Manager manager, double budgetThreshold) {
        // 3. FIX: Use thenCompose to handle the async updates
        return findAll().thenCompose(clients -> {

            List<CompletableFuture<VipClient>> updateFutures = StreamSupport.stream(clients.spliterator(), false)
                    .filter(v -> v.getBudget() > budgetThreshold && v.getPersonalManager() == null)
                    .map(v -> {
                        v.setPersonalManager(manager);
                        // 4. FIX: Return the future from update() so we can track it
                        return update(v);
                    })
                    .collect(Collectors.toList());

            // 5. FIX: Wait for ALL updates to complete
            return CompletableFuture.allOf(
                    updateFutures.toArray(new CompletableFuture[0])
            );
        });
    }
}