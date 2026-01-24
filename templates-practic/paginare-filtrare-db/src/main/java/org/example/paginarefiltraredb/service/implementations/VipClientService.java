package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Manager;
import org.example.paginarefiltraredb.domain.entities.VipClient;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.VipClientDbRepository;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class VipClientService extends BaseService<Integer, VipClient> {

    public VipClientService(EntityRepository<Integer, VipClient> repository, Validator<VipClient> validator) {
        super(repository, validator);
    }

    /**
     * Specific: Sort by Loyalty Points (Most loyal first)
     */
    public List<VipClient> findAllSortedByLoyalty() {
        return StreamSupport.stream(findAll().spliterator(), false)
                .sorted(Comparator.comparingInt(VipClient::getLoyaltyPoints).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Business Logic: Assign a Personal Manager to all high-value VIPs who don't have one
     */
    public void assignManagerToTopVips(Manager manager, double budgetThreshold) {
        StreamSupport.stream(findAll().spliterator(), false)
                .filter(v -> v.getBudget() > budgetThreshold && v.getPersonalManager() == null)
                .forEach(v -> {
                    v.setPersonalManager(manager);
                    update(v); // Notifies UI
                });
    }
}