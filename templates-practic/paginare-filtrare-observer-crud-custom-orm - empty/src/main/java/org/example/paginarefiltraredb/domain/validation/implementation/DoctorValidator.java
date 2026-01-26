package org.example.paginarefiltraredb.domain.validation.implementation;

import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
import org.example.paginarefiltraredb.domain.validation.Validator;

public class DoctorValidator implements Validator<Doctor> {
    @Override
    public void validate(Doctor entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Doctor entity cannot be null!");
        }

        String errors = "";

        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            errors += " Name cannot be empty.\n";
        }
        if (entity.getSpecialty() == null || entity.getSpecialty().trim().isEmpty()) {
            errors += "Doctor specialty cannot be empty.\n";
        }


        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}