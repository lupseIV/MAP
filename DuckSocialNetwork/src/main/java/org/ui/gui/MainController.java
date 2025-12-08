package org.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.domain.users.User;
import org.service.*;

import java.io.IOException;

public class MainController implements ViewController{

    @FXML private StackPane contentArea;

    private DucksService ducksService;
    private PersonsService personsService;
    private FriendshipService friendshipService;
    private UsersService usersService;
    private MessageService messageService;
    private AuthService authService;

    public void setServices(DucksService ds, PersonsService ps, FriendshipService fs, UsersService us,
                            AuthService as, MessageService ms) {
        this.ducksService = ds;
        this.personsService = ps;
        this.friendshipService = fs;
        this.usersService = us;
        this.authService = as;
        this.messageService = ms;
    }

    @FXML public void handleShowUsersView(){
        loadView("UsersView.fxml", controller -> {
            if (controller instanceof UsersController) {
                ((UsersController) controller).setService(ducksService, personsService,usersService,contentArea);
            }
        }, contentArea);
    }

    @FXML
    public void handleShowFriendships() {

        loadView("SocialView.fxml", controller -> {
            if (controller instanceof SocialController) {
                ((SocialController) controller).setService(friendshipService, usersService);
            }
        }, contentArea);
    }

    @FXML
    public void handleShowMessageView(){
        loadView("ChatView.fxml", controller ->  {
            if (controller instanceof ChatController) {
                ((ChatController) controller).setServices(messageService,authService,null);
            }
        }, contentArea);
    }
}