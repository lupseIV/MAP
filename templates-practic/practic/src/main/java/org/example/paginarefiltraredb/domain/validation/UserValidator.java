package org.example.paginarefiltraredb.domain.validation;

import org.example.paginarefiltraredb.domain.entities.User;
import org.example.paginarefiltraredb.domain.exceptions.ValidationException;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (entity.getUsername() == null || entity.getUsername().trim().isEmpty()) {
            errors.append("Username cannot be empty!\n");
        }

        if (entity.getPassword() == null || entity.getPassword().trim().isEmpty()) {
            errors.append("Password cannot be empty!\n");
        }

        if (entity.getRole() == null) {
            errors.append("Role cannot be null!\n");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}

