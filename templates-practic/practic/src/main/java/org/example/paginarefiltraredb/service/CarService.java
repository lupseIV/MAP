package org.example.paginarefiltraredb.service;

import javafx.application.Platform;
import org.example.paginarefiltraredb.domain.entities.Car;
import org.example.paginarefiltraredb.domain.entities.CarStatus;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.CarDbRepository;
import org.example.paginarefiltraredb.service.observer.ChangeEventType;
import org.example.paginarefiltraredb.service.observer.EntityChangeEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CarService extends BaseService<Long, Car> {

    private final CarDbRepository carRepository;
    private static final int ASYNC_DELAY_MS = 5000;

    public CarService(CarDbRepository repository, Validator<Car> validator) {
        super(repository, validator);
        this.carRepository = repository;
    }

    public List<Car> findCarsNeedingApproval() {
        return carRepository.findByStatus(CarStatus.NEEDS_APPROVAL);
    }

    public CompletableFuture<Boolean> submitForApproval(Long carId, String comments) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                Thread.sleep(ASYNC_DELAY_MS);

                Optional<Car> carOpt = findOne(carId);
                if (carOpt.isEmpty()) {
                    return false;
                }

                Car car = carOpt.get();
                CarStatus currentStatus = car.getStatus();


                if (currentStatus != CarStatus.NEW && currentStatus != CarStatus.REJECTED) {
                    return false;
                }

                car.setStatus(CarStatus.NEEDS_APPROVAL);
                car.setComments(comments);
                car.setRejectionReason(null);

                update(car);

                Platform.runLater(() -> {
                    notifyObservers(new EntityChangeEvent<>(ChangeEventType.UPDATE, car));
                });

                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> approveCar(Long carId) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                Thread.sleep(ASYNC_DELAY_MS);

                Optional<Car> carOpt = findOne(carId);
                if (carOpt.isEmpty()) {
                    return false;
                }

                Car car = carOpt.get();


                if (car.getStatus() != CarStatus.NEEDS_APPROVAL) {
                    return false;
                }

                car.setStatus(CarStatus.APPROVED);

                update(car);

                Platform.runLater(() -> {
                    notifyObservers(new EntityChangeEvent<>(ChangeEventType.UPDATE, car));
                });

                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> rejectCar(Long carId, String rejectionReason) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                Thread.sleep(ASYNC_DELAY_MS);

                Optional<Car> carOpt = findOne(carId);
                if (carOpt.isEmpty()) {
                    return false;
                }

                Car car = carOpt.get();

                if (car.getStatus() != CarStatus.NEEDS_APPROVAL) {
                    return false;
                }

                car.setStatus(CarStatus.REJECTED);
                car.setRejectionReason(rejectionReason);

                update(car);

                Platform.runLater(() -> {
                    notifyObservers(new EntityChangeEvent<>(ChangeEventType.UPDATE, car));
                });

                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        });
    }

    @Override
    public Car update(Car entity) {
        validator.validate(entity);
        return repository.update(entity);
    }
}
