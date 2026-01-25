package org.example.paginarefiltraredb.domain.validation.implementation;

import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
import org.example.paginarefiltraredb.domain.validation.Validator;

public class OrderValidator implements Validator<Order> {
    @Override
    public void validate(Order entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Order entity cannot be null!");
        }

        String errors = "";

        if (entity.getTableId() == null) {
            errors += "Table ID cannot be null.\n";
        }
        if (entity.getDate() == null) {
            errors += "Order date cannot be null.\n";
        }
        if (entity.getStatus() == null) {
            errors += "Order status cannot be null.\n";
        }
        if (entity.getMenuItemIds() == null || entity.getMenuItemIds().isEmpty()) {
            errors += "Order must contain at least one menu item.\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
