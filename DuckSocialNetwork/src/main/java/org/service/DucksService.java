package org.service;

import org.domain.dtos.filters.DuckGUIFilter;
import org.domain.dtos.guiDTOS.DuckGuiDTO;
import org.domain.exceptions.ServiceException;
import org.domain.users.duck.Duck;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.repository.Repository;
import org.repository.util.paging.Page;
import org.repository.util.paging.Pageable;
import org.service.utils.IdGenerator;
import org.utils.enums.DuckTypes;

import java.util.ArrayList;
import java.util.List;

public class DucksService extends EntityService<Long, Duck>{

    private FlockService flockService;

    public DucksService(Validator<Duck> validator, PagingRepository<Long, Duck> repository, IdGenerator<Long> idGenerator) {
        super(validator, repository, idGenerator);
    }

    public void setFlockService(FlockService flockService) {
        this.flockService = flockService;
    }

    @Override
    public Duck delete(Long duckId) {
        var flock = flockService.getFlockByDuckId(duckId);
        if(flock != null){
            flockService.removeDuckFromFlock(flock.getId(), duckId);
        }
        return repository.delete(duckId);
    }


    public List<DuckGuiDTO> getGuiDucksFromPage(Page<Duck> page){
        List<DuckGuiDTO> list = new ArrayList<>();

        page.getElementsOnPage().forEach(duck -> {
            Long id = duck.getId();
            String username = duck.getUsername();
            String email = duck.getEmail();
            String type = String.valueOf(duck.getDuckType());
            Double speed = duck.getSpeed();
            Double rezistance = duck.getRezistance();
            list.add(new DuckGuiDTO(id,username,email,type,speed,rezistance));
        });
        return list;
    }
    public List<String> getDuckTypes(){
        List<String> list = new ArrayList<>();
        list.add("All");
        for(DuckTypes dt : DuckTypes.values()){
            list.add(dt.name());
        }
        return list;
    }

}
