package map.domain.validators;


import map.domain.exceptions.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}