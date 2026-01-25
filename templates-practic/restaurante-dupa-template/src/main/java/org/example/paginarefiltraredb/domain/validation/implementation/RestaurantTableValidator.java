package org.example.paginarefiltraredb.domain.validation.implementation;

import org.example.paginarefiltraredb.domain.entities.RestaurantTable;
import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
import org.example.paginarefiltraredb.domain.validation.Validator;

public class RestaurantTableValidator implements Validator<RestaurantTable> {
    @Override
    public void validate(RestaurantTable entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Table entity cannot be null!");
        }
    }
}
