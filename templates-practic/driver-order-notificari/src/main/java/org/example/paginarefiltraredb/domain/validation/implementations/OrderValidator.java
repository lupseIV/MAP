package org.example.paginarefiltraredb.domain.validation.implementations;

import org.example.paginarefiltraredb.domain.entities.Order;
import org.example.paginarefiltraredb.domain.exceptions.ValidationException;
import org.example.paginarefiltraredb.domain.validation.Validator;

public class OrderValidator implements Validator<Order> {
    @Override
    public void validate(Order entity) throws ValidationException {
        StringBuilder sb = new StringBuilder();

        if(entity.getClientName().isEmpty() || entity.getClientName().isBlank()){
            sb.append("Numele clientului nu poate fi gol!\n");
        }
        if(entity.getPickUpAddress().isEmpty() || entity.getPickUpAddress().isBlank()){
            sb.append("Numele adresei nu poate fi gol!\n");
        }
        if(entity.getClientName().isEmpty() || entity.getClientName().isBlank()){
            sb.append("Numele clientului nu poate fi gol!\n");
        }
        if(entity.getDestinationAddress().isEmpty() || entity.getDestinationAddress().isBlank()){
            sb.append("Numele adresei nu poate fi gol!\n");
        }

        if(!sb.isEmpty()){
            throw new ValidationException(String.valueOf(sb));
        }
    }
}
