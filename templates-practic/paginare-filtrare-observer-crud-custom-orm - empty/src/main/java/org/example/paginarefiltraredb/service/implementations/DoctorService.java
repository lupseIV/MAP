package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Doctor;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DoctorService extends BaseService<Long, Doctor> {
    public DoctorService(EntityRepository<Long, Doctor> repository, Validator<Doctor> validator, Executor executor) {
        super(repository, validator,executor);
    }
}
