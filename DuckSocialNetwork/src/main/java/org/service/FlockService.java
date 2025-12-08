package org.service;

import org.domain.dtos.FlockPerformanceDTO;
import org.domain.exceptions.ServiceException;
import org.domain.users.duck.Duck;
import org.domain.users.duck.SwimmingDuck;
import org.domain.users.duck.flock.Flock;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.repository.Repository;
import org.service.utils.IdGenerator;

public class FlockService extends EntityService<Long, Flock<Duck>>{

    private DucksService ducksService;

    public FlockService(Validator<Flock<Duck>> validator, PagingRepository<Long, Flock<Duck>> repository, IdGenerator<Long> idGenerator, DucksService ducksService) {
        super(validator, repository, idGenerator);
        this.ducksService = ducksService;
    }

    /**
     * Override save to use database-generated IDs instead of in-memory IdGenerator.
     * This prevents race conditions when multiple instances are running.
     */
    @Override
    public Flock<Duck> save(Flock<Duck> entity) {
        validator.validate(entity);
        // Don't set ID here - let the repository get it from database sequence
        return repository.save(entity);
    }

    public Duck addDuckToFlock(Long flockId, Long duckId) {
        var flock = repository.findOne(flockId);
        var duck = ducksService.findOne(duckId);

        if(!(duck instanceof SwimmingDuck)){
            throw new ServiceException("Duck is not a SwimmingDuck");
        }

        if(flock != null){
            flock.addMember(duck);
            repository.update(flock);
        }
        return duck;
    }

    public Duck removeDuckFromFlock(Long flockId, Long duckId) {
        var flock = repository.findOne(flockId);
        var duck = ducksService.findOne(duckId);
        if(flock != null && duck != null){
            flock.removeMember(duck);
            repository.update(flock);
        }
        return duck;
    }

    public Flock<? extends Duck> getFlockByDuckId(Long duckId) {
        var duck = ducksService.findOne(duckId);
        if(duck == null){
            throw new ServiceException("No duck with id" +duckId);
        }
        return duck.getFlock();
    }

    @Override
    public Flock<Duck> delete(Long flockId) {
        var flock = repository.findOne(flockId);
        if(flock == null){
            throw new ServiceException("No flock with id" +flockId);
        }
        flock.getMembers().forEach(member -> {
            member.setFlock(null);
        });
        return repository.delete(flockId);
    }

    public FlockPerformanceDTO getFlockPerformance(Long flockId) {
        var flock = repository.findOne(flockId);
        if(flock == null){
            throw new ServiceException("No flock with id" +flockId);
        }
        return flock.getAveragePerformance();
    }
}
