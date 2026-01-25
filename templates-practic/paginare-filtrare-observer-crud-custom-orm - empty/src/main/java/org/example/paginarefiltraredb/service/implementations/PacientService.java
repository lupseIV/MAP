package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Pacient;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.concurrent.Executor;

public class PacientService extends BaseService<Long, Pacient> {
    public PacientService(EntityRepository<Long, Pacient> repository, Validator<Pacient> validator, Executor executors) {
        super(repository, validator, executors);
    }
}
