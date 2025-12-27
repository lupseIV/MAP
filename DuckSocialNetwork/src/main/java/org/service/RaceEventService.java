package org.service;

import javafx.application.Platform;
import org.domain.Observable;
import org.domain.Observer;
import org.domain.dtos.guiDTOS.EventGuiDTO;
import org.domain.events.MessageEvent;
import org.domain.events.RaceEvent;
import org.domain.events.UpdateRaceEvent;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RaceEventService extends EntityService<Long, RaceEvent> implements Observable<UpdateRaceEvent, Observer<UpdateRaceEvent>> {

    DucksService ducksService;
    private final List<Observer<UpdateRaceEvent>> observers = new CopyOnWriteArrayList<>();

    public RaceEventService(Validator<RaceEvent> validator, PagingRepository<Long, RaceEvent> repository, IdGenerator<Long> idGenerator, DucksService ducksService) {
        super(validator, repository, idGenerator);
        this.ducksService = ducksService;
    }

    @Override
    public void addObserver(Observer<UpdateRaceEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<UpdateRaceEvent> observer) {
    observers.remove(observer);
    }

    @Override
    public void notifyObservers(UpdateRaceEvent event) {
        for (Observer<UpdateRaceEvent> observer : observers) {
            Platform.runLater(() -> observer.update(event));
        }
    }

    public boolean isDuckSubscribedToEvent(Long eventId, Long duckId) {
        RaceEvent event = repository.findOne(eventId);
        if (event == null) {
            throw new ServiceException("Event not found");
        }

        Duck duck = ducksService.findOne(duckId);
        if (duck == null ) {
            throw new ServiceException("Duck not found");
        }
        if(!(duck instanceof SwimmingDuck)){
            return false;
        }
        return event.getSubscribers().contains((SwimmingDuck) duck);
    }

    public void addDuckToEvent(Long eventId, Long duckId) {
        RaceEvent event = repository.findOne(eventId);
        if (event == null) {
            throw new ServiceException("Event not found");
        }

        Duck duck = ducksService.findOne(duckId);
        if (duck == null ) {
            throw new ServiceException("Swimming Duck not found");
        }
        if(!(duck instanceof SwimmingDuck)){
            throw new ServiceException("Event only for Swimming Ducks");
        }

        event.addObserver((SwimmingDuck) duck);
        validator.validate(event);
        repository.update(event);
        notifyObservers(new UpdateRaceEvent(event, List.of((SwimmingDuck) duck)));
    }

    public void removeDuckFromEvent(Long eventId, Long duckId) {
        RaceEvent event = repository.findOne(eventId);
        if (event == null) {
            throw new ServiceException("Event not found");
        }

        Duck duck = ducksService.findOne(duckId);
        if (duck == null || !(duck instanceof SwimmingDuck)) {
            throw new ServiceException("Swimming Duck not found");
        }

        event.removeObserver((SwimmingDuck) duck);
        validator.validate(event);
        repository.update(event);
        notifyObservers(new UpdateRaceEvent(event, List.of((SwimmingDuck) duck)));

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
        var raceEvent =  repository.update(event);
        notifyObservers(new UpdateRaceEvent(event, selectedDucks));
        return raceEvent;
    }

    /**
     * Rezolvă problema "Natație" determinând timpul minim posibil.
     * Algoritm: Căutare Binară pe răspuns + Verificare Greedy.
     */
    public double solveRace(RaceEvent event) {
        List<SwimmingDuck> ducks = event.getSubscribers();
        List<Integer> distances = event.getDistances();

        // Validări de bază conform restricțiilor (M <= N)
        if (ducks == null || distances == null || ducks.size() < distances.size()) {
            throw new ServiceException("Invalid Race Event parameters for solving.");
        }
        List<SwimmingDuck> sortedDucks = new ArrayList<>(ducks);
        sortedDucks.sort(Comparator.comparingDouble(Duck::getRezistance));

        double minSpeed = ducks.stream().mapToDouble(Duck::getSpeed).min().orElse(0.1);
        double maxDistance = distances.get(distances.size() - 1) * 2.0;

        double low = 0.0;
        double high = maxDistance / minSpeed + 100.0;
        double ans = high;

        for (int i = 0; i < 100; i++) {
            double mid = low + (high - low) / 2;
            if (canFinishRaceInTime(mid, sortedDucks, distances)) {
                ans = mid;
                high = mid;
            } else {
                low = mid;
            }
        }

        event.setMaxTime(ans);
        return ans;
    }

    /**
     * Verifică dacă este posibilă o alocare validă a rațelor pe culoare în timpul dat.
     */
    private boolean canFinishRaceInTime(double timeLimit, List<SwimmingDuck> sortedDucks, List<Integer> distances) {
        int duckIndex = 0;
        int numDucks = sortedDucks.size();

        for (Integer distOneWay : distances) {
            double totalDist = distOneWay * 2.0;
            // time = dist / speed => speed = dist / time
            double requiredSpeed = totalDist / timeLimit;

            boolean foundDuck = false;
            // Căutăm prima rață disponibilă care are viteza necesară
            while (duckIndex < numDucks) {
                SwimmingDuck currentDuck = sortedDucks.get(duckIndex);
                duckIndex++; // Consumăm rața curentă (fie o folosim, fie o sărim)

                if (currentDuck.getSpeed() >= requiredSpeed) {
                    // Am găsit o rață validă!
                    // Deoarece lista e sortată după rezistență, condiția r_i <= r_{i+1}
                    // este satisfăcută implicit prin faptul că avansăm în listă.
                    foundDuck = true;
                    break;
                }
                // Dacă rața nu are viteză suficientă, o sărim.
                // Nu o putem folosi pe culoare ulterioare deoarece acelea sunt și mai lungi (deci cer viteză și mai mare).
            }

            if (!foundDuck) {
                return false; // Nu am găsit rață pentru acest culoar
            }
        }
        return true;
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
