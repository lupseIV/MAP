package org.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.service.DucksService;
import org.service.PersonsService;
import org.service.FriendshipService;
import org.service.UsersService;

import java.io.IOException;

public class MainController extends AbstractViewController{

    @FXML
    private StackPane contentArea;

    private DucksService ducksService;
    private PersonsService personsService;
    private FriendshipService friendshipService;
    private UsersService usersService;

    public void setServices(DucksService ds, PersonsService ps, FriendshipService fs, UsersService us) {
        this.ducksService = ds;
        this.personsService = ps;
        this.friendshipService = fs;
        this.usersService = us;
    }

    @FXML
    public void handleShowDucks() {
        loadView("DucksView.fxml", controller -> {
            if (controller instanceof DucksController) {
                ((DucksController) controller).setService(ducksService);
            }
        },  contentArea);
    }

    @FXML
    public void handleShowPersons() {
        loadView("PersonsView.fxml", controller -> {
            if (controller instanceof PersonsController) {
                ((PersonsController) controller).setService(personsService);
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
}