package org.example.paginarefiltraredb.service;

import org.example.paginarefiltraredb.domain.entities.User;
import org.example.paginarefiltraredb.domain.validation.Validator;
import org.example.paginarefiltraredb.repository.database.implementations.UserDbRepository;

import java.util.Optional;

public class UserService extends BaseService<Long, User> {

    private final UserDbRepository userRepository;

    public UserService(UserDbRepository repository, Validator<User> validator) {
        super(repository, validator);
        this.userRepository = repository;
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
}
