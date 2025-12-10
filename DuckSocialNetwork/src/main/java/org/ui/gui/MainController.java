package org.ui.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatPartnerView.fxml"));
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Select Users to Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(contentArea.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ChatPartnerController contr = loader.getController();
            contr.setServices(usersService, dialogStage);

            dialogStage.showAndWait();

            if (contr.getChatPartner() != null) {
                loadView("ChatView.fxml", controller ->  {
                    if (controller instanceof ChatController) {
                        ((ChatController) controller).setServices(messageService,authService, contr.getChatPartner(),authService.getNotificationListener());
                    }
                }, contentArea);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}