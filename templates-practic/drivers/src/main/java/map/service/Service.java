package map.service;

import map.domain.Driver;
import map.domain.Order;
import map.domain.Status;
import map.repository.DriverRepository;
import map.repository.OrderRepository;
import map.utils.events.ChangeEventType;
import map.utils.events.EntityChangeEvent;
import map.utils.observer.Observable;
import map.utils.observer.Observer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Service implements Observable<EntityChangeEvent> {
    DriverRepository driverRepository;
    OrderRepository orderRepository;
    List<Observer<EntityChangeEvent>> observers;
    private int whatDriverToBeNotified = 0;

    public Service(DriverRepository driverRepository, OrderRepository orderRepository) {
        this.driverRepository = driverRepository;
        this.orderRepository = orderRepository;
        observers = new java.util.ArrayList<>();
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Order> getOrdersByDriver(Integer id) {
        return orderRepository.getOrdersByDriver(id);
    }

    @Override
    public void addObserver(Observer<EntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(EntityChangeEvent event) {
        observers.forEach(o -> o.update(event));
    }

    public void finishOrder(Order order) {
        Order new_order = order;
        new_order.setStatus(Status.FINISHED);
        new_order.setEndDate(java.time.LocalDateTime.now());
        orderRepository.update(new_order);

        notifyObservers(new EntityChangeEvent(ChangeEventType.UPDATE, new_order));
    }

    public void AddOrder(String plecare, String sosire, String client) {
        Order new_order = new Order(null, Status.PENDING, LocalDateTime.now(), null, plecare, sosire, client);

        orderRepository.save(new_order);
        new_order.setId(orderRepository.getLastInsertedId());

        notifyObservers(new EntityChangeEvent(ChangeEventType.ADD, new_order));
        whatDriverToBeNotified = 0;
    }

    public List<Driver> amITheOne() {
        List<Driver> drivers = driverRepository.findAll();;
        return drivers.stream()
                .filter(driver -> {
                    // Filter drivers with no active orders
                    List<Order> driverOrders = orderRepository.getOrdersByDriver(driver.getId());
                    return driverOrders.stream()
                            .noneMatch(order -> order.getStatus() == Status.IN_PROGRESS);
                })
                .sorted((d1, d2) -> {
                    // Sort by most recent idle time in descending order
                    LocalDateTime lastEndTimeD1 = orderRepository.getOrdersByDriver(d1.getId()).stream()
                            .filter(order -> order.getEndDate() != null)
                            .map(Order::getEndDate)
                            .max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.MIN);

                    LocalDateTime lastEndTimeD2 = orderRepository.getOrdersByDriver(d2.getId()).stream()
                            .filter(order -> order.getEndDate() != null)
                            .map(Order::getEndDate)
                            .max(LocalDateTime::compareTo)
                            .orElse(LocalDateTime.MIN);

                    return lastEndTimeD2.compareTo(lastEndTimeD1);
                })
                .toList();
    }
    public void incrementWhatDriverToBeNotified(Order order) {
        whatDriverToBeNotified++;
        notifyObservers(new EntityChangeEvent(ChangeEventType.INCREMENT, order));
    }
    public int getWhatDriverToBeNotified() {
        return whatDriverToBeNotified;
    }
    public void setWhatDriverToBeNotified(int whatDriverToBeNotified) {
        this.whatDriverToBeNotified = whatDriverToBeNotified;
    }

    public void AcceptOrder(Order data, Integer driverId) {
        data.setStatus(Status.IN_PROGRESS);
        data.setDriver(driverId);
        data.setStartDate(LocalDateTime.now());
        orderRepository.update(data);
        notifyObservers(new EntityChangeEvent(ChangeEventType.UPDATE, data));
    }
}
