package org.service;

import javafx.application.Platform;
import org.domain.Observable;
import org.domain.Observer;
import org.domain.dtos.guiDTOS.EventGuiDTO;
import org.domain.events.RaceEvent;
import org.domain.exceptions.ServiceException;
import org.domain.observer_events.RaceObserverEvent;
import org.domain.users.User;
import org.domain.users.duck.Duck;
import org.domain.users.duck.SwimmingDuck;
import org.domain.users.relationships.notifications.MessageData;
import org.domain.users.relationships.notifications.Notification;
import org.domain.users.relationships.notifications.RaceEventData;
import org.domain.validators.Validator;
import org.repository.PagingRepository;
import org.repository.util.paging.Page;
import org.service.utils.IdGenerator;
import org.utils.enums.actions.MessageAction;
import org.utils.enums.actions.RaceEventAction;
import org.utils.enums.status.NotificationStatus;
import org.utils.enums.status.RaceEventStatus;
import org.utils.enums.types.NotificationType;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.Thread.sleep;

public class RaceEventService extends EntityService<Long, RaceEvent> implements Observable<RaceObserverEvent, Observer<RaceObserverEvent>> {

    DucksService ducksService;
    private final List<Observer<RaceObserverEvent>> observers = new CopyOnWriteArrayList<>();
    private NotificationService notificationService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public RaceEventService(Validator<RaceEvent> validator, PagingRepository<Long, RaceEvent> repository, IdGenerator<Long> idGenerator, DucksService ducksService) {
        super(validator, repository, idGenerator);
        this.ducksService = ducksService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void addObserver(Observer<RaceObserverEvent> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<RaceObserverEvent> observer) {
    observers.remove(observer);
    }

    @Override
    public void notifyObservers(RaceObserverEvent event) {
        for (Observer<RaceObserverEvent> observer : observers) {
            observer.update(event);
        }
    }

    @Override
    public RaceEvent save(RaceEvent entity) {
        var e = super.save(entity);
        notifyObservers(new RaceObserverEvent(RaceEventAction.CREATE, List.of(entity), entity.getOwner()));
        return e;
    }

    @Override
    public RaceEvent delete(Long aLong) {
        RaceEvent event = repository.findOne(aLong);
        var e = super.delete(aLong);
        notifyObservers(new RaceObserverEvent(RaceEventAction.DELETE, List.of(event), event.getOwner()));
        return e;
    }

    @Override
    public RaceEvent update(RaceEvent entity) {
        var e = super.update(entity);
        notifyObservers(new RaceObserverEvent(RaceEventAction.UPDATE, List.of(entity), entity.getOwner()));
        return e;
    }

    public  boolean isDuckSubscribedToEvent(Long eventId, Long duckId) {
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

    public  CompletableFuture<Void> addDuckToEvent(Long eventId, Long duckId) {
        return CompletableFuture.runAsync(() -> {
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
            boolean exists = event.getSubscribers().stream()
                    .anyMatch(d -> d.getId().equals(duck.getId()));
            if (exists) {
                throw new ServiceException("Duck already subscribed to event");
            }
            event.addObserver((SwimmingDuck) duck);
            validator.validate(event);
            repository.update(event);

            notifyObservers(new RaceObserverEvent(RaceEventAction.SUBSCRIBE, List.of(event), duck));

            Notification notification = new Notification(
                    NotificationType.RACE_EVENT,
                    NotificationStatus.NEW,
                    duck,
                    event.getOwner()
            );
            notification.setDescription("New subscription to event " + event.getName());
            notification.setData(new RaceEventData(event, duck, RaceEventAction.SUBSCRIBE));

            notificationService.save(notification);
        }, executorService);
    }


    public  CompletableFuture<Void> removeDuckFromEvent(Long eventId, Long duckId) {
        return CompletableFuture.runAsync(() -> {
            RaceEvent event = repository.findOne(eventId);
            if (event == null) {
                throw new ServiceException("Event not found");
            }

            Duck duck = ducksService.findOne(duckId);
            if (duck == null || !(duck instanceof SwimmingDuck)) {
                throw new ServiceException("Swimming Duck not found");
            }
            boolean exists = event.getSubscribers().stream()
                    .anyMatch(d -> d.getId().equals(duck.getId()));
            if (!exists) {
                throw new ServiceException("Duck not subscribed to event");
            }

            event.removeObserver((SwimmingDuck) duck);
            validator.validate(event);
            repository.update(event);

            notifyObservers(new RaceObserverEvent(RaceEventAction.UNSUBSCRIBE, List.of(event), duck));

            Notification notification = new Notification(
                    NotificationType.RACE_EVENT,
                    NotificationStatus.NEW,
                    duck,
                    event.getOwner()
            );
            notification.setDescription("New unsubscription to event " + event.getName());
            notification.setData(new RaceEventData(event, duck, RaceEventAction.UNSUBSCRIBE));

            notificationService.save(notification);
        }, executorService);
    }

    public  CompletableFuture<RaceEvent> addSpecifiedNrOfDucksToAnRaceEvent(Long id, Integer nrOfDucks) {
        return CompletableFuture.supplyAsync(() -> {
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
                event.addObserver(d);
            }

            validator.validate(event);
            var raceEvent = repository.update(event);

            for(var d : selectedDucks){
                notifyObservers(new RaceObserverEvent(RaceEventAction.SUBSCRIBE, List.of(raceEvent), d));

                Notification notification = new Notification(
                        NotificationType.RACE_EVENT,
                        NotificationStatus.NEW,
                        event.getOwner(),
                        d
                );
                notification.setDescription("You were added to event " + event.getName());
                notification.setData(new RaceEventData(event, d, RaceEventAction.SUBSCRIBE));

                notificationService.save(notification);
            }
            return raceEvent;
        }, executorService);
    }

    /**
     * Rezolvă problema "Natație" determinând timpul minim posibil.
     * Algoritm: Căutare Binară pe răspuns + Verificare Greedy.
     */
    //TODO : add start race notification
    public  CompletableFuture<Double> solveRace(RaceEvent event) {
        return CompletableFuture.supplyAsync(() -> {
            List<SwimmingDuck> ducks = event.getSubscribers();
            List<Integer> distances = event.getDistances();

            if (ducks == null || distances == null || ducks.size() < distances.size()) {
                throw new ServiceException("Invalid Race Event parameters for solving.");
            }

            event.setState(RaceEventStatus.ONGOING);

            List<SwimmingDuck> sortedDucks = new ArrayList<>(ducks);
            sortedDucks.sort(Comparator.comparingDouble(Duck::getRezistance));

            for(var d : sortedDucks){
                Notification notification = new Notification(
                        NotificationType.RACE_EVENT,
                        NotificationStatus.NEW,
                        event.getOwner(),
                        d
                );
                notification.setDescription("The race " + event.getName() + " has started!");
                notification.setData(new RaceEventData(event, d, RaceEventAction.START));

                notificationService.save(notification);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

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

            Map<Integer, SwimmingDuck> solution = new HashMap<>();
            int duckIndex = 0;
            int numDucks = sortedDucks.size();

            for (int i = 0; i < distances.size(); i++) {
                double totalDist = distances.get(i) * 2.0;
                double requiredSpeed = totalDist / ans;

                while (duckIndex < numDucks) {
                    SwimmingDuck currentDuck = sortedDucks.get(duckIndex);
                    duckIndex++;
                    if (currentDuck.getSpeed() >= requiredSpeed - 0.00001) {
                        solution.put(i + 1, currentDuck);

                        Notification notification = new Notification(
                                NotificationType.RACE_EVENT,
                                NotificationStatus.NEW,
                                event.getOwner(),
                                currentDuck
                        );
                        notification.setDescription("You won the " + event.getName() + " race event. Go see your results!");
                        notification.setData(new RaceEventData(event, currentDuck, RaceEventAction.FINISH));

                        notificationService.save(notification);

                        break;
                    }
                }
            }
            event.setWinners(solution);
            event.setState(RaceEventStatus.COMPLETED);

            Notification notification = new Notification(
                    NotificationType.RACE_EVENT,
                    NotificationStatus.NEW,
                    event.getOwner(),
                    event.getOwner()
            );
            notification.setDescription("The raced finished! Check out the results for event " + event.getName());
            notification.setData(new RaceEventData(event, event.getOwner(), RaceEventAction.FINISH));


            repository.update(event);

            return ans;
        }, executorService);
    }

    /**
     * Verifică dacă este posibilă o alocare validă a rațelor pe culoare în timpul dat.
     */
    private boolean canFinishRaceInTime(double timeLimit, List<SwimmingDuck> sortedDucks, List<Integer> distances) {
        int duckIndex = 0;
        int numDucks = sortedDucks.size();

        for (Integer distOneWay : distances) {
            double totalDist = distOneWay * 2.0;
            double requiredSpeed = totalDist / timeLimit;

            boolean foundDuck = false;
            while (duckIndex < numDucks) {
                SwimmingDuck currentDuck = sortedDucks.get(duckIndex);
                duckIndex++;

                if (currentDuck.getSpeed() >= requiredSpeed) {
                    foundDuck = true;
                    break;
                }
            }

            if (!foundDuck) {
                return false;
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
                        event.getState().name(),
                        event.getOwner().getUsername(),
                        (long) event.getSubscribers().size(),
                        (long) event.getWinners().size()
                ))
                .collect(Collectors.toList());
    }
}
