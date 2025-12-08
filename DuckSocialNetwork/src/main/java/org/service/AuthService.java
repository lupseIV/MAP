package org.service;

import org.domain.dtos.UserDTO;
import org.domain.exceptions.ServiceException;
import org.domain.users.User;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class AuthService {
    private boolean loggedIn = false;
    private UsersService usersService;
    private User user;

    public AuthService(UsersService usersService) {
        this.usersService = usersService;
    }

    public void login(String email, String password) {
        if(email.trim().isEmpty() || password.trim().isEmpty()){
            throw new ServiceException("Email or password is empty");
        }

        Optional<User> user = StreamSupport.stream(usersService.findAll().spliterator(), false)
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst();

        if(user.isPresent()){
            loggedIn = true;
            this.user = user.get();
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void register(User user){
        Optional<User> u = StreamSupport.stream(usersService.findAll().spliterator(), false)
                .filter(u1 -> u1.getEmail().equals(user.getEmail()) && u1.getPassword().equals(user.getPassword()))
                .findFirst();
        if(u.isPresent()){
            throw  new ServiceException("User already exists");
        }
        this.user = usersService.save(user);
        loggedIn = true;
    }
}
