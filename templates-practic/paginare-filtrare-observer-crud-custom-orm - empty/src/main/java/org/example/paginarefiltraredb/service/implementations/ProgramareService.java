package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.stream.StreamSupport;

public class ProgramareService extends BaseService<Long, Programare> {
    public ProgramareService(EntityRepository<Long, Programare> repository, Validator<Programare> validator, Executor executor) {
        super(repository, validator, executor);
    }

    public boolean isDoctorAvailable(Long id, LocalDateTime appointmentDateTime) {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .noneMatch(p -> p.getIdMedic().getId().equals(id) && p.getDataOra().equals(appointmentDateTime));
    }
}
