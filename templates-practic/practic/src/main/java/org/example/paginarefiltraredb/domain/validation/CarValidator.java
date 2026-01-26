package org.example.paginarefiltraredb.domain.validation;

import org.example.paginarefiltraredb.domain.entities.Car;
import org.example.paginarefiltraredb.domain.exceptions.ValidationException;

public class CarValidator implements Validator<Car> {
    @Override
    public void validate(Car entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            errors.append("Car name cannot be empty!\n");
        }

        if (entity.getBasePrice() == null || entity.getBasePrice() < 0) {
            errors.append("Base price must be a positive number!\n");
        }

        if (entity.getStatus() == null) {
            errors.append("Status cannot be null!\n");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}

