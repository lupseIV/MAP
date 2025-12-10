package org.service;

import org.domain.dtos.UserDTO;
import org.domain.exceptions.ServiceException;
import org.domain.users.User;
import org.repository.db.PostgresNotificationListener;
import org.utils.security.SecurityUtils;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class AuthService {
    private boolean loggedIn = false;
    private UsersService usersService;
    private User user;
    private PostgresNotificationListener notificationListener;
    private MessageService messageService;

    public AuthService(UsersService usersService) {
        this.usersService = usersService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void login(String email, String password) {
        if(email.trim().isEmpty() || password.trim().isEmpty()){
            throw new ServiceException("Email or password is empty");
        }

        Optional<User> user = StreamSupport.stream(usersService.findAll().spliterator(), false)
                .filter(u -> u.getEmail().equals(email) )
                .findFirst();

        if(user.isPresent()){
            if(SecurityUtils.checkPassword(password, user.get().getPassword())){
                loggedIn = true;
                this.user = user.get();

                startListening();
            }
        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public User getCurrentUser() {
        return user;
    }

    public void register(User user){
        String hashedPassword = SecurityUtils.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);

        boolean exists = StreamSupport.stream(usersService.findAll().spliterator(), false)
                .anyMatch(u -> u.getEmail().equals(user.getEmail()));

        if (exists) {
            throw new RuntimeException("Email already exists!");
        }

        usersService.save(user);
        this.user = user;
        loggedIn = true;

        startListening();
    }

    public void startListening(){
        notificationListener = new PostgresNotificationListener(user.getId());
        notificationListener.setMessageService(messageService);
        notificationListener. startListening();
    }

    public PostgresNotificationListener getNotificationListener() {
        return notificationListener;
    }
}
