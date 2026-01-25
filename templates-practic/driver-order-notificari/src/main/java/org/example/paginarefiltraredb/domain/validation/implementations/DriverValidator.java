package org.example.paginarefiltraredb.domain.validation.implementations;

import org.example.paginarefiltraredb.domain.entities.Driver;
import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
import org.example.paginarefiltraredb.domain.validation.Validator;

public class DriverValidator implements Validator<Driver> {
    @Override
    public void validate(Driver entity) throws ValidationException {
        StringBuilder sb = new StringBuilder();
        if(entity.getName().isEmpty() || entity.getName().isBlank()){
            sb.append("Numele clientului nu poate fi gol!\n");
        }
        if(!sb.isEmpty()){
            throw new ValidationException(String.valueOf(sb));
        }
    }
}
