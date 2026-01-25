package map.domain.validators;

import map.domain.Order;
import map.domain.exceptions.ValidationException;

import java.util.Objects;

public class OrderValidator implements Validator<Order>{
    @Override
    public void validate(Order entity) throws ValidationException {
        if(Objects.equals(entity.getClientName(), ""))
            throw new ValidationException("Client name cannot be null");
        if(Objects.equals(entity.getPickUpAddress(), ""))
            throw new ValidationException("Pick up address cannot be null");
        if(Objects.equals(entity.getDestinationAddress(), ""))
            throw new ValidationException("Destination address cannot be null");
    }
}
