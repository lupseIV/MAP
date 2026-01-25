package org.example.paginarefiltraredb.domain.validation.implementation;

import org.example.paginarefiltraredb.domain.entities.Client;
import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
import org.example.paginarefiltraredb.domain.validation.Validator;

public class ClientValidator implements Validator<Client> {
    @Override
    public void validate(Client entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Client entity cannot be null!");
        }

        String errors = "";

        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            errors += "Client Name cannot be empty.\n";
        }
        if (entity.getType() == null || entity.getType().trim().isEmpty()) {
            errors += "Client Type cannot be empty.\n";
        }
        if (entity.getBudget() == null || entity.getBudget() < 0) {
            errors += "Budget cannot be negative.\n";
        }
        if (entity.getRegistrationDate() == null) {
            errors += "Registration Date cannot be null.\n";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}