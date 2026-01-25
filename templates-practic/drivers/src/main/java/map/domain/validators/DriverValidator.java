package map.domain.validators;

import map.domain.Driver;
import map.domain.exceptions.ValidationException;

public class DriverValidator implements Validator<Driver>{
    @Override
    public void validate(Driver entity) {
        if(entity.getName() == null)
            throw new ValidationException("Driver name cannot be null");
    }
}
