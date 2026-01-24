package org.example.paginarefiltraredb.domain.validation;


import org.example.paginarefiltraredb.domain.exceptions.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}