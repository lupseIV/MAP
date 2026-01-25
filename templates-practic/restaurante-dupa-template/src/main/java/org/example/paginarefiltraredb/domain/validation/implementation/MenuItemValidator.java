package org.example.paginarefiltraredb.domain.validation.implementation;

import org.example.paginarefiltraredb.domain.entities.MenuItem;
import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
import org.example.paginarefiltraredb.domain.validation.Validator;

public class MenuItemValidator implements Validator<MenuItem> {
    @Override
    public void validate(MenuItem entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("MenuItem entity cannot be null!");
        }

        String errors = "";

        if (entity.getCategory() == null || entity.getCategory().trim().isEmpty()) {
            errors += "Category cannot be empty.\n";
        }
        if (entity.getItem() == null || entity.getItem().trim().isEmpty()) {
            errors += "Item name cannot be empty.\n";
        }
        if (entity.getPrice() == null || entity.getPrice() < 0) {
            errors += "Price cannot be negative.\n";
        }
        if (entity.getCurrency() == null || entity.getCurrency().trim().isEmpty()) {
            errors += "Currency cannot be empty.\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
