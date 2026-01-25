package org.example.paginarefiltraredb.service.implementations;

import org.example.paginarefiltraredb.domain.entities.RestaurantTable;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.RestaurantTableDbRepository;
import org.example.paginarefiltraredb.service.BaseService;

import java.util.ArrayList;
import java.util.List;

public class RestaurantTableService extends BaseService<Integer, RestaurantTable> {

    public RestaurantTableService(RestaurantTableDbRepository repository, Validator<RestaurantTable> validator) {
        super(repository, validator);
    }

    public List<RestaurantTable> getAllTables() {
        List<RestaurantTable> tables = new ArrayList<>();
        findAll().forEach(tables::add);
        return tables;
    }
}
