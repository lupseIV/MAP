package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.MenuItem;
import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.entities.OrderStatus;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.OrderDbRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService extends BaseService<Integer, Order> {

    private final OrderDbRepository orderRepository;

    public OrderService(OrderDbRepository repository, Validator<Order> validator) {
        super(repository, validator);
        this.orderRepository = repository;
    }

    public Order placeOrder(Integer tableId, List<MenuItem> selectedItems) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot place an order without selecting any products!");
        }

        Order order = new Order(tableId, LocalDateTime.now(), OrderStatus.PLACED);

        // Set menu item IDs
        List<Integer> menuItemIds = new ArrayList<>();
        for (MenuItem item : selectedItems) {
            menuItemIds.add(item.getId());
        }
        order.setMenuItemIds(menuItemIds);
        order.setMenuItems(selectedItems);

        return add(order);
    }

    public List<Order> getPlacedOrders() {
        return orderRepository.findByStatus(OrderStatus.PLACED);
    }

    public List<Order> getPreparingOrders() {
        return orderRepository.findByStatus(OrderStatus.PREPARING);
    }

    public void markAsPreparing(Order order) {
        order.setStatus(OrderStatus.PREPARING);
        update(order);
    }

    public void markAsServed(Order order) {
        order.setStatus(OrderStatus.SERVED);
        update(order);
    }
}
