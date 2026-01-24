package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Staff;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.StaffDbRepository;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.lang.reflect.Executable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StaffService extends BaseService<Long, Staff> {

    public StaffService(StaffDbRepository repository, Validator<Staff> validator, Executor executor) {
        super(repository, validator, executor);
    }

    /**
     * Specific: Sort staff by Salary (High to Low)
     * Returns a Future List
     */
    public CompletableFuture<List<Staff>> findAllSortedBySalary() {
        // 2. FIX: Chain logic inside thenApply
        return findAll().thenApply(staffList ->
                StreamSupport.stream(staffList.spliterator(), false)
                        .sorted(Comparator.comparing(Staff::getSalary).reversed())
                        .collect(Collectors.toList())
        );
    }

    /**
     * Business Logic: Give a percentage raise to a specific department
     * Returns CompletableFuture<Void> so the UI knows when to stop the loading spinner
     */
    public CompletableFuture<Void> giveRaiseToDepartment(String department, double percentage) {
        // 3. FIX: Use thenCompose to chain the read -> write operation
        return findAll().thenCompose(allStaff -> {

            List<CompletableFuture<Staff>> updateFutures = StreamSupport.stream(allStaff.spliterator(), false)
                    .filter(s -> department.equalsIgnoreCase(s.getDepartment()))
                    .map(s -> {
                        BigDecimal current = s.getSalary();
                        BigDecimal increase = current.multiply(BigDecimal.valueOf(percentage / 100));
                        s.setSalary(current.add(increase));

                        // 4. FIX: Return the future so we can wait for it
                        return update(s);
                    })
                    .collect(Collectors.toList());

            // 5. FIX: Return a single future that completes when ALL updates are done
            return CompletableFuture.allOf(
                    updateFutures.toArray(new CompletableFuture[0])
            );
        });
    }
}