package org.service;

import org.domain.dtos.guiDTOS.EventGuiDTO;
import org.domain.events.RaceEvent;
import org.domain.exceptions.ServiceException;
import org.domain.users.duck.Duck;
import org.domain.users.duck.SwimmingDuck;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.repository.Repository;
import org.repository.util.paging.Page;
import org.service.utils.IdGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RaceEventService extends EntityService<Long, RaceEvent> {

    DucksService ducksService;

    public RaceEventService(Validator<RaceEvent> validator, PagingRepository<Long, RaceEvent> repository, IdGenerator<Long> idGenerator, DucksService ducksService) {
        super(validator, repository, idGenerator);
        this.ducksService = ducksService;
    }

    public RaceEvent addSpecifiedNrOfDucksToAnRaceEvent(Long id, Integer nrOfDucks) {

        long swimmingCount = StreamSupport.stream(ducksService.findAll().spliterator(), false)
                .filter(SwimmingDuck.class::isInstance)
                .count();

        if (nrOfDucks > swimmingCount) {
            throw new ServiceException("Not enough Swimming Ducks");
        }

        RaceEvent event = repository.findOne(id);
        if (event == null) {
            throw new ServiceException("Event not found");
        }

        if(!event.getSubscribers().isEmpty()) {
            event.setSubscribers(new ArrayList<>());
        }

        List<SwimmingDuck> allDucks = StreamSupport.stream(ducksService.findAll().spliterator(), false)
                .filter(SwimmingDuck.class::isInstance)
                .map(SwimmingDuck.class::cast)
                .toList();

        List<SwimmingDuck> sortedDucks = allDucks.stream()
                .sorted(Comparator.comparing(Duck::getRezistance)
                        .thenComparing(Duck::getSpeed))
                .toList();

        List<SwimmingDuck> selectedDucks = sortedDucks.stream()
                .limit(nrOfDucks)
                .toList();

        for (SwimmingDuck d : selectedDucks) {
            event.addObserver( d);
        }
        validator.validate(event);
        return repository.update(event);
    }

    public List<EventGuiDTO> getGuiRaceEventsFromPage(Page<RaceEvent> page) {
        Iterable<RaceEvent> events = page.getElementsOnPage();
        return StreamSupport.stream(events.spliterator(), false)
                .map(event -> new EventGuiDTO(
                        event.getId(),
                        event.getMaxTime(),
                        event.getName(),
                        event.getState().name()
                ))
                .collect(Collectors.toList());
    }
}
