package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Staff;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.StaffDbRepository;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StaffService extends BaseService<Long, Staff> {

    public StaffService(EntityRepository<Long, Staff> repository, Validator<Staff> validator) {
        super(repository, validator);
    }

    /**
     * Specific: Sort staff by Salary (High to Low)
     */
    public List<Staff> findAllSortedBySalary() {
        return StreamSupport.stream(findAll().spliterator(), false)
                .sorted(Comparator.comparing(Staff::getSalary).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Business Logic: Give a percentage raise to a specific department
     */
    public void giveRaiseToDepartment(String department, double percentage) {
        Iterable<Staff> allStaff = findAll();
        for (Staff s : allStaff) {
            if (department.equalsIgnoreCase(s.getDepartment())) {
                BigDecimal current = s.getSalary();
                BigDecimal increase = current.multiply(BigDecimal.valueOf(percentage / 100));
                s.setSalary(current.add(increase));

                // This triggers the UPDATE event in BaseService
                update(s);
            }
        }
    }
}