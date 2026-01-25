package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Driver;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.concurrent.Executor;

public class DriverService extends BaseService<Integer, Driver> {

    public DriverService(EntityRepository<Integer, Driver> repository, Validator<Driver> validator, Executor executor) {
        super(repository, validator, executor);
    }

}
