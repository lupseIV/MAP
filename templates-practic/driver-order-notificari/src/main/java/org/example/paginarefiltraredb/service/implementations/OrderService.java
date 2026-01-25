package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.Driver;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.filters.implementations.OrderFilter;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.paging.EntityRepository;
import org.example.paginarefiltraredb.service.BaseService;
import org.example.paginarefiltraredb.service.implementations.DriverService;
import org.example.paginarefiltraredb.service.observer.ChangeEventType;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class OrderService extends BaseService<Integer, Order> {

    private DriverService driverService;

    public OrderService(EntityRepository<Integer, Order> repository, Validator<Order> validator, Executor executor) {
        super(repository, validator, executor);
    }

    public void setDriverService(DriverService driverService) {
        this.driverService = driverService;
    }

    @Override
    public CompletableFuture<Order> add(Order entity) {
        return CompletableFuture.supplyAsync(() -> {
            // Validate
            validator.validate(entity);

            // Save synchronously (repository is synchronous)
            Order saved = repository.save(entity);

            if (saved == null) {
                // Build candidate list: drivers without IN_PROGRESS, ordered by time since last FINISHED (desc)
                List<Driver> drivers = new ArrayList<>();
                if (driverService != null) {
                    try {
                        Iterable<Driver> drvIter = driverService.findAll().join();
                        drvIter.forEach(drivers::add);
                    } catch (Exception e) {
                        // No drivers available - just notify observers normally
                        notifyObservers(new EntityChangeEvent<>(ChangeEventType.ADD, entity));
                        return null;
                    }
                } else {
                    // If no driverService configured, fall back to broadcasting
                    notifyObservers(new EntityChangeEvent<>(ChangeEventType.ADD, entity));
                    return null;
                }

                LocalDateTime now = LocalDateTime.now();
                // Compute duration since last finished for each driver; skip drivers with active IN_PROGRESS
                List<DriverCandidate> candidates = new ArrayList<>();
                for (Driver d : drivers) {
                    OrderFilter fActive = new OrderFilter();
                    fActive.setDriverId(d.getId());
                    fActive.setStatus("IN_PROGRESS");
                    Iterable<Order> active = repository.findAll(fActive);
                    boolean hasActive = active.iterator().hasNext();
                    if (hasActive) continue; // skip

                    // find last finished
                    OrderFilter fFinished = new OrderFilter();
                    fFinished.setDriverId(d.getId());
                    fFinished.setStatus("FINISHED");
                    Iterable<Order> finished = repository.findAll(fFinished);
                    LocalDateTime lastEnd = null;
                    for (Order o : finished) {
                        if (o.getEndDate() != null) {
                            if (lastEnd == null || o.getEndDate().isAfter(lastEnd)) lastEnd = o.getEndDate();
                        }
                    }
                    Duration sinceLast = (lastEnd == null) ? Duration.ofDays(Long.MAX_VALUE / (24*3600)) : Duration.between(lastEnd, now);
                    candidates.add(new DriverCandidate(d, sinceLast));
                }

                // Sort descending by duration
                candidates.sort(Comparator.comparing(DriverCandidate::getSinceLast).reversed());

                // Sequentially notify
                for (DriverCandidate candidate : candidates) {
                    // Create a transient notification Order with driverId set to candidate
                    Order notifyOrder = new Order(saved.getDriverId(), saved.getStatus(), saved.getStartDate(), saved.getEndDate(), saved.getPickUpAddress(), saved.getDestinationAddress(), saved.getClientName());
                    notifyOrder.setId(saved.getId());
                    notifyOrder.setDriverId(candidate.driver.getId());

                    // Notify all observers (each DriverController will determine if it's for them)
                    notifyObservers(new EntityChangeEvent<>(ChangeEventType.ADD, notifyOrder));

                    // Wait up to 5 seconds for acceptance (i.e., repository shows driver assigned / status changed)
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Refresh from repository
                    Optional<Order> currentOpt = repository.findOne(saved.getId());
                    if (currentOpt.isPresent()) {
                        Order current = currentOpt.get();
                        if (current.getDriverId() != null && current.getStatus() != null && current.getStatus().name().equals("IN_PROGRESS")) {
                            // Accepted by someone (probably this candidate) - stop
                            break;
                        }
                    } else {
                        // Order disappeared (unlikely) - stop
                        break;
                    }
                }

                return null;
            }

            return saved;
        }, executor);
    }

    private static class DriverCandidate {
        final Driver driver;
        final java.time.Duration sinceLast;

        DriverCandidate(Driver d, java.time.Duration sinceLast) {
            this.driver = d;
            this.sinceLast = sinceLast;
        }

        public java.time.Duration getSinceLast() {
            return sinceLast;
        }
    }
}
